package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;


public class CostObjective implements IObjective {
    @Override
    public double evaluate(DynamicProject project, DedicationMatrix solution) {
        return project.evaluateEfficiency(solution).cost;
    }

    @Override
    public double penalize(DynamicProject project, DedicationMatrix solution, int missingSkills) {
        double cost = 0;
        for (DynamicEmployee employee: project.getAvailableEmployees()) {
            for (DynamicTask task: project.getAvailableTasks()) {
                cost += employee.getOvertimeSalary() * task.getRemainingEffort();
            }
        }
        return 14 * missingSkills * cost;
    }
}
