package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.spsp.objectives.IObjective;
import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;


public class StabilityObjective implements IObjective {
    @Override
    public double evaluate(Project project, DedicationMatrix solution) {
        return 0;
    }
}
