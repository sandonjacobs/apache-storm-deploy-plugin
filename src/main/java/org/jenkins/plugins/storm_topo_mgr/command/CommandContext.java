package org.jenkins.plugins.storm_topo_mgr.command;

public abstract class CommandContext {

    private final StormCommand command;
    private final String nimbusHost;
    private final Integer thriftPort;

    public CommandContext(String nimbusHostName, Integer nimbusThriftPort, StormCommand cmd) {
        this.command = cmd;
        this.nimbusHost = nimbusHostName;
        this.thriftPort = nimbusThriftPort;
    }

    public StormCommand getCommand() {
        return command;
    }

    public String getNimbusHost() {
        return nimbusHost;
    }

    public Integer getThriftPort() {
        return thriftPort;
    }

    abstract void execute() throws Exception;
}
