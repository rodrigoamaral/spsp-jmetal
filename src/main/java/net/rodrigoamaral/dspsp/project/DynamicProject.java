package net.rodrigoamaral.dspsp.project;

import net.rodrigoamaral.dspsp.adapters.SolutionConverter;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.project.events.EventType;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.*;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.max;
import static net.rodrigoamaral.util.DoubleUtils.sum;


public class DynamicProject {

    private List<DynamicTask> tasks;
    private List<DynamicTask> availableTasks;
    private List<DynamicTask> activeTasks;
    private List<DynamicEmployee> employees;

    private List<DynamicEmployee> availableEmployees;

    private DynamicTaskPrecedenceGraph taskPrecedenceGraph;
    private List<DynamicEvent> events;
    private Map<Integer, Integer> taskIndices;
    private Map<Integer, Integer> employeeIndices;

    private Map<Integer, List<Double>> taskProficiency;

    private List<Double> taskArrivalTimes;

    private DedicationMatrix previousSchedule;

    private double lastSchedulingTime;

    //    REFACTOR : Move SCENARIO_SAMPLE_SIZE initialization elsewhere
    public static final int SCENARIO_SAMPLE_SIZE = 30;
    //    REFACTOR: Move ROBUSTNESS_COST_WEIGHT initialization elsewhere
    public static final double ROBUSTNESS_COST_WEIGHT = 1;
    public DynamicProject() {
        tasks = new ArrayList<>();
        employees = new ArrayList<>();
        taskPrecedenceGraph = new DynamicTaskPrecedenceGraph(tasks.size());
        taskIndices = new HashMap<>();
        employeeIndices = new HashMap<>();
        taskProficiency = new HashMap<>();
        taskArrivalTimes = new ArrayList<>();
        previousSchedule = null;
        lastSchedulingTime = 0;
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

    public Map<Integer, Integer> getEmployeeIndices() {
        return employeeIndices;
    }

    public Map<Integer, List<Double>> getTaskProficiency() {
        return taskProficiency;
    }

    public List<Double> getTaskArrivalTimes() {
        return taskArrivalTimes;
    }

    public void setLastSchedulingTime(double lastSchedulingTime) {
        this.lastSchedulingTime = lastSchedulingTime;
    }

    public void setPreviousSchedule(DoubleSolution previousSchedule) {
        this.previousSchedule =  new SolutionConverter(this).convert(previousSchedule);
    }

    public DynamicTask getTaskById(int id) {
        return tasks.get(taskIndices.get(id));
    }

    public DynamicTask getTaskByIndex(int index) {
        return getTaskById(index + 1);
    }

    public DynamicEmployee getEmployeeByIndex(int index) {
        return getEmployeeById(index + 1);
    }

    public DynamicEmployee getEmployeeById(int id) {
        try {
            return employees.get(employeeIndices.get(id));
        } catch (NullPointerException npe) {
            System.out.println("Employee ID " + id + " not found!");
            npe.printStackTrace();
        }
        return null;
    }

    public int size() {
        return getEmployees().size() * getTasks().size();
    }

    public List<DynamicTask> getAvailableTasks() {
        return availableTasks;
    }

    public List<DynamicTask> getActiveTasks() {
        return activeTasks;
    }

    public List<DynamicEmployee> getAvailableEmployees() {
        return availableEmployees;
    }

    public List<DynamicTask> cloneTasks(Collection<DynamicTask> tasks_) {
        List<DynamicTask> cloned = new ArrayList<>();
        for (DynamicTask task: tasks_) {
            cloned.add(new DynamicTask(task));
        }
        return cloned;
    }

    public List<DynamicTask> resetTasksDuration(List<DynamicTask> tasks_) {
        for (DynamicTask t : tasks_) {
            t.setDuration(0);
            t.setStart(0);
            t.setFinish(0);
        }
        return tasks_;
    }

    public List<DynamicTask> fillTasksDuration(DedicationMatrix dm, List<DynamicTask> tasks, List<DynamicEmployee> employees) {
        tasks = resetTasksDuration(tasks);
        for (DynamicTask t : tasks){
            double taskDedication = 0;
            for (DynamicEmployee e : employees){
                taskDedication += dm.getDedication(e.index(), t.index());
            }
            if (taskDedication > 0) {
                double effort = TaskManager.adjustedEffort(dm, t);
                t.setDuration(effort / taskDedication);
            }
        }
        return tasks;
    }

    public boolean hasDependencies(DynamicTask t) {
        return hasDependencies(t, getTaskPrecedenceGraph());
    }

    public boolean hasDependencies(DynamicTask t, DynamicTaskPrecedenceGraph tpg_) {
        ArrayList<Integer> taskDependencies = tpg_.getTaskDependencies();
        return taskDependencies.get(t.index()) != 0;
    }

    public List<DynamicTask> fillTasksStartAndFinish(DedicationMatrix dm,
                                                     List<DynamicTask> tasks,
                                                     List<DynamicEmployee> employees,
                                                     DynamicTaskPrecedenceGraph tpg) {

        tasks = fillTasksDuration(dm, tasks, employees);
        for (DynamicTask t: tasks) {
            if (hasDependencies(t)){
                double start = -1;
                Vector<Integer> taskPredecessors = tpg.getTaskPredecessors(t.index());
                for (Integer taskIndex: taskPredecessors) {
                    DynamicTask predecessor = getTasks().get(taskIndex);
                    start = Math.max(start, predecessor.getFinish());
                }
                t.setStart(start);
                t.setFinish(t.getStart() + t.getDuration());
            } else {
                t.setStart(0);
                t.setFinish(t.getStart() + t.getDuration());
            }
        }
        return tasks;
    }

    /**
     * Updates project state based on the dynamic event and current schedule
     *
     * @param lastSchedule
     * @param event
     */
    public double update(DynamicEvent event, DoubleSolution lastSchedule) throws Exception {
        // REFACTOR: lastSchedule should be a DedicationMatrix to avoid dependencies with jMetal
        setPreviousSchedule(lastSchedule);
        double partialDuration = updateFinishedEffort(activeTasks, availableEmployees);
        updateCurrentStatus(event);
        setLastSchedulingTime(event.getTime());
        return partialDuration;
    }

    public void updateCurrentStatus(DynamicEvent event) {
        ////
        SPSPLogger.info(">>> " + event.description() + " at " + event.getTime());
        ////
        updateEmployeeAvailability(event);
        updateTaskAvailability(event);
        updateCurrentStatus();
    }

    public void updateCurrentStatus() {
        availableTasks = filterAvailableTasks();
        availableEmployees = filterAvailableEmployees();
        activeTasks = filterActiveTasks();
        ////
        SPSPLogger.info("... Available employees: " + availableEmployees);
        SPSPLogger.info("... Available tasks: " + availableTasks);
        SPSPLogger.info("... Active tasks: " + activeTasks);
        ////
    }

    private void updateEmployeeAvailability(DynamicEvent event) {
        int id = event.getSubject().getId();
        if (event.getType() == EventType.EMPLOYEE_LEAVE) {
            getEmployeeById(id).setAvailable(false);
        } else if (event.getType() == EventType.EMPLOYEE_RETURN) {
            getEmployeeById(id).setAvailable(true);
        }
//        availableEmployees = filterAvailableEmployees();
    }

    private void updateTaskAvailability(DynamicEvent event) {
//        checkTaskAvailabilityCriteria();
        List<Integer> incomingTasksIDs = getIncomingTasks(event);
        makeTasksAvailable(incomingTasksIDs, event);
//        availableTasks = filterAvailableTasks();
    }

    private void checkTaskAvailabilityCriteria() {
        for (DynamicTask task: availableTasks) {
            boolean available = TaskManager.isAvailable(task, availableEmployees, this);
            task.setAvailable(available);
        }
    }

    private void makeTasksAvailable(List<Integer> incomingTaskIDs, DynamicEvent event) {
        int urgentTaskIndex = -1;
        if (event.getType() == EventType.NEW_URGENT_TASK) {
            if (!incomingTaskIDs.isEmpty()) {
                urgentTaskIndex = incomingTaskIDs.remove(incomingTaskIDs.size() - 1);
            }
        }
        for (int t: incomingTaskIDs) {
            List<Integer> predecessors = chooseRandomTasks();
            for (int p: predecessors) {
                taskPrecedenceGraph.addEdge(p, t);
                DynamicTask newTask = getTaskByIndex(t);
                boolean available = TaskManager.isAvailable(newTask, availableEmployees, this);
                newTask.setAvailable(available);
                ////
                SPSPLogger.info("... Task " + t + " added after task " + p);
                ////
            }
        }
        if (urgentTaskIndex > -1) {
            List<Integer> successors = chooseRandomTasks();
            for (int s: successors) {
                taskPrecedenceGraph.addEdge(urgentTaskIndex, s);
                DynamicTask newUrgentTask = getTaskByIndex(urgentTaskIndex);
                boolean available = TaskManager.isAvailable(newUrgentTask, availableEmployees, this);
                newUrgentTask.setAvailable(available);
                ////
                SPSPLogger.info("... Urgent Task " + urgentTaskIndex + " added before task " + s);
                ////
            }
        }
    }

    private List<Integer> chooseRandomTasks() {
        List<Integer> tasks = new ArrayList<>();
        int numTasks = Math.random() <= 0.5 ? 2 : 1;
        int s = availableTasks.size();
        while (tasks.size() < numTasks && tasks.size() <= s && s > 0) {
            int t = availableTasks.get(new Random().nextInt(s)).index();
            if (!tasks.contains(t)) {
                tasks.add(t);
            }
        }
        return tasks;
    }

    private List<Integer> getIncomingTasks(DynamicEvent event) {
        List<Integer> incomingTasks = new ArrayList<>();
        double currentTime = event.getTime();
        for (int i = 0; i < getTaskArrivalTimes().size(); i++) {
            double time = getTaskArrivalTimes().get(i);
            if (time > lastSchedulingTime && time <= currentTime) {
                incomingTasks.add(i);
            }
        }
        return incomingTasks;
    }

    private double updateFinishedEffort(List<DynamicTask> activeTasks, List<DynamicEmployee> availableEmployees) throws Exception {
        double partialDuration;
        if (activeTasks.isEmpty()) {
            throw new Exception("Problem instance not solvable!");
        } else {
            DedicationMatrix normalizedSchedule = normalize(this.previousSchedule, activeTasks);
            partialDuration = getPartialDuration(activeTasks, availableEmployees, normalizedSchedule);

            for (DynamicTask task : activeTasks) {

                double totalDedication = TaskManager.totalDedication(task, availableEmployees, normalizedSchedule);
                double totalFitness = TaskManager.totalFitness(this, task, availableEmployees, normalizedSchedule, totalDedication);
                double costDriveValue = TaskManager.costDriveValue(totalFitness);
                double finishedEffort = partialDuration * (totalDedication / costDriveValue);

                task.addFinishedEffort(finishedEffort);

                if (task.isFinished()) {
                    task.setAvailable(false);
                    taskPrecedenceGraph.remove(task.index());
                    ////
                    SPSPLogger.info("... Task " + task.index() + " is COMPLETE");
                    ////
                } else {
                    task.setRealEffort(reestimateEffort(task));
                    ////
                    SPSPLogger.info("... Task " + task.index() + " effort estimated to " + task.getRealEffort() + " (finished " + task.getFinishedEffort() + ")");
                    ////
                }
            }
        }
        availableTasks = filterAvailableTasks();
        return partialDuration;
    }

    private double reestimateEffort(DynamicTask task) {
        double realEffort = task.getRealEffort();
        double finishedEffort = task.getFinishedEffort();

        while (realEffort <= finishedEffort) {
            realEffort = TaskManager.sampleEstimatedEffort(task);
        }

        return realEffort;
    }

    private double getPartialDuration(List<DynamicTask> activeTasks, List<DynamicEmployee> availableEmployees, DedicationMatrix normalizedSchedule) {
        if (activeTasks.isEmpty()) {
            return 0;
        }
        double partialDuration = Double.POSITIVE_INFINITY;
        for (DynamicTask task : activeTasks) {
            double totalDedication = TaskManager.totalDedication(task, availableEmployees, normalizedSchedule);
            double totalFitness = TaskManager.totalFitness(this, task, availableEmployees, normalizedSchedule, totalDedication);
            double costDriveValue = TaskManager.costDriveValue(totalFitness);
            double timeSpent = TaskManager.timeSpent(task, costDriveValue, totalDedication);
            if (timeSpent < partialDuration) {
                partialDuration = timeSpent;
            }
        }
        return partialDuration;
    }

    private DedicationMatrix normalize(DedicationMatrix dm, List<DynamicTask> activeTasks) {
        DedicationMatrix normalized = new DedicationMatrix(employees.size(), tasks.size());
//        normalized.reset();
        for (DynamicTask t: activeTasks) {
            for (DynamicEmployee e: availableEmployees) {
                double n = dm.getDedication(e.index(), t.index()) / max(1, dedicationSum(dm, activeTasks, e) / e.getMaxDedication());
                normalized.setDedication(e.index(), t.index(), n);
            }
        }
        return normalized;
    }

    private double dedicationSum(DedicationMatrix dm, List<DynamicTask> activeTasks, DynamicEmployee e) {
        return sum(activeTasksDedication(dm, activeTasks, e));
    }

    private List<Double> activeTasksDedication(DedicationMatrix dm, List<DynamicTask> activeTasks, DynamicEmployee employee) {
        List<Double> result = new ArrayList<>();
        for (DynamicTask task: activeTasks) {
            result.add(dm.getDedication(employee.index(), task.index()));
        }
        return result;
    }


    public double calculateDuration(DedicationMatrix dm) {
        // REVIEW: Does the paper consider only the ACTIVE tasks???
//        return calculateDuration(dm, availableTasks, getEmployees(), getTaskPrecedenceGraph());
        return calculateDuration(dm, activeTasks, getEmployees(), getTaskPrecedenceGraph());
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
        double employeeDedication = solution.getDedication(e.index(), t.index());
        double regularCost = e.getSalary() * employeeDedication * t.getDuration();
        return regularCost + getOvertimeCost(e, t, employeeDedication - 1)  * t.getDuration();
    }

    public double calculateCost(DedicationMatrix solution) {
        calculateDuration(solution);
        double projectCost = 0;
        for (DynamicEmployee e: getEmployees()) {
            for (DynamicTask t: availableTasks) {
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

    public double calculateRobustness(DedicationMatrix solution) {
        double duration = calculateDuration(solution);
        double cost = calculateCost(solution);

        List<Double> durationDistances = new ArrayList<>();
        List<Double> costDistances = new ArrayList<>();

        for (int i = 0; i < SCENARIO_SAMPLE_SIZE; i++) {
            List<DynamicTask> scenarioAvailableTasks = cloneTasks(availableTasks);
            for (DynamicTask task: scenarioAvailableTasks) {
                double remainingEffort = TaskManager.generateRemainingEffort(task, availableEmployees, solution);
                task.setEffort(remainingEffort);
            }
            double scenarioDuration = calculateDuration(solution, scenarioAvailableTasks, availableEmployees, getTaskPrecedenceGraph());
            double scenarioCost = calculateCost(solution, scenarioAvailableTasks, availableEmployees, getTaskPrecedenceGraph());
            durationDistances.add(efficiencyDistance(scenarioDuration, duration));
            costDistances.add(efficiencyDistance(scenarioCost, cost));
        }
        return Math.sqrt(avg(durationDistances)) + ROBUSTNESS_COST_WEIGHT * Math.sqrt(avg(costDistances));
    }

    public double calculateStability(DedicationMatrix solution) {

//        REFACTOR: Avoid invoking calculateStability method for the initial scheduling
        if (previousSchedule == null) {
            return 0;
        }

        double stabilityValue = 0;
        for (DynamicEmployee e: availableEmployees) {
            for (DynamicTask t: availableTasks) {
                double currentDedication = solution.getDedication(e.index(), t.index());
                double previousDedication = previousSchedule.getDedication(e.index(), t.index());
                double w = reschedulingPenalty(currentDedication, previousDedication);
                stabilityValue = stabilityValue + (w * Math.abs(currentDedication - previousDedication));
            }
        }

        return stabilityValue;
    }

    public double reschedulingPenalty(double currentDedication, double previousDedication) {
        if (currentDedication > 0 && previousDedication == 0) {
            return 2;
        } else if (currentDedication == 0 && previousDedication > 0) {
            return  1.5;
        }
        return 1;
    }

    private double efficiencyDistance(double scenarioObjective, double solutionObjective) {
        return Math.pow(Math.max(0, (scenarioObjective - solutionObjective) / solutionObjective), 2);
    }

    private double avg(List<Double> doubleList) {
        return sum(doubleList) / doubleList.size();
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

    private List<DynamicEmployee> filterAvailableEmployees() {
        List<DynamicEmployee> availableEmployees = new ArrayList<>();
        for (DynamicEmployee employee: getEmployees()) {
            if (employee.isAvailable()) {
                availableEmployees.add(employee);
            }
        }
        return availableEmployees;
    }

    private List<DynamicTask> filterActiveTasks() {
        List<DynamicTask> activeTasks = new ArrayList<>();
        for (int t: taskPrecedenceGraph.getIndependentTasks()) {
            DynamicTask task = getTaskByIndex(t);
            if (task.isAvailable()) {
                activeTasks.add(task);
            }
        }
        return activeTasks;
    }

    public boolean isFinished() {

        for (DynamicTask task: getTasks()) {
            if (!task.isFinished()) {
                return false;
            }
        }

        return true;
    }

    public int missingSkills() {
        return TaskManager.totalMissingSkills(getAvailableTasks(), getAvailableEmployees());
    }

    public int missingSkills(DynamicTask task, List<DynamicEmployee> employees) {
        return TaskManager.missingSkills(task, employees);
    }

    public double getAvailableEmployeeMaxDedication() {
        double max = 0;
        for (DynamicEmployee employee: this.getAvailableEmployees()) {
            if (employee.getMaxDedication() > max) {
                max = employee.getMaxDedication();
            }
        }
        return max;
    }

    public double getAvailableEmployeeMinDedication() {
        double min = POSITIVE_INFINITY;
        for (DynamicEmployee employee: this.getAvailableEmployees()) {
            if (employee.getMaxDedication() < min) {
                min = employee.getMaxDedication();
            }
        }
        return min;
    }

    public double getTotalEstimatedRemainingEffort() {
        double remainingEffort = 0;
        for (DynamicTask task: getAvailableTasks()) {
            remainingEffort += task.getRemainingEffort();
        }
        return remainingEffort;
    }

    public int taskTeamSize(DynamicTask task, DedicationMatrix solution) {
        return TaskManager.teamSize(task, solution);
    }

    public List<DynamicEmployee> taskTeam(DynamicTask task, DedicationMatrix solution) {
        List<DynamicEmployee> team = new ArrayList<>();
        for (int e: TaskManager.team(task, solution)) {
            team.add(getEmployeeByIndex(e));
        }
        return team;
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
