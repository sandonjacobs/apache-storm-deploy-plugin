package org.jenkins.plugins.storm_topo_mgr;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import org.jenkins.plugins.storm_topo_mgr.model.ClusterDefinition;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TopologyPlugin extends Builder {

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        System.out.println("stop here");
        return super.perform(build, launcher, listener);
    }

    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
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
