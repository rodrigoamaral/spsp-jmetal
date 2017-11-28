package net.rodrigoamaral.dspsp.project;

import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.project.events.EventType;
import net.rodrigoamaral.dspsp.scenarios.TaskScenario;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.*;

public class DynamicProject {

    private List<DynamicTask> tasks;
    private List<DynamicEmployee> employees;
    private DynamicTaskPrecedenceGraph taskPrecedenceGraph;
    private List<DynamicEvent> events;
    private Map<Integer, Integer> taskIndices;
    private Map<Integer, Integer> employeeIndices;
    private Map<Integer, List<Double>> taskProficiency;

    //    TODO: Move SCENARIO_SAMPLE_SIZE initialization elsewhere
    public static final int SCENARIO_SAMPLE_SIZE = 30;
//    TODO: Move ROBUSTNESS_COST_WEIGHT initialization elsewhere
    public static final double ROBUSTNESS_COST_WEIGHT = 1;

    public DynamicProject() {
        tasks = new ArrayList<>();
        employees = new ArrayList<>();
        taskPrecedenceGraph = new DynamicTaskPrecedenceGraph(tasks.size());
        taskIndices = new HashMap<>();
        employeeIndices = new HashMap<>();
        taskProficiency = new HashMap<>();
    }

    public List<DynamicTask> getTasks() {
        return tasks;
    }

    public List<DynamicEmployee> getEmployees() {
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

    public List<DynamicTask> cloneTasks(Collection<DynamicTask> tasks_) {
        List<DynamicTask> cloned = new ArrayList<DynamicTask>();
        for (DynamicTask task: tasks_) {
            cloned.add(new DynamicTask(task));
        }
        return cloned;
    }

    public List<DynamicEmployee> cloneEmployees(Collection<DynamicEmployee> employees_) {
        List<DynamicEmployee> cloned = new ArrayList<DynamicEmployee>();
        for (DynamicEmployee employee: employees_) {
            cloned.add(new DynamicEmployee(employee));
        }
        return cloned;
    }

    public List<DynamicTask> resetTasksDuration() {
        return resetTasksDuration(getTasks());
    }

    public List<DynamicTask> resetTasksDuration(List<DynamicTask> tasks_) {
        for (DynamicTask t : tasks_) {
            t.setDuration(0);
            t.setStart(0);
            t.setFinish(0);
        }
        return tasks_;
    }

    public List<DynamicTask> fillTasksDuration(DedicationMatrix dm) {
        return fillTasksDuration(dm, getTasks(), getEmployees());
    }

    public List<DynamicTask> fillTasksDuration(DedicationMatrix dm, List<DynamicTask> tasks_, List<DynamicEmployee> employees_) {
        tasks_ = resetTasksDuration(tasks_);
        for (DynamicTask t : tasks_){
            double taskDedication = 0;
            for (DynamicEmployee e : employees_){
                taskDedication += dm.getDedication(e.getOriginalIndex(), t.getOriginalIndex());
            }
            if (taskDedication > 0) {
                t.setDuration(t.getEffort() / taskDedication);
            }
        }
        return tasks_;
    }

    public boolean hasDependencies(DynamicTask t) {
        return hasDependencies(t, getTaskPrecedenceGraph());
    }

    public boolean hasDependencies(DynamicTask t, DynamicTaskPrecedenceGraph tpg_) {
        ArrayList<Integer> taskDependencies = tpg_.getTaskDependencies();
        return taskDependencies.get(t.getOriginalIndex()) != 0;
    }

    public List<DynamicTask> fillTasksStartAndFinish(DedicationMatrix dm,
                                                     List<DynamicTask> tasks_,
                                                     List<DynamicEmployee> employees_,
                                                     DynamicTaskPrecedenceGraph tpg_) {

        tasks_ = fillTasksDuration(dm, tasks_, employees_);
        for (DynamicTask t: tasks_) {
            if (hasDependencies(t)){
                double start = -1;
                ArrayList<Integer> taskPredecessors = tpg_.getTaskPredecessors(t.getOriginalIndex());
                for (Integer taskIndex: taskPredecessors) {
                    DynamicTask predecessor = tasks_.get(taskIndex);
                    start = Math.max(start, predecessor.getFinish());
                }
                t.setStart(start);
                t.setFinish(t.getStart() + t.getDuration());
            } else {
                t.setStart(0);
                t.setFinish(t.getStart() + t.getDuration());
            }
        }
        return tasks_;
    }

    public double calculateDuration(DedicationMatrix dm) {
        return calculateDuration(dm, getTasks(), getEmployees(), getTaskPrecedenceGraph());
    }

    public double calculateDuration(DedicationMatrix dm, List<DynamicTask> tasks_, List<DynamicEmployee> employees_, DynamicTaskPrecedenceGraph tpg_) {
        tasks_ = fillTasksStartAndFinish(dm, tasks_, employees_, tpg_);
        double duration = 0;
        for (DynamicTask t: tasks_) {
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
        for (DynamicEmployee e: getEmployees()) {
            for (DynamicTask t: getTasks()) {
                projectCost += taskCostByEmployee(e, t, solution);
            }
        }
        return projectCost;
    }

    public double calculateCost(DedicationMatrix solution, List<DynamicTask> tasks_, List<DynamicEmployee> employees_, DynamicTaskPrecedenceGraph tpg_) {
        calculateDuration(solution, tasks_, employees_, tpg_);
        double projectCost = 0;
        for (DynamicEmployee e: employees_) {
            for (DynamicTask t: tasks_) {
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

    public double calculateRobustness(DedicationMatrix solution) {
        double duration = calculateDuration(solution);
        double cost = calculateCost(solution);

        List<Double> durationDistances = new ArrayList<>();
        List<Double> costDistances = new ArrayList<>();

        for (int i = 0; i < SCENARIO_SAMPLE_SIZE; i++) {
            List<DynamicTask> scenarioTasks = cloneTasks(filterAvailableTasks());
            for (DynamicTask task: scenarioTasks) {
                double remainingEffort = TaskScenario.generateRemainingEffort(task, filterAvailableEmployees(), solution);
                task.setEffort(remainingEffort);
            }
            double scenarioDuration = calculateDuration(solution, scenarioTasks, filterAvailableEmployees(), getTaskPrecedenceGraph());
            double scenarioCost = calculateCost(solution, scenarioTasks, filterAvailableEmployees(), getTaskPrecedenceGraph());
            durationDistances.add(efficiencyDistance(scenarioDuration, duration));
            costDistances.add(efficiencyDistance(scenarioCost, cost));
        }
        return Math.sqrt(avg(durationDistances)) + ROBUSTNESS_COST_WEIGHT * Math.sqrt(avg(costDistances));
    }

    public double calculateStability(DedicationMatrix solution) {
        return 0;
    }

    private double efficiencyDistance(double scenarioObjective, double solutionObjective) {
        return Math.pow(Math.max(0, (scenarioObjective - solutionObjective) / solutionObjective), 2);
    }

    private double avg(List<Double> doubleList) {
        return sum(doubleList) / doubleList.size();
    }

    private double sum(List<Double> doubleList) {
        double sum = 0;
        for (double d: doubleList) {
            sum += d;
        }
        return sum;
    }

    public List<DynamicTask> filterAvailableTasks() {
        List<DynamicTask> availableTasks = new ArrayList<>();
        for (DynamicTask task: getTasks()) {
            if (task.isAvailable()) {
                availableTasks.add(task);
            }
        }
        return availableTasks;
    }

    // TODO: Implement actual filtering in filterAvailableEmployees method
    private List<DynamicEmployee> filterAvailableEmployees() {
        List<DynamicEmployee> availableEmployees = new ArrayList<>();
        for (DynamicEmployee employee: getEmployees()) {
//            if (employee.isAvailable()) {
                availableEmployees.add(employee);
//            }
        }
        return availableEmployees;
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
