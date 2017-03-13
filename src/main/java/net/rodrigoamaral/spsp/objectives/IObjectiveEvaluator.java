package net.rodrigoamaral.spsp.objectives;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

import java.util.List;

/**
 * Created by rodrigo on 04/03/17.
 */
public interface IObjectiveEvaluator {
    IObjectiveEvaluator addObjective(IObjective objective);
    List<IObjective> getObjectives();
    double evaluate(int index, Project project, DedicationMatrix solution);
    int size();
}
