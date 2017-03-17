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
public class SPSProblem extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution>{

    private JMetalSPSPAdapter spsp;
    public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints;


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
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        for (int i = 0; i < spsp.getNumberOfObjectives(); i++){
            solution.setObjective(i, spsp.evaluateObjective(i, solution));
        }
    }

    @Override
    public void evaluateConstraints(DoubleSolution solution) {
        overallConstraintViolationDegree.setAttribute(solution, spsp.getConstraintViolationDegree(solution));
        numberOfViolatedConstraints.setAttribute(solution, spsp.getNumberOfViolatedConstraints(solution));
    }

}
