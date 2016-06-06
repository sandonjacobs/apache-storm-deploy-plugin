package org.jenkins.plugins.storm_topo_mgr.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class KillContext extends CommandContext {

    private static Logger logger = LoggerFactory.getLogger(KillContext.class);

    private final Integer httpPort;
    private final String topologyName;
    private final Integer waitTime;

    private static final String _URI_ROOT = "api/v1/topology/";
    private static final String SUMMARY_URI = _URI_ROOT + "summary";
    private static final String KILL_URI = _URI_ROOT + "kill/";

    public KillContext(String nimbusHostName, Integer nimbusThriftPort, Integer nimbusHttpPort, String name, Integer waitTimeSecs) {
        super(nimbusHostName, nimbusThriftPort, StormCommand.KILL);
        this.topologyName = name;
        this.httpPort = nimbusHttpPort;
        this.waitTime = waitTimeSecs;
    }

    public String getTopologyName() {
        return topologyName;
    }

    @Override
    void execute() throws Exception {
        final String statusUrl = createStatusUrl();
        final String summary = getTopologySummary(statusUrl);
        final String topoID = findTopologyId(summary);
        if (topoID == null) {
            throw new IllegalArgumentException(String.format("", this.topologyName));
        }
        this.kill(topoID);
    }

    protected String createStatusUrl() {
        return String.format("http://%s:%d/%s", this.getNimbusHost(), this.httpPort, SUMMARY_URI);
    }

    protected String createKillUrl(String id) {
        return String.format("http://%s:%d/%s/%s/%d", this.getNimbusHost(), this.httpPort, id, KILL_URI, this.waitTime);
    }

    protected String getTopologySummary(String urlString) {

        BufferedReader rd;
        OutputStreamWriter wr;

        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();

            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            StringBuilder sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            // TODO log with details
            e.printStackTrace();
            return null;
        }
    }

    private ObjectMapper mapper = new ObjectMapper();

    public String findTopologyId(String jsonSummary) throws IOException {
        Map<String, Object> summary = mapper.readValue(jsonSummary, Map.class);
        List<Map<String, Object>> topos = (List<Map<String, Object>>)summary.get("topologies");
        for (Map<String, Object> m : topos) {
            String name = m.get("name").toString();
            if (name.equalsIgnoreCase(this.topologyName))
                return m.get("id").toString();
        }
        return null;
    }


    private Integer kill(String id) throws Exception {
        PostMethod post = new PostMethod(createKillUrl(id));
        try {
            return post.execute(new HttpState(), new HttpConnection(this.getNimbusHost(), this.httpPort));
        }
        finally {
            post.releaseConnection();
        }
    }
}
