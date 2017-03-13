package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

/**
 * Constraint 1: Each task must be performed by at least one person
 */
public class AllTasksAllocatedConstraint extends AbstractConstraint implements IConstraint {

    @Override
    public boolean isViolated(Project project, DedicationMatrix s) {
        return violationDegree(project, s) > 0.0;
    }

    @Override
    public double violationDegree(Project p, DedicationMatrix s) {
        double degree = 0.0;
        for (int t = 0; t < s.getTasks(); t++) {
            double taskDedication = 0.0;
            for (int e = 0; e < s.getEmployees(); e++) {
                taskDedication = taskDedication + s.getDedication(e, t);
            }
            if (taskDedication == 0.0) {
                degree = degree + 1;
            }
        }
        return degree;
    }
}
