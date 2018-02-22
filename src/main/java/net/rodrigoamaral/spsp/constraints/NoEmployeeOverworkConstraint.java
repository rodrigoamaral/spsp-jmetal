package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Employee;
import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.project.Task;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

import java.util.Collection;

public class NoEmployeeOverworkConstraint implements IConstraint {
    @Override
    public boolean isViolated(Project project, DedicationMatrix s) {
        return violationDegree(project, s) > 0;
    }

    @Override
    public double violationDegree(Project project, DedicationMatrix s) {
        Collection<Employee> employees = project.getEmployees().values();
        Collection<Task> tasks = project.getTasks().values();
        double projectDuration = project.calculateDuration(s);
        double projectOverwork = 0.0;
        for (Employee e: employees) {
            double employeeOverDedication = 0.0;
            for (int instant = 0; instant <= projectDuration; instant++) {
                double employeeDedication = 0.0;
                for (Task t: tasks) {
                    if (t.isTaskRunning(instant)) {
                        employeeDedication += s.getDedication(e.getId(), t.getId());
                    }
                }
                if (employeeDedication > e.getMaxDedication()) {
                    employeeOverDedication += employeeDedication - e.getMaxDedication();
                }
            }
            projectOverwork += employeeOverDedication;
        }
        return projectOverwork;
    }

}
