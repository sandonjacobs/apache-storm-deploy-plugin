package org.jenkins.plugins.storm_topo_mgr;

import hudson.Extension;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Descriptor;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jenkins.plugins.storm_topo_mgr.model.ClusterDefinition;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

import static jenkins.model.Jenkins.XSTREAM2;

@Extension
public class TopoMgrGlobalConfig extends GlobalConfiguration {

    private List<ClusterDefinition> clusterDefinitions = new ArrayList<>();

    public TopoMgrGlobalConfig() {
        load();
    }

    @Initializer(before = InitMilestone.PLUGINS_STARTED)
    public static void xStreamCompatibility() {
        XSTREAM2.addCompatibilityAlias("org.jenkins.plugins.storm_topo_mgr.TopologyPlugin$DescriptorImpl", TopoMgrGlobalConfig.class);
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    public static TopoMgrGlobalConfig get() {
        return GlobalConfiguration.all().get(TopoMgrGlobalConfig.class);
    }

    public List<ClusterDefinition> getClusterDefinitions() {
        return clusterDefinitions;
    }

    public void setClusterDefinitions(List<ClusterDefinition> clusterDefinitions) {
        this.clusterDefinitions = clusterDefinitions;
    }

    public ClusterDefinition getClusterDefinition(String key) {
        for (ClusterDefinition d : getClusterDefinitions()) {
            if (d.getName().equals(key))
                return d;
        }
        return null;
    }
}
