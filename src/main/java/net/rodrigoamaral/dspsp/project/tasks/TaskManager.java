package net.rodrigoamaral.dspsp.project.tasks;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.DynamicTaskPrecedenceGraph;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskManager {

    final static double Z = 5;

    public static boolean isAvailable(DynamicTask task,
                                      List<DynamicEmployee> employees,
                                      DynamicProject project, DynamicTaskPrecedenceGraph tpg) {
        return (!task.isFinished()) &&
                (missingSkills(task, employees) == 0) &&
                (employeesHaveAllSkillsForPredecessors(task, employees, project, tpg));
    }

    private static boolean employeesHaveAllSkillsForPredecessors(DynamicTask task, List<DynamicEmployee> employees, DynamicProject project, DynamicTaskPrecedenceGraph tpg) {

        for (Integer t: tpg.getTaskPredecessors(task.index())) {
            DynamicTask predecessor = project.getTaskByIndex(t);
            if (missingSkills(predecessor, employees) > 0) {
                return false;
            }
        }

        return true;
    }


    public static double adjustedEffort(DedicationMatrix dm, DynamicTask t) {
        double effort = t.getRemainingEffort();
        if (TaskManager.teamSize(t, dm) > t.getMaximumHeadcount()) {
            effort = effort * TaskManager.teamSizePenalty(t, dm);
        }
        return effort;
    }

    public static double generateEffortSample(DynamicTask task) {

        double mean = task.getMeanEstimatedEffort();
        double sd = task.getEffortDeviation();

        NormalDistribution nd = new NormalDistribution(mean, sd);
        double totalEffort = nd.sample();

        while (!validEffortValue(totalEffort, task.getFinishedEffort())) {
            totalEffort = nd.sample();
        }

        return totalEffort;
    }

    public static double sampleEstimatedEffort(DynamicTask task) {
        NormalDistribution nd = new NormalDistribution(task.getMeanEstimatedEffort(), task.getEffortDeviation());
        return nd.sample();
    }

    static public double totalDedication(DynamicTask task, List<DynamicEmployee> employees, DedicationMatrix solution) {
        double totalDedication = 0;
        for (DynamicEmployee employee: employees) {
            totalDedication += solution.getDedication(employee.index(), task.index());
        }
        return totalDedication;
    }

    static public EffortParameters getEffortProperties(DynamicTask task,
                                                       List<DynamicEmployee> employees,
                                                       DedicationMatrix solution) {

        double totalDedication = totalDedication(task,  employees, solution);
        double totalFitness = totalFitness(task, employees, solution, totalDedication);
        double costDriveValue = costDriveValue(totalFitness);
        double timeSpent = timeSpent(task, costDriveValue, totalDedication);

        return new EffortParameters(task.index(), totalDedication, totalFitness, costDriveValue, timeSpent);
    }

    static public double costDriveValue(double totalFitness) {
        return Math.max(1, 8 - Math.round(totalFitness * 7 + 0.5));
    }

    static public double timeSpent(DynamicTask task, double costDriveValue, double totalDedication) {
        if (totalDedication == 0) {
            return 0;
        }
        return task.getRemainingEffort() * costDriveValue / totalDedication;
    }

    static private double totalFitness(DynamicTask task, List<DynamicEmployee> employees, DedicationMatrix solution, double totalDedication) {
        if (totalDedication == 0) {
            return 0;
        }
        double totalProficiency = 0;
        for (DynamicEmployee e: employees) {
            int id = e.index();
            double proficiencyOnTask = e.getProficiencyOnTask().get(task.index());
            totalProficiency += (proficiencyOnTask * solution.getDedication(id, task.index()));

        }
        return totalProficiency / totalDedication;
    }

    static private boolean validEffortValue(double totalEffort, double finishedEffort) {
        return totalEffort > finishedEffort;
    }

    static public int missingSkills(DynamicTask task, List<DynamicEmployee> employees) {
        Set<Integer> missingSkills = new HashSet<>(task.getSkills());
        for (DynamicEmployee employee: employees) {
            Set<Integer> skills =  new HashSet<>(employee.getSkills());
            missingSkills.removeAll(skills);
        }
        return missingSkills.size();
    }

//    static public int missingSkills(DynamicTask task, DynamicEmployee employee) {
//        Set<Integer> missingSkills = new HashSet<>(task.getSkills());
//        Set<Integer> skills =  new HashSet<>(employee.getSkills());
//        missingSkills.removeAll(skills);
//        return missingSkills.size();
//    }

    static public int missingSkills(DynamicTask task, DynamicEmployee employee) {
        return missingSkillset(task, employee).size();
    }

    static public Set<Integer> missingSkillset(DynamicTask task, DynamicEmployee employee) {
        Set<Integer> missingSkills = new HashSet<>(task.getSkills());
        Set<Integer> skills =  new HashSet<>(employee.getSkills());
        missingSkills.removeAll(skills);
        return missingSkills;
    }

    static public int totalMissingSkills(List<DynamicTask> tasks, List<DynamicEmployee> employees) {
        int missingSkills = 0;
        List<DynamicEmployee> availableEmployees = employees;
        for (DynamicTask task: tasks) {
            missingSkills += missingSkills(task, availableEmployees);
        }
        return missingSkills;
    }

    static public List<Integer> team(DynamicTask task, DedicationMatrix solution) {
        List<Integer> team = new ArrayList<>();
        for (int e = 0; e < solution.getEmployees(); e++) {
            if (solution.getDedication(e, task.index()) > 0) {
                team.add(e);
            }
        }
        return team;
    }

    static public int teamSize(DynamicTask task, DedicationMatrix solution) {
        return team(task, solution).size();
    }

    static public double teamSizePenalty(DynamicTask task, DedicationMatrix solution) {
        int teamSize = teamSize(task, solution);
        return 1 + ((teamSize * (teamSize - 1) / 2) / Z);
    }

    static public List<Integer> getEmployeeAssignedTasks(DedicationMatrix dm, DynamicEmployee employee) {
        List<Integer> taskList = new ArrayList<>();
        for (int j = 0; j < dm.getTasks(); j++) {
            if (dm.getDedication(employee.index(), j) != 0) {
                taskList.add(j);
            }
        }
        return taskList;
    }

    public static List<Integer> getEmployeesNotAssignedToTask(DedicationMatrix dm, DynamicTask task) {
        List<Integer> employeeList = new ArrayList<>();
        for (int i = 0; i < dm.getEmployees(); i++) {
            if (dm.getDedication(i, task.index()) == 0) {
                employeeList.add(i);
            }
        }
        return employeeList;
    }

    public static List<DynamicEmployee> getEmployeesNotAssignedToTask(DedicationMatrix dm, DynamicTask task, final DynamicProject project) {
        List<DynamicEmployee> employees = new ArrayList<>();
        for (int i = 0; i < dm.getEmployees(); i++) {
            if (dm.getDedication(i, task.index()) == 0) {
                employees.add(project.getEmployeeByIndex(i));
            }
        }
        return employees;
    }

    public static List<DynamicEmployee> getEmployeesAssignedToTask(DedicationMatrix dm, DynamicTask task, final DynamicProject project) {
        List<DynamicEmployee> employees = new ArrayList<>();
        for (int i = 0; i < dm.getEmployees(); i++) {
            if (dm.getDedication(i, task.index()) != 0) {
                employees.add(project.getEmployeeByIndex(i));
            }
        }
        return employees;
    }
}
