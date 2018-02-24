package net.rodrigoamaral.dspsp.experiment;

import java.util.List;

/**
 * Experiment settings for DSPSP.
 *
 * @author Rodrigo Amaral
 */
public class ExperimentSettings {

    private List<String> instanceFiles;
    private boolean debugMode;

    public List<String> getInstanceFiles() {
        return instanceFiles;
    }

    public void setInstanceFiles(List<String> instanceFiles) {
        this.instanceFiles = instanceFiles;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
