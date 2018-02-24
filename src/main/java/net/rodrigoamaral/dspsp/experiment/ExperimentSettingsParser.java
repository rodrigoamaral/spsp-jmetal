package net.rodrigoamaral.dspsp.experiment;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Parses JSON files containing settings for DSPSP experiments
 *
 * @author Rodrigo Amaral
 */
public class ExperimentSettingsParser {

    public ExperimentSettings parse(String settingsFilename) {

        ObjectMapper mapper = new ObjectMapper();

        ExperimentSettings settings = null;

        try {
            settings = mapper.readValue(new File(settingsFilename), ExperimentSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return settings;
    }
}
