package net.rodrigoamaral.spsp.project;

import net.rodrigoamaral.spsp.graph.TaskPrecedenceGraph;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Project {

    private Map<Integer, Task> tasks;
    private Map<Integer, Employee> employees;
    private TaskPrecedenceGraph taskPrecedenceGraph;

    public Project() {
        tasks = new HashMap<>();
        employees = new HashMap<>();
        taskPrecedenceGraph = new TaskPrecedenceGraph(tasks.size());
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Employee> getEmployees() {
        return employees;
    }

    public TaskPrecedenceGraph getTaskPrecedenceGraph() {
        return taskPrecedenceGraph;
    }

    public void setTaskPrecedenceGraph(TaskPrecedenceGraph taskPrecedenceGraph) {
        this.taskPrecedenceGraph = taskPrecedenceGraph;
    }

    public int size() {
        return getEmployees().size() * getTasks().size();
    }

    private void setTasksDuration(DedicationMatrix dm) {
        for (Task t : getTasks().values()){
            double taskDedication = 0;
            for (Employee e : getEmployees().values()){
                taskDedication += dm.getDedication(e.getId(), t.getId());
            }
            t.setDuration((t.getEffort() / taskDedication));
        }
    }

    private boolean hasDependencies(Task t) {
        ArrayList<Integer> taskDependencies = taskPrecedenceGraph.getTaskDependencies();
        return taskDependencies.get(t.getId()) != 0;
    }

    private void setTasksStartAndFinish(DedicationMatrix dm) {
        setTasksDuration(dm);
        for (Task t: tasks.values()) {
            if (hasDependencies(t)){
                double start = -1;
                ArrayList<Integer> taskPredecessors = taskPrecedenceGraph.getTaskPredecessors(t.getId());
                for (Integer p: taskPredecessors) {
                    start = Math.max(start, tasks.get(p).getFinish());
                }
                t.setStart(start);
                t.setFinish(t.getStart() + t.getDuration());
            } else {
                t.setStart(0);
                t.setFinish(t.getStart() + t.getDuration());
            }
        }
    }

    public double calculateDuration(DedicationMatrix dm) {
        setTasksStartAndFinish(dm);
        double duration = 0;
        for (Task t: tasks.values()) {
            duration = Math.max(duration, t.getFinish());
        }
        return duration;
    }

    private double taskCostByEmployee(Employee e, Task t, DedicationMatrix solution) {
        return e.getSalary() * solution.getDedication(e.getId(), t.getId()) * t.getDuration();
    }

    public double calculateCost(DedicationMatrix solution) {
        double projectCost = 0;
        for (Employee e: employees.values()) {
            double employeeCost = 0;
            for (Task t: tasks.values()) {
                employeeCost += taskCostByEmployee(e, t, solution);
            }
            projectCost += employeeCost;
        }
        return projectCost;
    }

    @Override
    public String toString() {
        return "Project{\n\t" +
                "tasks=" + tasks + ",\n\t" +
                "employees=" + employees + ",\n\t" +
                "taskPrecedenceGraph=" + taskPrecedenceGraph +
                "\n}";
    }

}
