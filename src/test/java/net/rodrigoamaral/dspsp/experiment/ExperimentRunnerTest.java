package net.rodrigoamaral.dspsp.experiment;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ExperimentRunnerTest {

    @Test
    public void testGetExperimentSettings() {
        ExperimentRunner runner = new ExperimentRunner(new ExperimentSettings());
        assertNotNull(runner.getExperimentSettings());
    }

}
