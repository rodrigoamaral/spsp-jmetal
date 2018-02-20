package net.rodrigoamaral.dspsp;

import net.rodrigoamaral.dspsp.adapters.JMetalDSPSPAdapter;
import net.rodrigoamaral.dspsp.project.DynamicProject;
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
public class DSPSProblem extends AbstractDoubleProblem {

    private JMetalDSPSPAdapter dspsp;

    public DSPSProblem(String projectPropertiesFileName) throws FileNotFoundException {
        dspsp = new JMetalDSPSPAdapter(projectPropertiesFileName);
        init();
    }

    public DSPSProblem(DynamicProject project) {
        dspsp = new JMetalDSPSPAdapter(project);
        init();
    }

    private void init() {
        setName(dspsp.getProblemName());
        setNumberOfVariables(dspsp.getNumberOfVariables());
        setNumberOfObjectives(dspsp.getNumberOfObjectives());
        setNumberOfConstraints(dspsp.getNumberOfConstraints());
        setLowerLimit(dspsp.getLowerLimit());
        setUpperLimit(dspsp.getUpperLimit());
    }

    @Override
    public void evaluate(DoubleSolution solution) {
//        long start = System.currentTimeMillis();
        dspsp.evaluateObjectives(solution);
//        System.out.println(System.currentTimeMillis() - start);
    }

    public DynamicProject getProject() {
        return dspsp.getProject();
    }
}
