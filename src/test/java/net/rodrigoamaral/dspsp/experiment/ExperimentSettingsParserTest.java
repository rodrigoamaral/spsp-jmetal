package net.rodrigoamaral.dspsp.experiment;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExperimentSettingsParserTest {

    private ExperimentSettingsParser parser;

    @Before
    public void setUp() {
        parser = new ExperimentSettingsParser();
    }

    @Test
    public void testParseInstances() {
        ExperimentSettings settings = parser.parse("project-conf/settings-complete.json");
        assertEquals(18, settings.getInstanceFiles().size());
        assertEquals("project-conf/dynamic/dynamic_example_new1.json", settings.getInstanceFiles().get(0));
    }
}
