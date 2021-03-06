package net.rodrigoamaral.spsp.objectives;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

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
    public double evaluate(int index, Project project, DedicationMatrix solution) {
        return objectives.get(index).evaluate(project, solution);
    }

    @Override
    public int size() {
        return objectives.size();
    }
}
