package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;


public class RobustnessObjective implements IObjective {

    // REFACTOR: Move consntant CROB elsewhere
    private static final int CROB = 100;

    @Override
    public double evaluate(DynamicProject project, DedicationMatrix solution) {
        return project.calculateRobustness(solution, null);
    }

    @Override
    public double penalize(DynamicProject project, DedicationMatrix solution, int missingSkills) {
        return 2 * CROB * missingSkills;
    }
}
