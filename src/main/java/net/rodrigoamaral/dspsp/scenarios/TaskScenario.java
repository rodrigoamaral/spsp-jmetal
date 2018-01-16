package net.rodrigoamaral.dspsp.scenarios;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.DynamicTask;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.List;

public class TaskScenario {

    static public List<Double> generateRemainingEfforts(List<DynamicTask> tasks, List<DynamicEmployee> employees, DedicationMatrix solution) {
        List<Double> efforts = new ArrayList<>();
        for (DynamicTask task: tasks) {
            efforts.add(generateRemainingEffort(task, employees, solution));
        }
        return efforts;
    }

//  REVIEW: Check if generateRemainingEffort matches the description in the paper
    public static double generateRemainingEffort(DynamicTask task,
                                                 List<DynamicEmployee> employees,
                                                 DedicationMatrix solution) {
        double mean = task.getEffort();
        double sd = mean * 0.5;
        NormalDistribution nd = new NormalDistribution(mean, sd);
        double totalEffort = nd.sample();
        double finishedEffort = getFinishedEffort(task, employees, solution);
        while (!validEffortValue(totalEffort, finishedEffort)) {
            totalEffort = nd.sample();
        }
        return totalEffort - finishedEffort;
    }

    static public double employeeTotalDedication(DedicationMatrix solution, DynamicEmployee e, List<DynamicTask> tasks_) {
        double employeeDedication = 0;
        for (DynamicTask t: tasks_) {
            employeeDedication += solution.getDedication(e.index(), t.index());
        }
        return employeeDedication;
    }

    static public double totalDedicationToTask(DedicationMatrix solution, DynamicTask t, List<DynamicEmployee> employees_) {
        double totalDedication = 0;
        for (DynamicEmployee e: employees_) {
            totalDedication += solution.getDedication(e.index(), t.index());
        }
        return totalDedication;
    }


    /**
     * Calculate finished effort for a task.
     *
     * The task must be available and active at the current time.
     * @param task
     * @param employees
     * @param solution
     * @return
     */
    public static double getFinishedEffort(DynamicTask task,
                                           List<DynamicEmployee> employees,
                                           DedicationMatrix solution) {
//        System.out.println("getFinishedEffort :: TASK " + task);
        double totalDedication = totalDedicationToTask(solution, task, employees);
        double totalFitness = totalFitnessOfEmployeesToTask(task, employees, solution, totalDedication);
        double costDriveValue = costDriveValue(totalFitness);
        double timeSpent = timeSpent(task.getEffort(), costDriveValue, totalDedication);

        return task.getFinishedEffort() + totalDedication * timeSpent / costDriveValue;
    }

    static public double costDriveValue(double totalFitness) {
        return 8 - Math.round(totalFitness * 7 + 0.5);
    }

    static public double timeSpent(double effort, double costDriveValue, double totalDedication) {
        return effort * costDriveValue / totalDedication;
    }

    static public double totalFitnessOfEmployeesToTask(DynamicTask task, List<DynamicEmployee> employees, DedicationMatrix solution, double totalDedication) {
        double totalProficiency = 0;
        for (DynamicEmployee e: employees) {
            int id = e.index();
            for (int skill: task.getSkills()) {
                for (int i = 0; i < e.getSkills().size(); i++) {
                    if (e.getSkills().get(i) == skill) {
                        double skillProficiency = e.getSkillsProficiency().get(i);
                        totalProficiency += skillProficiency * solution.getDedication(id, task.index());
                    }
                }
            }

        }
        return totalProficiency / totalDedication;
    }

    static public double totalFitnessOfEmployeesToTask(DynamicProject project, DynamicTask task, List<DynamicEmployee> employees, DedicationMatrix solution, double totalDedication) {
        double totalProficiency = 0;
        for (DynamicEmployee e: employees) {
            totalProficiency += taskProficiency(project, task, e) * solution.getDedication(e.index(), task.index());
        }
        return totalProficiency / totalDedication;
    }

    static public double taskProficiency(DynamicProject project, DynamicTask task, DynamicEmployee employee) {
        return project.getTaskProficiency().get(employee.index()).get(task.index());
    }

    private static boolean validEffortValue(double totalEffort, double finishedEffort) {
        return totalEffort > finishedEffort;
    }


}
