package net.rodrigoamaral.dspsp.experiment;

import java.util.List;

/**
 * Experiment settings for DSPSP.
 *
 * Instances of this class are populated by the settings read from
 * a JSON file.
 *
 * @author Rodrigo Amaral
 */
@SuppressWarnings("unused")
public class ExperimentSettings {

    private List<String> instanceFiles;
    private List<String> algorithms;

    public List<String> getInstanceFiles() {
        return instanceFiles;
    }

    public void setInstanceFiles(List<String> instanceFiles) {
        this.instanceFiles = instanceFiles;
    }

    public List<String> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<String> algorithms) {
        this.algorithms = algorithms;
    }
}
