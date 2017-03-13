package net.rodrigoamaral.spsp.objectives;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

/**
 * Created by rodrigo on 08/03/17.
 */
public class DurationObjective implements IObjective {
    @Override
    public double evaluate(Project project, DedicationMatrix solution) {
        return project.calculateDuration(solution);
    }
}
