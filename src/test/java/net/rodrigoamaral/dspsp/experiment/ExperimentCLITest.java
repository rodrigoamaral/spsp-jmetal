package net.rodrigoamaral.dspsp.experiment;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ExperimentCLITest {

    private String[] DEFAULT_TEST_ARGS;

    @Before
    public void setUp() {
        DEFAULT_TEST_ARGS = new String[]{"-s", "project-conf/settings.json"};
    }

    @Test
    public void testGetArgs() {
        ExperimentCLI cli = new ExperimentCLI(DEFAULT_TEST_ARGS);
        assertNotNull(cli.getArgs());
    }

    @Test
    public void testGetExperimentSettings() {
        ExperimentCLI cli = new ExperimentCLI(DEFAULT_TEST_ARGS);
        assertNotNull(cli.getExperimentSettings());
    }
}
