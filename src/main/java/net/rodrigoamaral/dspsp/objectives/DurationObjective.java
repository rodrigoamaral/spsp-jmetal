package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

/**
 * Created by rodrigo on 08/03/17.
 */
public class DurationObjective implements IObjective {
    @Override
    public double evaluate(DynamicProject project, DedicationMatrix solution) {
        return project.calculateDuration(solution);
    }
}
