package net.rodrigoamaral.dspsp.cli;

import net.rodrigoamaral.logging.SPSPLogger;
import org.apache.commons.cli.*;

/**
 * Provides a command line inteface to the program
 *
 * @author Rodrigo Amaral
 */
public class CLI {

    private String instanceInputFile;

    public CLI(String[] args) throws ParseException {

        Options options = new Options();
        options.addOption("h", "help", false, "Show this help");
        options.addOption("d", "debug", false, "Set debug logging level");
        options.addOption("i", "input", true, "Pass instance input file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        HelpFormatter formatter = new HelpFormatter();

        if (cmd.hasOption("h")) {
            formatter.printHelp("DSPSPRunner", options);
            System.exit(0);
        }

        if (cmd.hasOption("d")) {
            SPSPLogger.setDebugLevel(true);
        }

        if (cmd.hasOption("i")) {
            instanceInputFile = cmd.getOptionValue("i");
        } else {
            System.out.println("Instance input file missing.\n");
            formatter.printHelp("DSPSPRunner", options);
            System.exit(0);
        }

    }

    public String getInstanceInputFile() {
        return instanceInputFile;
    }
}
