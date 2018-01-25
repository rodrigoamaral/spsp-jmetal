package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
//import net.rodrigoamaral.spsp.project.Employee;
//import net.rodrigoamaral.spsp.project.Task;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.Collection;
import java.util.List;

/**
 * Created by rodrigo on 07/03/17.
 */
public class NoEmployeeOverworkConstraint implements IConstraint {
    @Override
    public boolean isViolated(DynamicProject project, DedicationMatrix dm) {
        return violationDegree(project, dm) > 0;
    }

    @Override
    public double violationDegree(DynamicProject project, DedicationMatrix dm) {
        Collection<DynamicEmployee> employees = project.getAvailableEmployees();
        Collection<DynamicTask> tasks = project.getActiveTasks();
        // REFACTOR: Get duration objective from some kind of cache
        double projectDuration = project.calculateDuration(dm);
        double projectOverwork = 0.0;
        for (DynamicEmployee e: employees) {
            double employeeOverDedication = 0.0;
            for (int instant = 0; instant <= projectDuration; instant++) {
                double employeeDedication = 0.0;
                for (DynamicTask t: tasks) {
                    if (t.isTaskRunning(instant)) {
                        employeeDedication += dm.getDedication(e.index(), t.index());
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

    public DedicationMatrix repair(DedicationMatrix dm, DynamicProject project) {
        DedicationMatrix repaired = dm;
        List<DynamicEmployee> availableEmployees = project.getAvailableEmployees();
        List<DynamicTask> activeTasks = project.getActiveTasks();
        for (DynamicEmployee e: availableEmployees) {
            double employeeDedication = 0.0;
            for (DynamicTask t: activeTasks) {
                employeeDedication += dm.getDedication(e.index(), t.index());
            }
            if (employeeDedication > e.getMaxDedication()) {
                for (DynamicTask t: activeTasks) {
                    double normalizedDedication = dm.getDedication(e.index(), t.index()) / Math.max(1, employeeDedication/e.getMaxDedication());
                    repaired.setDedication(e.index(), t.index(), normalizedDedication);
                }
            }
        }
        return repaired;
    }

}
