package net.rodrigoamaral.dspsp.experiment.metrics;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates files with the normalized values of objective functions
 */
public class ResultNormalizer {

    public static void main(String[] args) throws FileNotFoundException {
        ResultNormalizer rn = new ResultNormalizer();
        List<String> normalized = rn.normalize(args[0]);
        for (String s: normalized) {
            System.out.println(s);
        }
    }

    public List<String> normalize(String resultsPath) throws FileNotFoundException {
        return normalize(resultsPath, resultsPath);
    }

    public List<String> normalize(String resultsPath, String referencePath) throws FileNotFoundException {
        try {
            ArrayFront resultsFront = new ArrayFront(resultsPath);
            ArrayFront referenceFront = new ArrayFront(referencePath);
            FrontNormalizer fn = new FrontNormalizer(referenceFront);
            Front normalizedFront = fn.normalize(resultsFront);
            List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront);

            List<String> normString = new ArrayList<>();

            for (int i = 0; i < normalizedPopulation.size(); i++) {
                StringBuilder sb = new StringBuilder();
                for (int obj = 0; obj < normalizedPopulation.get(i).getNumberOfObjectives(); obj++) {
                    sb.append(normalizedPopulation.get(i).getObjective(obj)).append(" ");
                }
                normString.add(sb.toString());
            }

            return normString;

        } catch (JMetalException jem) {
            jem.printStackTrace();
            return new ArrayList<>();
        }
    }
}
