package org.jenkins.plugins.storm_topo_mgr.command;

import java.text.MessageFormat;

public enum StormCommand {

    JAR("jar"), KILL("kill");

    private String value;

    StormCommand(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static StormCommand getEnum(String value) {
        for (StormCommand s : values()) {
            if (s.getValue().equalsIgnoreCase(value))
                return s;
        }
        throw new IllegalArgumentException(MessageFormat.format("{0} is not valid for type StormCommand", value));
    }
}
