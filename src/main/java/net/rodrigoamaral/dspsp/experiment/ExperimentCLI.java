package net.rodrigoamaral.dspsp.experiment;

import org.apache.commons.cli.*;

/**
 * Implements a command line interface (CLI) service for the
 * ExperimentRunner.
 *
 * @author Rodrigo Amaral
 */
public class ExperimentCLI {

    private static final String PROGRAM_NAME = "ExperimentRunner";
    private static final String DEFAULT_SETTINGS_FILE = "project-conf/settings.json";
    private final String[] args;
    private final ExperimentSettings experimentSettings;

    public ExperimentCLI(final String[] args) {
        this.args = args;
        String settingsFilename = DEFAULT_SETTINGS_FILE;

        Options options = new Options();

        options.addOption("h", "help", false, "Show this help");
        options.addOption("d", "debug", false, "Set debug logging level");
        options.addOption("s", "settings", true, "Path to experiment settings JSON file");

        CommandLineParser cmdParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;

        try {
            cmd = cmdParser.parse(options, this.args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (cmd.hasOption("h")) {
            formatter.printHelp(PROGRAM_NAME, options);
            System.exit(0);
        }

        if (cmd.hasOption("s")) {
            settingsFilename = cmd.getOptionValue("s");
        } else {
            System.out.println("Settings file missing.\n");
            formatter.printHelp(PROGRAM_NAME, options);
            System.exit(0);
        }

        final ExperimentSettingsParser parser = new ExperimentSettingsParser();
        this.experimentSettings = parser.parse(settingsFilename);

        // Command line overrides settings file attibute
        if (cmd.hasOption("d")) {
            experimentSettings.setDebugMode(true);
        }
    }

    public String[] getArgs() {
        return args;
    }

    public ExperimentSettings getExperimentSettings() {
        return this.experimentSettings;
    }
}
