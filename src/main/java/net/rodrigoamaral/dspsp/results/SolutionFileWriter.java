package net.rodrigoamaral.dspsp.results;

import net.rodrigoamaral.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.util.List;

public class SolutionFileWriter {
    private static final String DEFAULT_SEPARATOR = ";";
    private static final String DEFAULT_EXT = ".csv";
    public static final String RESULTS_DIR = "results";

    private final List<DoubleSolution> population;
    private String separator;
    private String algorithmID;
    private String instanceID;
    private Integer reschedulingPoint;

    public SolutionFileWriter(final List<DoubleSolution> population) {
        this.population = population;
        this.separator = DEFAULT_SEPARATOR;
        createResultsDirectory();
    }

    private void createResultsDirectory() {
        File directory = new File(RESULTS_DIR);
        directory.mkdir();
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


    private String getFilename(String type) {
            StringBuilder sb = new StringBuilder();
            sb.append(RESULTS_DIR);
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
            if (reschedulingPoint != null) {
                sb.append("-");
                sb.append(reschedulingPoint);
            } else {
                sb.append("-");
                sb.append("STATIC");
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
        new SolutionListOutput(population)
                .setSeparator(separator)
                .setVarFileOutputContext(new DefaultFileOutputContext(getVariablesFilename()))
                .setFunFileOutputContext(new DefaultFileOutputContext(getObjectivesFilename()))
                .print();
    }
}
