package net.rodrigoamaral.dspsp.results;

import net.rodrigoamaral.dspsp.experiment.metrics.ResultNormalizer;
import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Outputs normalized objective values to file
 */
public class NormalizedSolutionListOutput {
    private final String objectivesFilename;
    private final FileOutputContext context;

    public NormalizedSolutionListOutput(String objectivesFilename) {
        this.objectivesFilename = objectivesFilename;
        context = new DefaultFileOutputContext(this.objectivesFilename.replace("OBJ", "NOB"));
    }

    public String getObjectivesFilename() {
        return objectivesFilename;
    }


    // REFACTOR: Customize separator
    public void print() throws IOException {
        ResultNormalizer rn = new ResultNormalizer();
        List<String> normalized = rn.normalize(getObjectivesFilename());

        if (normalized == null || normalized.isEmpty()) {
            String msg = "Could not generate file " + getObjectivesFilename() + " (normalized front is empty)." +
                    " Will generate dummy file instead.";
            SPSPLogger.warning(msg);
            writeDummyFile();
        } else {
            int numberOfObjectives = normalized.get(0).split(" ").length;

            BufferedWriter bufferedWriter = context.getFileWriter();

            try {
                if (normalized.size() > 0) {
                    for (int i = 0; i < normalized.size(); i++) {
                        String[] line = normalized.get(i).split(" ");
                        for (int j = 0; j < numberOfObjectives; j++) {
                            bufferedWriter.write(line[j] + "");
                            if (j < numberOfObjectives - 1) {
                                bufferedWriter.write(context.getSeparator());
                            }
                        }
                        bufferedWriter.newLine();
                    }
                }
                bufferedWriter.close();
            } catch (IOException e) {
                throw new JMetalException("Error printing normalized objecives to file: ", e);
            }
        }
    }

    private void writeDummyFile() throws IOException {
        BufferedWriter bufferedWriter = context.getFileWriter();
        bufferedWriter.write("1.099999 1.099999 1.099999 1.099999");
        bufferedWriter.close();
    }
}
