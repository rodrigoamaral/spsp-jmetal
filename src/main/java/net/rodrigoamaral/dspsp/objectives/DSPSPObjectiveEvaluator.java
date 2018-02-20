package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Encapsulates a collection of all objectives ({@link IObjective})
 * for a given SPSP formulation.
 *
 * @author Rodrigo Amaral
 *
 */
public class DSPSPObjectiveEvaluator implements IObjectiveEvaluator {

    private Map<Integer, IObjective> objectives;

    public DSPSPObjectiveEvaluator() {
        objectives = new HashMap<>();
    }

    @Override
    public IObjectiveEvaluator addObjective(Integer key, IObjective objective) {
        objectives.put(key, objective);
        return this;
    }

    @Override
    public Map<Integer, IObjective> getObjectives() {
        return objectives;
    }

    @Override
    public IObjective get(Integer key) {
        return objectives.get(key);
    }

    @Override
    public double evaluate(int key, DynamicProject project, DedicationMatrix solution) {
        return get(key).evaluate(project, solution);
    }

    @Override
    public int size() {
        return objectives.size();
    }
}
