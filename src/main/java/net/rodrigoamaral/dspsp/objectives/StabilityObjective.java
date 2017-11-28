package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;


public class StabilityObjective implements IObjective {
    @Override
    public double evaluate(DynamicProject project, DedicationMatrix solution) {
        return project.calculateStability(solution);
    }
}
