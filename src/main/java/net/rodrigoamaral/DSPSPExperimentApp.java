package net.rodrigoamaral;

import net.rodrigoamaral.dspsp.experiment.ExperimentCLI;
import net.rodrigoamaral.dspsp.experiment.ExperimentRunner;
import net.rodrigoamaral.logging.SPSPLogger;

/**
 * DSPSP experiment entry point.
 *
 * @author Rodrigo Amaral
 */
public class DSPSPExperimentApp {
    public static void main(String[] args) {
        ExperimentCLI cli = new ExperimentCLI(args);
        ExperimentRunner runner = new ExperimentRunner(cli.getExperimentSettings());
        runner.run();
    }
}
