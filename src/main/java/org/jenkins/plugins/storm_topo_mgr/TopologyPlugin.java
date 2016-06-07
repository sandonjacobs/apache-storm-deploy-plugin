package org.jenkins.plugins.storm_topo_mgr;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import org.jenkins.plugins.storm_topo_mgr.command.StormCommand;
import org.jenkins.plugins.storm_topo_mgr.model.ClusterDefinition;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TopologyPlugin extends Builder {

    private StormCommand action = DescriptorImpl.action;
    private @Nonnull String topologyName;
    private String topologyClass;
    private @Nonnull String clusterDefinition;

    @SuppressWarnings("unused")
    @DataBoundConstructor
    public TopologyPlugin(@Nonnull String topologyName, @Nonnull String clusterDefinition) {
        this.topologyName = topologyName;
        this.clusterDefinition = clusterDefinition;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setAction(@Nonnull StormCommand action) {
        this.action = action;
    }

    @SuppressWarnings("unused")
    @DataBoundSetter
    public void setTopologyClass(String topologyClass) {
        this.topologyClass = topologyClass;
    }

    @SuppressWarnings("unused")
    @Nonnull
    public StormCommand getAction() {
        return action;
    }

    @SuppressWarnings("unused")
    @Nonnull
    public String getTopologyName() {
        return topologyName;
    }

    @SuppressWarnings("unused")
    public String getTopologyClass() {
        return topologyClass;
    }

    @SuppressWarnings("unused")
    @Nonnull
    public String getClusterDefinition() {
        return clusterDefinition;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // TODO perform something here...
        return super.perform(build, launcher, listener);
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public static final StormCommand action = StormCommand.DEPLOY;

        public DescriptorImpl() {
             load();
         }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Manage Storm Topology";
        }

        public ListBoxModel doFillActionItems() { return StormCommand.getFillItems(); }

        @SuppressWarnings("unused")
        public ListBoxModel doFillClusterDefinitions() {
            return fillClusterDefinitions();
        }

        public static ListBoxModel fillClusterDefinitions() {
            ListBoxModel items = new ListBoxModel();
            items.add("");
            for (ClusterDefinition c : TopoMgrGlobalConfig.get().getClusterDefinitions()) {
                items.add(c.getName());
            }
            return items;
        }
    }
}
