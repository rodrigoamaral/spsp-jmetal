package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Employee;
import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.project.Task;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rodrigo on 07/03/17.
 */
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
//        System.out.println("Project Duration: " + projectDuration);
        double projectOverwork = 0.0;
        for (Employee e: employees) {
//            System.out.println("DynamicEmployee: " + e);
            double employeeOverDedication = 0.0;
            for (int instant = 0; instant <= projectDuration; instant++) {
//                System.out.println("-------------------");
//                System.out.println("Instant: " + instant);
//                System.out.println("-------------------");
                double employeeDedication = 0.0;
                for (Task t: tasks) {
//                    System.out.println("DynamicTask:" + t);
                    if (t.isTaskRunning(instant)) {
//                        System.out.println("DynamicTask is running!");
                        employeeDedication += s.getDedication(e.getId(), t.getId());
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

//    @Override
//    public double violationDegree(Project project, DedicationMatrix s) {
//        Collection<DynamicEmployee> employees = project.getEmployees().values();
//        Collection<DynamicTask> tasks = project.getTasks().values();
//        double projectDuration = project.calculateDuration(s);
//        double projectOverwork = 0.0;
//        for (DynamicEmployee e: employees) {
//            List<Double> overwork = new ArrayList<>();
//            double employeeOverDedication = 0.0;
//            for (int instant = 0; instant <= projectDuration; instant++) {
//                double employeeDedication = 0.0;
//                for (DynamicTask t: tasks) {
//                    if (t.isTaskRunning(instant)) {
//                        employeeDedication += s.getDedication(e.getId(), t.getId());
//                    }
//                }
//                if (employeeDedication > e.getMaxDedication()) {
//                    overwork.add(employeeDedication - e.getMaxDedication());
//                } else {
//                    overwork.add(0.0);
//                }
//                employeeOverDedication += overwork.get(overwork.size() - 1);
//            }
//            projectOverwork += employeeOverDedication;
//        }
//        return projectOverwork;
//    }
}
