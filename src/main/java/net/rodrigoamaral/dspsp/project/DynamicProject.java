package net.rodrigoamaral.dspsp.project;

import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.project.events.EventType;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicProject {

    private Map<Integer, DynamicTask> tasks;
    private Map<Integer, DynamicEmployee> employees;
    private DynamicTaskPrecedenceGraph taskPrecedenceGraph;
    private List<DynamicEvent> events;
    private Map<Integer, Integer> taskIndices;
    private Map<Integer, Integer> employeeIndices;
    private Map<Integer, List<Double>> taskProficiency;


    public DynamicProject() {
        tasks = new HashMap<>();
        employees = new HashMap<>();
        taskPrecedenceGraph = new DynamicTaskPrecedenceGraph(tasks.size());
        taskIndices = new HashMap<>();
        employeeIndices = new HashMap<>();
        taskProficiency = new HashMap<>();
    }

    public Map<Integer, DynamicTask> getTasks() {
        return tasks;
    }

    public Map<Integer, DynamicEmployee> getEmployees() {
        return employees;
    }

    public DynamicTaskPrecedenceGraph getTaskPrecedenceGraph() {
        return taskPrecedenceGraph;
    }

    public void setTaskPrecedenceGraph(DynamicTaskPrecedenceGraph taskPrecedenceGraph) {
        this.taskPrecedenceGraph = taskPrecedenceGraph;
    }

    public Map<Integer, Integer> getTaskIndices() {
        return taskIndices;
    }

    public void setTaskIndices(Map<Integer, Integer> taskIndices) {
        this.taskIndices = taskIndices;
    }

    public Map<Integer, Integer> getEmployeeIndices() {
        return employeeIndices;
    }

    public void setEmployeeIndices(Map<Integer, Integer> employeeIndices) {
        this.employeeIndices = employeeIndices;
    }

    public Map<Integer, List<Double>> getTaskProficiency() {
        return taskProficiency;
    }

    public void setTaskProficiency(Map<Integer, List<Double>> taskProficiency) {
        this.taskProficiency = taskProficiency;
    }

    public DynamicTask getTaskById(int id) {
        return tasks.get(taskIndices.get(id));
    }

    public DynamicEmployee getEmployeeById(int id) {
        return employees.get(employeeIndices.get(id));
    }

    public int size() {
        return getEmployees().size() * getTasks().size();
    }

    private void resetTasksDuration() {
        for (DynamicTask t : getTasks().values()) {
            t.setDuration(0);
            t.setStart(0);
            t.setFinish(0);
        }
    }
    private void setTasksDuration(DedicationMatrix dm) {
        resetTasksDuration();
        for (DynamicTask t : getTasks().values()){
            double taskDedication = 0;
            for (DynamicEmployee e : getEmployees().values()){
                taskDedication += dm.getDedication(e.getOriginalIndex(), t.getOriginalIndex());
            }
            if (taskDedication > 0) {
                t.setDuration((t.getEffort() / taskDedication));
            }
        }
    }

    private boolean hasDependencies(DynamicTask t) {
        ArrayList<Integer> taskDependencies = taskPrecedenceGraph.getTaskDependencies();
        return taskDependencies.get(t.getOriginalIndex()) != 0;
    }

    private void setTasksStartAndFinish(DedicationMatrix dm) {
        setTasksDuration(dm);
        for (DynamicTask t: tasks.values()) {
            if (hasDependencies(t)){
                double start = -1;
                ArrayList<Integer> taskPredecessors = taskPrecedenceGraph.getTaskPredecessors(t.getOriginalIndex());
                for (Integer taskIndex: taskPredecessors) {
                    DynamicTask predecessor = tasks.get(taskIndex);
                    start = Math.max(start, predecessor.getFinish());
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
        for (DynamicTask t: tasks.values()) {
            duration = Math.max(duration, t.getFinish());
        }
        return duration;
    }

    private double taskCostByEmployee(DynamicEmployee e, DynamicTask t, DedicationMatrix solution) {
        double employeeDedication = solution.getDedication(e.getOriginalIndex(), t.getOriginalIndex());
        double regularCost = e.getSalary() * employeeDedication * t.getDuration();
        return regularCost + getOvertimeCost(e, t, employeeDedication - 1)  * t.getDuration();
    }

    public double calculateCost(DedicationMatrix solution) {
        calculateDuration(solution);
        double projectCost = 0;
        for (DynamicEmployee e: employees.values()) {
            for (DynamicTask t: tasks.values()) {
                projectCost += taskCostByEmployee(e, t, solution);
            }
        }
        return projectCost;
    }

    private double getOvertimeCost(DynamicEmployee e, DynamicTask t, double overdedication) {
        double overtimeCost = e.getOvertimeSalary() * overdedication * t.getDuration();
        overtimeCost = overtimeCost > 0 ? overtimeCost : 0;
        return overtimeCost;
    }

    private double employeeTotalDedication(DedicationMatrix solution, DynamicEmployee e) {
        double employeeDedication = 0;
        for (DynamicTask t: tasks.values()) {
            employeeDedication += solution.getDedication(e.getOriginalIndex(), t.getOriginalIndex());
        }
        return employeeDedication;
    }

    public List<DynamicEvent> getEvents() {
        return events;
    }

    public void setEvents(List<DynamicEvent> events) {
        this.events = events;
    }

    public boolean isEmployeeAvailable(DynamicEmployee emp, int eventIndex) {
        boolean available = true;
        for (int i = 0; i <= eventIndex; i++) {
            DynamicEvent event = getEvents().get(i);
            if (event.getSubject().equals(emp) && event.getType() == EventType.EMPLOYEE_LEAVE) {
                available = false;
                continue;
            }
            if (event.getSubject().equals(emp) && event.getType() == EventType.EMPLOYEE_RETURN) {
                available = true;
                continue;
            }
        }
        return available;
    }

    public double getEmployeeProficiencyInTask(DynamicEmployee employee, DynamicTask task) {
        return taskProficiency.get(employee.getOriginalIndex()).get(task.getOriginalIndex());
    }


    @Override
    public String toString() {
        return "DynamicProject{\n\t" +
                "tasks=" + tasks + ",\n\t" +
                "employees=" + employees + ",\n\t" +
                "taskPrecedenceGraph=" + taskPrecedenceGraph +
                "\n}";
    }

}
