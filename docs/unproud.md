# The Storm Deployinator

```bash
!/usr/bin/env python -u
    
import json, requests, random, sys, os, time, paramiko
from optparse import OptionParser


topology_classes = {
  "mine": {"class": "com.somecompany.MyTopology", "project": "storm_2.10"},
  "yours": {"class": "com.somecompany.YourTopology", "project": "storm_2.10"},
  "theirs": {"class": "com.somecompany.TheirTopology", "project": "storm_2.10"},
}

nimbus_nodes = {
  "dev": "dev-storm",
  "qa": "qa-storm",
  "prod": "prod-storm",
}

def build_opt_parser():
  parser = OptionParser(usage="Usage: %prog [options]")
  environment = os.environ.get('ENVIRONMENT', 'qa')
  parser.add_option("-r", "--release", dest="release", help="Release Version", default=os.environ.get('RELEASE'))
  parser.add_option("-e", "--environment", dest="environment", help="Environment (qa)", default=environment)
  parser.add_option("-t", "--topology", dest="topology", help="Topology Name", default=os.environ.get('TOPOLOGY', False))
  parser.add_option("-p", "--project", dest="project", help="Project Name", default=os.environ.get('PROJECT', False))
  parser.add_option("-n", "--nimbus", dest="nimbus", help="Numbus Server", default=os.environ.get('NIMBUS', nimbus_nodes[environment]))
  parser.add_option("-d", "--debug", dest="debug", help="Enable debug printing.", action="store_true", default=os.environ.get('DEBUG', False))
  parser.add_option("-A", "--action", dest="action", help="Action (kill, deploy, re-deploy)", default=os.environ.get('ACTION', "redeploy"))
  parser.add_option("-w", "--waittime", dest="waittime", help="Kill Waittime", default=os.environ.get('WAITTIME', 30))
  parser.add_option("-j", "--jvm-args", dest="jvm_args", help="STORM_JAR_JVM_OPTS to pass to storm", default=os.environ.get('STORM_OPTS', "STORM_JAR_JVM_OPTS='-Dconfig.file=/opt/config/typesafe_overrides.conf'"))
  return parser

def getAPI(url):
  try:
    resp  = requests.get(url=url, params="", verify=False)
    return json.loads(resp.text)
  except:
    return False

def postAPI(url, params):
  try:
    resp  = requests.post(url=url, data=params, verify=False)
    return json.loads(resp.text)
  except Exception, e:
    return False

def getSummary(options=None):
  return getAPI("http://%s:8080/api/v1/cluster/summary" % (options.nimbus))


def getTopology(id=None, options=None):
  if id and options:
    return getAPI("http://%s:8080/api/v1/topology/%s?window=600" % (options.nimbus, id))
  else:
    return {}

def getTopologies(options=None):
  topos = {}
  sum = getAPI("http://%s:8080/api/v1/topology/summary" % (options.nimbus))
  if sum:
    for topo in sum['topologies']:
      topos[topo['name']]  = getTopology(topo['id'], options)
  return topos

def getMavenJar(options=None, client=None, topo=None):
  maven_url = "http://my-nexus.me.com:8081/nexus/content/repositories/releases/com/somecompany/%s/%s/%s-%s-assembly.jar" % (topology_classes[topo]['project'], options.release, topology_classes[topo]['project'], options.release)
  jar_path = "/opt/storm/deploy/%s-%s.jar" % (topology_classes[topo]['project'], options.release)
  wget_cmd = "/usr/bin/wget -O %s %s" % (jar_path, maven_url)

  print "\nPulling JAR from Maven..."
  if options.debug: print "Running: 'wget_cmd'"% (maven_url, options.nimbus)
  stdin, stdout, stderr = client.exec_command(wget_cmd)
  success = False

  for line in stderr.read().splitlines():
    if "saved" in line:
      print "\t> %s" % (line)
      success = True

  if not success:
    print "Getting the JAR appears to have failed for release %s, STDERR contains:" % (options.release)
    for line in stderr.read().splitlines():
      print "\t> %s" % (line)
    return False
  else:
    return jar_path

def getSSHClient(options=None):
  pemfile = open('/var/lib/jenkins/.ssh/qa.pem', 'r')
  pemstring = pemfile.read()
  import StringIO
  keyfile = StringIO.StringIO(pemstring)
  mykey = paramiko.RSAKey.from_private_key(keyfile)
  client = paramiko.SSHClient()
  client.load_system_host_keys()
  client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
  client.connect(options.nimbus, username='root', pkey=mykey)
  return client

def deployTopology(name=None, options=None, jar=None, client=None):
  if jar and client:
    storm_cmd = "%s /opt/storm/bin/storm jar %s " % (options.jvm_args, jar[topology_classes[name]['project']])
    if 'method' in topology_classes[name]:
      if topology_classes[name]['method'] == 0: storm_cmd += "%s %s remote %s" % (topology_classes[name]['class'], topology_classes[name]['sub'], options.environment)
      if topology_classes[name]['method'] == 1: storm_cmd += "%s -t %s -m remote -e %s" % (topology_classes[name]['class'], topology_classes[name]['sub'], options.environment)
    else:
      storm_cmd += "%s remote %s" % (topology_classes[name]['class'], options.environment)
    print "\nRunning deploy in nimbus node (%s)..." % (options.nimbus)
    if options.debug: print "Running: '%s'" % (storm_cmd)
    stdin, stdout, stderr = client.exec_command(storm_cmd)
    success = False
    for line in stdout.read().splitlines():
      if "Finished submitting topology" in line:
        print "\t> %s" % (line)
        success = True
    if not success:
      print "Redeploy Appears to have failed for %s, STDERR contains:" % (options.release)
      for line in stderr.read().splitlines():
        print "\t> %s" % (line)
      return False
    else:
      if options.environment in replay_machine:
        try:
          params = {
            'version': options.release,
            'topo_name': name,
            'url': "http://maven-repo:8081/nexus/content/repositories/releases/com/somecompany/%s/%s/%s-%s-assembly.jar" % (topology_classes[name]['project'], options.release, topology_classes[name]['project'], options.release),
          }
          version_update = postAPI(
            url="https://%s/topology/version" % (replay_machine[options.environment]), 
            params=params
          )
        except Exception, e:
          print "\nWas Unable to update the Replay Machine's version."
          print str(e)
          pass
        else:
          if version_update:
            print "\nUpdated the Replay Machine's version for %s to %s" % (name, options.release)
            print "\t> %s" % (version_update)
          else:
            print "\nWas Unable to update the Replay Machine's version."
      return True
  else:
    return False

def killTopology(id=None, options=None):
  if id and options:
    print "\nRunning kill in nimbus node (%s)..." % (options.nimbus)
    not_finished = True
    kill_data = postAPI("http://%s:8080/api/v1/topology/%s/kill/%s" % (options.nimbus, id, options.waittime), {})
    if kill_data['status'] == "KILLED":
      while not_finished:
        top_status = getTopology(id, options)
        if 'status' in top_status:
          print "\tTopology killed, waiting for it to clear the nimbus..."
          time.sleep(5)
        else:
          print "\tTopology has been completely killed."
          not_finished = False
      return True
    else:
      return False
  else:
    return False

def main():
  parser = build_opt_parser()
  (options, args) = parser.parse_args()
  topos = getTopologies(options)
  run_topos = []
  jar = {}
  client = False
  return_code = 0

  if options.topology:
    if options.topology in topology_classes:
      run_topos.append(options.topology)
    elif options.topology == 'all':
      run_topos = topology_classes.keys()
    else:
      print "%s is not a known topology." % (options.topology)
      sys.exit(2)

  for topo in run_topos:
    if options.project != topology_classes[topo]['project']:
      print "Skipping %s because it is of project type %s." % (topo, topology_classes[topo]['project'])
      continue
    if options.action == 'kill':
      if topo in topos:
        topo_id = topos[topo]['id']
      else:
        print "%s does not appear to be deployed." % (topo)
        return_code = 2
        continue
      print "\nAttempting to kill %s..." % (topo)
      if not killTopology(topo_id, options):
        print "Unable to kill topology."
        return_code = 2
        continue
    elif options.action == 'deploy':
      print "\nAttempting to deploy %s..." % (topo)
      if not client: client = getSSHClient(options)
      if topology_classes[topo]['project'] not in jar: jar[topology_classes[topo]['project']] = getMavenJar(options, client, topo)
      if not deployTopology(topo, options, jar, client):
        print "Unable to deploy topology."
        return_code = 2
        continue
    elif options.action == 'redeploy':
      skip_kill = False
      print "\nAttempting to redeploy %s..." % (topo)
      if topo in topos:
        topo_id = topos[topo]['id']
      else:
        print "\t%s does not appear to be deployed, skipping kill." % (topo)
        skip_kill = True
      if not client: client = getSSHClient(options)
      if topology_classes[topo]['project'] not in jar: jar[topology_classes[topo]['project']] = getMavenJar(options, client, topo)
      if skip_kill:
        if not deployTopology(topo, options, jar, client):
          print "Unable to deploy topology."
          return_code = 2
          continue
      else:
        if killTopology(topo_id, options):
          if not deployTopology(topo, options, jar, client):
            print "Unable to deploy topology."
            return_code = 2
            continue
        else:
          print "Unable to kill topology."
          return_code = 2
          continue
    else:
      print "%s is not a valid action." % (options.action)
      sys.exit(2)

  if return_code == 0:
    print "\nCompleted Successfully.\n"
  else:
    print "\nOne or more actions failed, check logs for details.\n"
  sys.exit(return_code)

if __name__ == "__main__":
    main()
    
```