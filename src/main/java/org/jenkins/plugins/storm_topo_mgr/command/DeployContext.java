package org.jenkins.plugins.storm_topo_mgr.command;

import java.text.MessageFormat;

public class DeployContext extends CommandContext {

    private final String topologyClassName;
    private final String cliOptions;

    public DeployContext(String host, Integer port, String className, String options) {
        super(host, port, StormCommand.DEPLOY);
        this.topologyClassName = className;
        this.cliOptions = options;
    }

    protected String createCommand() {
        return MessageFormat.format("jar {0} {1} {2}", this.getNimbusHost(), this.getThriftPort(), this.topologyClassName, this.cliOptions);
    }

    @Override
    void execute() throws Exception {
        // create jar command with cliOptions
    }
}
