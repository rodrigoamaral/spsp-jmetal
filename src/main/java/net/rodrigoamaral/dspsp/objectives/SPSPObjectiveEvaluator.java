package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Encapsulates a collection of all objectives ({@link IObjective})
 * for a given SPSP formulation.
 *
 * @author Rodrigo Amaral
 *
 */
public class SPSPObjectiveEvaluator implements IObjectiveEvaluator {

    private List<IObjective> objectives;

    public SPSPObjectiveEvaluator() {
        objectives = new ArrayList<>();
    }

    @Override
    public IObjectiveEvaluator addObjective(IObjective objective) {
        objectives.add(objective);
        return this;
    }

    @Override
    public List<IObjective> getObjectives() {
        return objectives;
    }

    @Override
    public double evaluate(int index, DynamicProject project, DedicationMatrix solution) {
        return objectives.get(index).evaluate(project, solution);
    }

    @Override
    public int size() {
        return objectives.size();
    }
}
