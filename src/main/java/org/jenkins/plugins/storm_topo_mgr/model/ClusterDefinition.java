package org.jenkins.plugins.storm_topo_mgr.model;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

public class ClusterDefinition extends AbstractDescribableImpl<ClusterDefinition> {

    private final String name;
    private final String nimbusHost;
    private final String thriftPort;
    private final String version;

    @DataBoundConstructor
    public ClusterDefinition(String name, String nimbusHost, String thriftPort, String version) {
        this.name = name;
        this.nimbusHost = nimbusHost;
        this.thriftPort = thriftPort;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getNimbusHost() {
        return nimbusHost;
    }

    public String getThriftPort() {
        return thriftPort;
    }

    public String getVersion() {
        return version;
    }

    @Extension
    public static class ClusterDefinitionDescriptor extends Descriptor<ClusterDefinition> {

        // TODO Add FormValidation methods here for host, port, and version...

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Storm Cluster Defintiion";
        }
    }
}
