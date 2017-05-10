package net.rodrigoamaral.spsp;

import net.rodrigoamaral.spsp.adapters.JMetalSPSPAdapter;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.io.FileNotFoundException;

/**
 *
 * Wraps our SPSP model as a jMetal DoubleProblem.
 *
 * @author Rodrigo Amaral
 *
 */
//public class SPSProblem extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution>{
public class SPSProblem extends AbstractDoubleProblem {

    private JMetalSPSPAdapter spsp;
    public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints;
    private static final double PENALTY = 1.5;
    private int count;


    public SPSProblem(String projectPropertiesFileName) throws FileNotFoundException {
        spsp = new JMetalSPSPAdapter(projectPropertiesFileName);
        setName(spsp.getProblemName());
        setNumberOfVariables(spsp.getNumberOfVariables());
        setNumberOfObjectives(spsp.getNumberOfObjectives());
        setNumberOfConstraints(spsp.getNumberOfConstraints());
        setLowerLimit(spsp.getLowerLimit());
        setUpperLimit(spsp.getUpperLimit());
        overallConstraintViolationDegree = new OverallConstraintViolation<>();
        numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
        count = 0;
    }


    @Override
    public void evaluate(DoubleSolution solution) {
        for (int i = 0; i < spsp.getNumberOfObjectives(); i++){
            solution.setObjective(i, spsp.evaluateObjective(i, solution));
        }
        evaluateConstraints(solution);
        // Solutions with violated constraints are penalized
        if (numberOfViolatedConstraints.getAttribute(solution) > 0) {
            for (int i = 0; i < spsp.getNumberOfObjectives(); i++){
                solution.setObjective(i, solution.getObjective(i) * PENALTY);
            }
        }
        count++;
        System.out.println(count);
    }


    private void evaluateConstraints(DoubleSolution solution) {
        Integer violated = spsp.getNumberOfViolatedConstraints(solution);
        numberOfViolatedConstraints.setAttribute(solution, violated);
        overallConstraintViolationDegree.setAttribute(solution, Double.valueOf(violated));
    }

}
