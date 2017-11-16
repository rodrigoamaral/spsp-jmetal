package net.rodrigoamaral.dspsp;

import net.rodrigoamaral.dspsp.adapters.JMetalDSPSPAdapter;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.io.FileNotFoundException;

/**
 *
 * Wraps our MODPSP model as a jMetal DoubleProblem.
 *
 * @author Rodrigo Amaral
 *
 */
//public class SPSProblem extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution>{
public class DSPSProblem extends AbstractDoubleProblem {

    private JMetalDSPSPAdapter dspsp;
    public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints;
    private static final double PENALTY = 1.5;
    private int evaluationCounter;


    public DSPSProblem(String projectPropertiesFileName) throws FileNotFoundException {
        dspsp = new JMetalDSPSPAdapter(projectPropertiesFileName);
        setName(dspsp.getProblemName());
        setNumberOfVariables(dspsp.getNumberOfVariables());
        setNumberOfObjectives(dspsp.getNumberOfObjectives());
        setNumberOfConstraints(dspsp.getNumberOfConstraints());
        setLowerLimit(dspsp.getLowerLimit());
        setUpperLimit(dspsp.getUpperLimit());
        overallConstraintViolationDegree = new OverallConstraintViolation<>();
        numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
        evaluationCounter = 0;
    }


    @Override
    public void evaluate(DoubleSolution solution) {
        for (int i = 0; i < dspsp.getNumberOfObjectives(); i++){
            solution.setObjective(i, dspsp.evaluateObjective(i, solution));
        }
        evaluateConstraints(solution);
        // Solutions with violated constraints are penalized
        if (numberOfViolatedConstraints.getAttribute(solution) > 0) {
            for (int i = 0; i < dspsp.getNumberOfObjectives(); i++){
                solution.setObjective(i, solution.getObjective(i) * PENALTY);
            }
        }
        evaluationCounter++;
    }


    private void evaluateConstraints(DoubleSolution solution) {
        Integer violated = dspsp.getNumberOfViolatedConstraints(solution);
        numberOfViolatedConstraints.setAttribute(solution, violated);
        overallConstraintViolationDegree.setAttribute(solution, Double.valueOf(violated));
    }

    public int getEvaluationCounter() {
        return evaluationCounter;
    }
}
