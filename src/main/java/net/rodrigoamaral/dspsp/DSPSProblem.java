package net.rodrigoamaral.dspsp;

import net.rodrigoamaral.dspsp.adapters.JMetalDSPSPAdapter;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.io.FileNotFoundException;

/**
 *
 * Wraps MODPSP model as a jMetal DoubleProblem.
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
        dspsp.evaluateObjectives(solution);
    }

    public DynamicProject getProject() {
        return dspsp.getProject();
    }

    public String getInstanceDescription() {
        return getProject().getInstanceDescription();
    }
}
