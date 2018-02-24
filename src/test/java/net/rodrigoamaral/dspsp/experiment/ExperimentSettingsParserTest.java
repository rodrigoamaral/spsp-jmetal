package net.rodrigoamaral.dspsp.experiment;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExperimentSettingsParserTest {

    private ExperimentSettingsParser parser;

    @Before
    public void setUp() {
        parser = new ExperimentSettingsParser();
    }

    @Test
    public void testParseInstances() {
        ExperimentSettings settings = parser.parse("project-conf/settings.json");
        assertEquals(18, settings.getInstanceFiles().size());
        assertEquals("dynamic_example_new1.json", settings.getInstanceFiles().get(0));
    }

    @Test
    public void testParseDebugMode() {
        ExperimentSettings settings = parser.parse("project-conf/settings.json");
        final boolean debugMode = settings.isDebugMode();
        assertNotNull(debugMode);
        if (debugMode) {
            assertEquals("true", Boolean.toString(debugMode));
        } else {
            assertEquals("false", Boolean.toString(debugMode));
        }
    }
}
