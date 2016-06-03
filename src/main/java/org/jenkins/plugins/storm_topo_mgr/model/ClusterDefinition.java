package org.jenkins.plugins.storm_topo_mgr.model;

import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;

public class ClusterDefinition extends AbstractDescribableImpl<ClusterDefinition> {

    private final String nimbusHost;
    private final Integer thiftPort;
    private final String version;

    @DataBoundConstructor
    public ClusterDefinition(String nimbus, String port, String stormVersion) {
        this.nimbusHost = nimbus;
        this.thiftPort = Integer.parseInt(port);
        this.version = stormVersion;
    }

    public String getNimbusHost() {
        return nimbusHost;
    }

    public Integer getThiftPort() {
        return thiftPort;
    }

    public String getVersion() {
        return version;
    }

    public static class ClusterDefinitionDescriptor extends Descriptor<ClusterDefinition> {

        // TODO Add FormValidation methods here for host, port, and version...

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Storm Cluster Defintiion";
        }
    }
}
