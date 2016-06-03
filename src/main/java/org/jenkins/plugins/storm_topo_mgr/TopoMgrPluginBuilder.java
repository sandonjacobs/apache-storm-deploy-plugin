package org.jenkins.plugins.storm_topo_mgr;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class TopoMgrPluginBuilder extends Builder {


    public static final class StormEnvironment extends ToolInstallation implements
            EnvironmentSpecific<StormEnvironment>, NodeSpecific<StormEnvironment>, Serializable {

        public StormEnvironment(String name, String nimbusHost, String version, List<? extends ToolProperty<?>> properties) {
            super(name, "", properties);

        }

        @Override
        public StormEnvironment forEnvironment(EnvVars environment) {
            return null;
        }

        @Override
        public StormEnvironment forNode(@NonNull Node node, TaskListener log) throws IOException, InterruptedException {
            return null;
        }
    }

}
