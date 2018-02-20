package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

/**
 * Constraint 1: Each task must be performed by at least one person
 */
public class AllTasksAllocatedConstraint implements IConstraint {

    public double violationDegree(DynamicProject p, DedicationMatrix dm) {
        return 0;
    }

    @Override
    public DedicationMatrix repair(DedicationMatrix dm, DynamicProject project) {
        for (DynamicEmployee employee: project.getAvailableEmployees()) {
            for (DynamicTask task: project.getAvailableTasks()) {
                double proficiency = project.getTaskProficiency().get(employee.index()).get(task.index());
                if (proficiency == 0) {
                    dm.setDedication(employee.index(), task.index(), 0);
                }
            }
        }
        return dm;
    }

}
