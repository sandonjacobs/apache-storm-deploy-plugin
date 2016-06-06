package org.jenkins.plugins.storm_topo_mgr.command;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.*;

public class KillContextTest {

    private KillContext context = new KillContext("host.com", 9876, 8888, "junit", 9);


    @Test
    public void testFindTopologyId() throws Exception {
        String fileName = "json/topo-summary.json";
        ClassLoader classLoader = getClass().getClassLoader();
        String summary = IOUtils.toString(classLoader.getResourceAsStream(fileName));

        String result = context.findTopologyId(summary);
        assertEquals("Topology ID does not match expected value...", "junit-1-1402960825", result);
    }

}