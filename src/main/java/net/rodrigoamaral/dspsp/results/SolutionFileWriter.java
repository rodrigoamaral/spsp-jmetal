package net.rodrigoamaral.dspsp.results;

import net.rodrigoamaral.jmetal.util.fileoutput.SolutionListOutput;
import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.util.List;

public class SolutionFileWriter {
    private static final String DEFAULT_SEPARATOR = ";";
    private static final String DEFAULT_EXT = ".csv";
    public static final String BASE_RESULTS_DIR = "results";

    private final List<DoubleSolution> population;
    private String separator;
    private String algorithmID;
    private String instanceID;
    private Integer reschedulingPoint;
    private Integer runNumber;
    private String resultsPath;

    public SolutionFileWriter(final List<DoubleSolution> population) {
        this.population = population;
        this.separator = DEFAULT_SEPARATOR;
        this.resultsPath = BASE_RESULTS_DIR;
    }

    private boolean createResultsDirectory(String path) {

        File objDir = new File(path + "/OBJ");
        File varDir = new File(path + "/VAR");
        boolean createdObj = objDir.exists() || objDir.mkdirs();
        boolean createdVar = varDir.exists() || varDir.mkdirs();

        return createdObj && createdVar;
    }

    private String buildResultsPath() {
        StringBuilder sb = new StringBuilder(this.resultsPath);
        sb.append("/");
        sb.append(this.algorithmID);
        sb.append("/");
        sb.append(this.instanceID);
        if (this.runNumber != null) {
            sb.append("/");
            sb.append(this.runNumber);
        }
        this.resultsPath = sb.toString();
        return this.resultsPath;
    }

    public SolutionFileWriter setAlgorithmID(final String algorithmID) {
        this.algorithmID = algorithmID;
        return this;
    }

    public SolutionFileWriter setInstanceID(final String instanceID) {
        this.instanceID = instanceID;
        return this;
    }

    public SolutionFileWriter setSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    public SolutionFileWriter setReschedulingPoint(int reschedulingPoint) {
        this.reschedulingPoint = reschedulingPoint;
        return this;
    }

    public SolutionFileWriter setRunNumber(Integer runNumber) {
        this.runNumber = runNumber;
        return this;
    }

    private String getFilename(String type) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.resultsPath);
            sb.append("/");
            sb.append(type);
            sb.append("/");
            sb.append(type);
            if (algorithmID != null) {
                sb.append("-");
                sb.append(algorithmID.toUpperCase());
            }
            if (instanceID != null) {
                sb.append("-");
                sb.append(instanceID.toUpperCase());
            }
            if (runNumber != null) {
                sb.append("-");
                sb.append(runNumber);
            }
            if (reschedulingPoint != null) {
                sb.append("-");
                sb.append(reschedulingPoint);
            } else {
                sb.append("-");
                sb.append(0);
            }

            sb.append(DEFAULT_EXT);
            return sb.toString();
    }

    public String getVariablesFilename() {
        return getFilename("VAR");
    }

    public String getObjectivesFilename() {
        return getFilename("OBJ");
    }

    public void write() {
        String path = buildResultsPath();
        boolean created = createResultsDirectory(path);
        if (!created) {
            SPSPLogger.warning("Unable to create results directory (" + this.resultsPath + ")");
        }
        new SolutionListOutput(population)
                .setSeparator(separator)
                .setVarFileOutputContext(new DefaultFileOutputContext(getVariablesFilename()))
                .setFunFileOutputContext(new DefaultFileOutputContext(getObjectivesFilename()))
                .print();
    }
}
