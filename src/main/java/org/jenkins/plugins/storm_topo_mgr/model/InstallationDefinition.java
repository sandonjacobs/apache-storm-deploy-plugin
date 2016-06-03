package org.jenkins.plugins.storm_topo_mgr.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Information about a specific installation of Storm on an node in the Jenkins cluster.
 */
public class InstallationDefinition extends ToolInstallation
        implements EnvironmentSpecific<InstallationDefinition>, NodeSpecific<InstallationDefinition>, Serializable {

    private String stormVersion;

    @DataBoundConstructor
    public InstallationDefinition(String name, String home, String version, List<? extends ToolProperty<?>> properties) {
        super(name, launderHomePath(home), properties);
        this.stormVersion = version;
    }

    private static String launderHomePath(String h) {
        if (h.endsWith("/") || h.endsWith("\\")) {
            // see https://issues.apache.org/bugzilla/show_bug.cgi?id=26947
            // Ant doesn't like the trailing slash, especially on Windows
            return h.substring(0, h.length() - 1);
        } else {
            return h;
        }
    }

    public String getStormVersion() {
        return stormVersion;
    }

    @Override
    public InstallationDefinition forEnvironment(EnvVars environment) {
        return new InstallationDefinition(getName(), environment.expand(getHome()), stormVersion, getProperties().toList());
    }

    @Override
    public InstallationDefinition forNode(@NonNull Node node, TaskListener log) throws IOException, InterruptedException {
        return new InstallationDefinition(getName(), translateFor(node, log), stormVersion, getProperties().toList());
    }

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<InstallationDefinition> {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Storm Installation Info";
        }
    }
}
