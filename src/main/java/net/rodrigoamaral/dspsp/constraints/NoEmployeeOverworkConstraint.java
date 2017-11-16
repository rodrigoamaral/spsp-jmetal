package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.DynamicTask;
//import net.rodrigoamaral.spsp.project.Employee;
//import net.rodrigoamaral.spsp.project.Task;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.Collection;

/**
 * Created by rodrigo on 07/03/17.
 */
public class NoEmployeeOverworkConstraint implements IConstraint {
    @Override
    public boolean isViolated(DynamicProject project, DedicationMatrix s) {
        return violationDegree(project, s) > 0;
    }

    @Override
    public double violationDegree(DynamicProject project, DedicationMatrix s) {
        Collection<DynamicEmployee> employees = project.getEmployees().values();
        Collection<DynamicTask> tasks = project.getTasks().values();
        double projectDuration = project.calculateDuration(s);
//        System.out.println("Project Duration: " + projectDuration);
        double projectOverwork = 0.0;
        for (DynamicEmployee e: employees) {
//            System.out.println("DynamicEmployee: " + e);
            double employeeOverDedication = 0.0;
            for (int instant = 0; instant <= projectDuration; instant++) {
//                System.out.println("-------------------");
//                System.out.println("Instant: " + instant);
//                System.out.println("-------------------");
                double employeeDedication = 0.0;
                for (DynamicTask t: tasks) {
//                    System.out.println("DynamicTask:" + t);
                    if (t.isTaskRunning(instant)) {
//                        System.out.println("DynamicTask is running!");
                        employeeDedication += s.getDedication(e.getOriginalIndex(), t.getOriginalIndex());
                    }
//                    System.out.println("DynamicEmployee " + e.getId() + " dedication to DynamicTask " + t.getId() + ": " + s.getDedication(e.getId(), t.getId()));
                }
//                System.out.println("DynamicEmployee " + e.getId() + " total dedication: " + employeeDedication);
                if (employeeDedication > e.getMaxDedication()) {
                    employeeOverDedication += employeeDedication - e.getMaxDedication();
//                    System.out.println("DynamicEmployee " + e.getId() + " overdedication: " + employeeOverDedication);
                }
            }
            projectOverwork += employeeOverDedication;
        }
        return projectOverwork;
    }


}
