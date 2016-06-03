package org.jenkins.plugins.storm_topo_mgr.model;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

/**
 * Encapsulate the characteristics of a Storm Cluster, in particular the attributes needed to interact with the Nimbus in that cluster.
 *
 */
public class ClusterDefinition extends AbstractDescribableImpl<ClusterDefinition> {

    /**
     * logical name of the cluster
     *
     * might use this to describe environments, like dev, load, prod, etc...
     */
    private final String name;
    /**
     * fully-qualified host name or IP address of the nimbus
     */
    private final String nimbusHost;
    /**
     * thrift port on the nimbus, used in jar, kill, (de)activate commands to the cluster
     */
    private final String thriftPort;
    /**
     * the version of Apache Storm on this cluster
     */
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
