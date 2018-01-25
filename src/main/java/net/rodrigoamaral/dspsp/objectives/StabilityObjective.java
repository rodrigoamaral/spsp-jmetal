package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;


public class StabilityObjective implements IObjective {
    @Override
    public double evaluate(DynamicProject project, DedicationMatrix solution) {
        return project.calculateStability(solution);
    }

    @Override
    public double penalize(DynamicProject project, DedicationMatrix solution, int missingSkills) {

        int numberOfAvailableEmployees = project.getAvailableEmployees().size();
        int numberOfAvailableTasks = project.getAvailableTasks().size();
        double maxDedication = project.getAvailableEmployeeMaxDedication();

        return 2 * missingSkills * numberOfAvailableEmployees * numberOfAvailableTasks * maxDedication;
    }
}
