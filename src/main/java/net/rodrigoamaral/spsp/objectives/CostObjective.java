package net.rodrigoamaral.spsp.objectives;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

public class CostObjective implements IObjective {
    @Override
    public double evaluate(Project project, DedicationMatrix solution) {
        return project.calculateCost(solution);
    }
}
