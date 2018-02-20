package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.Map;

/**
 *
 * Basic interface for a class which evaluates all objectives
 * in a {@link net.rodrigoamaral.spsp.SPSProblem}.
 *
 * @author Rodrigo Amaral
 *
 */
public interface IObjectiveEvaluator {
    IObjectiveEvaluator addObjective(Integer key, IObjective objective);
    Map<Integer, IObjective> getObjectives();
    IObjective get(Integer key);
    double evaluate(int index, DynamicProject project, DedicationMatrix solution);
    int size();
}
