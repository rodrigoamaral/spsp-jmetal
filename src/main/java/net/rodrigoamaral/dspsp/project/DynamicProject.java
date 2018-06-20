package net.rodrigoamaral.dspsp.project;

import net.rodrigoamaral.dspsp.adapters.SolutionConverter;
import net.rodrigoamaral.dspsp.exceptions.InvalidSolutionException;
import net.rodrigoamaral.dspsp.objectives.Efficiency;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.project.events.EventType;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.project.tasks.EffortParameters;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.*;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.max;
import static net.rodrigoamaral.util.DoubleUtils.sum;


public class DynamicProject {

    public static final int SCENARIO_SAMPLE_SIZE = 30;
    public static final double ROBUSTNESS_COST_WEIGHT = 1;
    public static final int K = 1;
    private static final int CROB = 100;
    double totalDuration;
    double totalCost;
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
    private List<Map<Integer, Double>> sampleEffortScenarios;
    private String instanceDescription;
    private List<Integer> lastAvailableEmployees;

    public DynamicProject() {
        tasks = new ArrayList<>();
        employees = new ArrayList<>();
        taskPrecedenceGraph = new DynamicTaskPrecedenceGraph(tasks.size());
        taskIndices = new HashMap<>();
        employeeIndices = new HashMap<>();
        taskProficiency = new HashMap<>();
        taskArrivalTimes = new ArrayList<>();
        sampleEffortScenarios = new ArrayList<>(SCENARIO_SAMPLE_SIZE);
        previousSchedule = null;
        lastSchedulingTime = 0;
        totalDuration = 0.0;
        totalCost = 0.0;
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

    public DedicationMatrix getPreviousSchedule() {
        return previousSchedule;
    }

    public void setPreviousSchedule(DoubleSolution previousSchedule) {
        this.previousSchedule = new SolutionConverter(this).convert(previousSchedule);
    }

    public List<Map<Integer, Double>> getSampleEffortScenarios() {
        return sampleEffortScenarios;
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
        for (DynamicTask task : tasks_) {
            cloned.add(new DynamicTask(task));
        }
        return cloned;
    }

    /**
     * Updates project state based on the dynamic event and current schedule
     *
     * @param lastSchedule
     * @param event
     */
    public void update(DynamicEvent event, DoubleSolution lastSchedule) {
        // REFACTOR: lastSchedule should be a DedicationMatrix to avoid dependencies with jMetal
        setPreviousSchedule(lastSchedule);
        setLastAvailableEmployees(availableEmployees);
        updateFinishedEffort(availableEmployees, event.getTime());
        updateCurrentStatus(event);
        setLastSchedulingTime(event.getTime());
    }

    public void updateCurrentStatus(DynamicEvent event) {
        updateEmployeeAvailability(event);
        updateTaskAvailability(event);
        updateCurrentStatus();
    }

    public void updateCurrentStatus() {
        availableEmployees = filterAvailableEmployees();
        availableTasks = filterAvailableTasks();
        activeTasks = filterActiveTasks();
        sampleEffortScenarios = generateEffortScenarioSample(availableTasks, SCENARIO_SAMPLE_SIZE);
        ////
        SPSPLogger.info("Available employees: " + availableEmployees);
        SPSPLogger.info("Available tasks: " + availableTasks);
        SPSPLogger.info("Active tasks: " + activeTasks);
        ////
    }

    private List<Map<Integer, Double>> generateEffortScenarioSample(List<DynamicTask> tasks, int scenarioCount) {

        List<Map<Integer, Double>> effortScenarioSample = new ArrayList<>(scenarioCount);

        for (int i = 0; i < scenarioCount; i++) {
            Map<Integer, Double> taskEffortSample = new HashMap<>(tasks.size());
            for (DynamicTask task : tasks) {
                double effortSample = TaskManager.generateEffortSample(task);
                taskEffortSample.put(task.index(), effortSample);
            }
            effortScenarioSample.add(taskEffortSample);
        }

        return effortScenarioSample;
    }

    private void updateEmployeeAvailability(DynamicEvent event) {
        int id = event.getSubject().getId();
        if (event.getType() == EventType.EMPLOYEE_LEAVE) {
            getEmployeeById(id).setAvailable(false);
        } else if (event.getType() == EventType.EMPLOYEE_RETURN) {
            getEmployeeById(id).setAvailable(true);
        }
    }

    private void updateTaskAvailability(DynamicEvent event) {
        List<Integer> incomingTasksIDs = getIncomingTasks(event);
        makeTasksAvailable(incomingTasksIDs, event);
    }

    private void makeTasksAvailable(List<Integer> incomingTaskIDs, DynamicEvent event) {
        int urgentTaskIndex = -1;
        if (event.getType() == EventType.NEW_URGENT_TASK) {
            if (!incomingTaskIDs.isEmpty()) {
                urgentTaskIndex = incomingTaskIDs.remove(incomingTaskIDs.size() - 1);
            }
        }
        for (int t : incomingTaskIDs) {
            List<Integer> predecessors = chooseRandomTasks();
            for (int p : predecessors) {
                taskPrecedenceGraph.addEdge(p, t);
                DynamicTask newTask = getTaskByIndex(t);
                boolean available = TaskManager.isAvailable(newTask, availableEmployees, this, this.getTaskPrecedenceGraph());
                newTask.setAvailable(available);
                ////
                SPSPLogger.info("Regular T_" + t + " added after T_" + p + " (T_"+p+" -> T_" + t +")");
                ////
            }
        }
        if (urgentTaskIndex > -1) {
            List<Integer> successors = chooseRandomTasks();
            for (int s : successors) {
                taskPrecedenceGraph.addEdge(urgentTaskIndex, s);
                DynamicTask newUrgentTask = getTaskByIndex(urgentTaskIndex);
                boolean available = TaskManager.isAvailable(newUrgentTask, availableEmployees, this, this.getTaskPrecedenceGraph());
                newUrgentTask.setAvailable(available);
                ////
                SPSPLogger.info("Urgent T_" + urgentTaskIndex + " added before T_" + s + " (T_"+ urgentTaskIndex +" -> T_" + s +")");
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

    private void updateFinishedEffort(List<DynamicEmployee> availableEmployees_, double currentTime) {

        Map<Integer, EffortParameters> efforts = new HashMap<>();

        double duration = 0.0;
        double effortDuration = 0.0;
        boolean durationBelowCurrentTime = true;

        DynamicTaskPrecedenceGraph localTPG = taskPrecedenceGraph.copy();
        List<DynamicTask> localAvailableTasks = cloneTasks(availableTasks);

        while ((!localTPG.isEmpty() || !localAvailableTasks.isEmpty()) && durationBelowCurrentTime) {
            List<DynamicTask> localActiveTasks = filterActiveTasks(localTPG, localAvailableTasks);

            if (localActiveTasks.isEmpty()) {
                SPSPLogger.debug("localAvailableTasks = " + localAvailableTasks);
                throw new RuntimeException("Problem instance not solvable!");
            }

            DedicationMatrix normalizedSchedule = normalize(this.previousSchedule, localActiveTasks);

            double partialDuration = Double.POSITIVE_INFINITY;


            for (DynamicTask localTask : localActiveTasks) {
                EffortParameters ep = TaskManager.getEffortProperties(localTask, availableEmployees_, normalizedSchedule);
                efforts.put(localTask.index(), ep);
                // Checking if active task had anyone really working on it
                if (ep.timeSpent > 0) {
                    partialDuration = Math.min(partialDuration, ep.timeSpent);
                }
            }

            duration += partialDuration;

            effortDuration = partialDuration;

            if (duration + lastSchedulingTime > currentTime) {
                effortDuration -= (duration + lastSchedulingTime - currentTime);
                durationBelowCurrentTime = false;
            }

            SPSPLogger.debug(
                    "partialDuration = " + partialDuration + "; " +
                    "effortDuration = " + effortDuration + "; " +
                    "delta = " + (currentTime - lastSchedulingTime)
            );


            // Updates finished effort for the local active tasks
            for (DynamicTask localTask : localActiveTasks) {

                EffortParameters ep = efforts.get(localTask.index());

                // Checks again if task was really active
                if (ep.timeSpent > 0) {
                    double finished = ep.finishedEffort(effortDuration);
                    localTask.addFinishedEffort(finished);
                    ////
                    SPSPLogger.debug("localTask " + localTask + " finished effort: (" + (localTask.getFinishedEffort() - finished) + " + " + finished + ") = " + localTask.getFinishedEffort());
                    ////
                }
            }

            // Calculates cost
            double partialCost = 0;

            for (DynamicTask localTask : localActiveTasks) {
                for (DynamicEmployee e : availableEmployees_) {
                    partialCost += taskCostByEmployee(e, localTask, normalizedSchedule, effortDuration);
                }
            }

            totalCost += partialCost;

            // Updates local tasks state
            if (durationBelowCurrentTime) {
                for (DynamicTask localTask : localActiveTasks) {
                    if (localTask.isFinished()) {
                        ////
                        SPSPLogger.debug("Local task " + localTask +" COMPLETE");
                        ////
                        localTPG.remove(localTask.index());
                        localAvailableTasks.remove(localTask);
                    }
                }
            }

        } // while

        // Update finished effort for the actual project active tasks
        for (DynamicTask globalTask : activeTasks) {

            EffortParameters ep = efforts.get(globalTask.index());

            // Checks once more if task was really active
            if (ep.timeSpent > 0) {
                globalTask.addFinishedEffort(ep.finishedEffort(effortDuration));
            }

            if (globalTask.isFinished()) {
                globalTask.setAvailable(false);
                taskPrecedenceGraph.remove(globalTask.index());
                ////
                SPSPLogger.info(
                        String.format(
                                Locale.US,
                                "%-4s is COMPLETE: %7.4f / %7.4f (%6.2f %%)",
                                globalTask,
                                globalTask.getFinishedEffort(),
                                globalTask.getEffort(),
                                globalTask.finishedEffortRatio() * 100
                        )
                );
                ////
            } else {
                globalTask.setEffort(reestimateEffort(globalTask));
                ////
                SPSPLogger.info(
                        String.format(
                                Locale.US,
                                "%-4s finished effort: %7.4f / %7.4f (%6.2f %%)",
                                globalTask,
                                globalTask.getFinishedEffort(),
                                globalTask.getEffort(),
                                globalTask.finishedEffortRatio() * 100
                        )
                );
                ////
            }
        }

        availableTasks = filterAvailableTasks();
        totalDuration = currentTime;
    }

    private double reestimateEffort(DynamicTask task) {

        double realEffort = task.getEffort();
        double finishedEffort = task.getFinishedEffort();

        while (realEffort <= finishedEffort) {
            realEffort = TaskManager.sampleEstimatedEffort(task);
        }

        return realEffort;
    }

    private DedicationMatrix normalize(DedicationMatrix dm, List<DynamicTask> activeTasks) {

        DedicationMatrix normalized = new DedicationMatrix(dm);

        for (DynamicTask t : activeTasks) {
            for (DynamicEmployee e : availableEmployees) {
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
        for (DynamicTask task : activeTasks) {
            result.add(dm.getDedication(employee.index(), task.index()));
        }
        return result;
    }

    private double taskCostByEmployee(DynamicEmployee e, DynamicTask t, DedicationMatrix solution, double duration) {
        double employeeDedication = solution.getDedication(e.index(), t.index());
        double regularCost = e.getSalary() * employeeDedication * duration;
        return regularCost + getOvertimeCost(e, employeeDedication - 1, duration);
    }

    private double getOvertimeCost(DynamicEmployee e, double overdedication, double duration) {
        double overtimeCost = e.getOvertimeSalary() * overdedication * duration;
        overtimeCost = overtimeCost > 0 ? overtimeCost : 0;
        return overtimeCost;
    }

    public List<DynamicEvent> getEvents() {
        return events;
    }

    public void setEvents(List<DynamicEvent> events) {
        this.events = events;
    }

    public double calculateRobustness(DedicationMatrix solution, Efficiency efficiency) throws InvalidSolutionException {

        List<Double> durationDistances = new ArrayList<>();
        List<Double> costDistances = new ArrayList<>();

        List<DynamicTask> scenarioAvailableTasks = cloneTasks(availableTasks);

        for (Map<Integer, Double> effortScenario : getSampleEffortScenarios()) {
            for (DynamicTask t : scenarioAvailableTasks) {
                t.setEffort(effortScenario.get(t.index()));
            }
            Efficiency scenario = evaluateEfficiency(solution, scenarioAvailableTasks);

            durationDistances.add(efficiencyDistance(scenario.duration, efficiency.duration));
            costDistances.add(efficiencyDistance(scenario.cost, efficiency.cost));
        }

        return Math.sqrt(avg(durationDistances)) + ROBUSTNESS_COST_WEIGHT * Math.sqrt(avg(costDistances));
    }

    public double calculateStability(DedicationMatrix solution) {

        if (previousSchedule == null) {
            return 0;
        }

        double stabilityValue = 0;
        for (DynamicEmployee e : availableEmployees) {
            for (DynamicTask t : availableTasks) {
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
            return 1.5;
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
        for (DynamicTask task : getTasks()) {
            if (task.isAvailable()) {
                availableTasks.add(task);
            }
        }
        return availableTasks;
    }

    private List<DynamicEmployee> filterAvailableEmployees() {
        List<DynamicEmployee> availableEmployees = new ArrayList<>();
        for (DynamicEmployee employee : getEmployees()) {
            if (employee.isAvailable()) {
                availableEmployees.add(employee);
            }
        }
        return availableEmployees;
    }

    private List<DynamicTask> filterActiveTasks() {
        List<DynamicTask> activeTasks = new ArrayList<>();
        for (int t : taskPrecedenceGraph.getIndependentTasks()) {
            DynamicTask task = getTaskByIndex(t);
            if (task.isAvailable()) {
                activeTasks.add(task);
            }
        }
        return activeTasks;
    }

    private List<DynamicTask> filterActiveTasks(DynamicTaskPrecedenceGraph tpg, List<DynamicTask> tasks) {
        List<DynamicTask> active = new ArrayList<>();

        for (DynamicTask task : tasks) {
            for (int t : tpg.getIndependentTasks()) {
                if (task.index() == t && task.isAvailable()) {
                    active.add(task);
                }
            }
        }

        return active;
    }

    public boolean isFinished() {

        for (DynamicTask task : getTasks()) {
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
        for (DynamicEmployee employee : this.getAvailableEmployees()) {
            if (employee.getMaxDedication() > max) {
                max = employee.getMaxDedication();
            }
        }
        return max;
    }

    public double getAvailableEmployeeMinDedication() {
        double min = POSITIVE_INFINITY;
        for (DynamicEmployee employee : this.getAvailableEmployees()) {
            if (employee.getMaxDedication() < min) {
                min = employee.getMaxDedication();
            }
        }
        return min;
    }

    public double getTotalEstimatedRemainingEffort() {
        double remainingEffort = 0;
        for (DynamicTask task : getAvailableTasks()) {
            remainingEffort += task.getRemainingEffort();
        }
        return remainingEffort;
    }

    /**
     * Returns the task team among the available employees at the moment
     * @param task
     * @param solution
     * @return
     */
    public List<DynamicEmployee> availableTaskTeam(DynamicTask task, DedicationMatrix solution) {
        return taskTeam(task, solution, availableEmployees);
    }

    public List<DynamicEmployee> taskTeam(DynamicTask task, DedicationMatrix solution, List<DynamicEmployee> employees) {
        List<DynamicEmployee> team = new ArrayList<>();
        for (int e : TaskManager.team(task, solution)) {
            DynamicEmployee teamEmployee = getEmployeeByIndex(e);
            for (DynamicEmployee filterEmployee: employees) {
                if (teamEmployee.index() == filterEmployee.index()) {
                    team.add(teamEmployee);
                }
            }
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

    public Efficiency evaluateEfficiency(DedicationMatrix dm) throws InvalidSolutionException {
        return evaluateEfficiency(dm, availableTasks);
    }

    public Efficiency evaluateEfficiency(DedicationMatrix dm, List<DynamicTask> tasks) throws InvalidSolutionException {
        double duration = 0;
        double cost = 0;
        double partialCost = 0;

        DynamicTaskPrecedenceGraph localTPG = taskPrecedenceGraph.copy();
        List<DynamicTask> localAvailableTasks = cloneTasks(tasks);


        // --------------------------
        // Repairing overhead (begin)
        // --------------------------

        //// First headcount repair heuristic
        dm = removeNonProficientEmployees(dm, localAvailableTasks);

        //// Second headcount repair heuristic
        for (DynamicTask t: localAvailableTasks) {
            final List<DynamicEmployee> taskTeam = availableTaskTeam(t, dm);
            List<DynamicEmployee> originalTeam = getSortedTeamByProficiencyInTask(taskTeam, t);
            List<DynamicEmployee> repairedTeam = new ArrayList<>(originalTeam);

            if (originalTeam.size() > t.getMaximumHeadcount()) {
                for (DynamicEmployee e: originalTeam) {
                    DynamicEmployee removed = repairedTeam.remove(0);
                    if (missingSkills(t, repairedTeam) == 0) {
                        dm.setDedication(e.index(), t.index(), 0);
                    } else {
                        repairedTeam.add(0, removed);
                    }
                }
            }

            // Penalizing task effort if max headcount constraint violated
            if (repairedTeam.size() > t.getMaximumHeadcount()) {
                t.setEffort(TaskManager.adjustedEffort(dm, t));
            }

        }
        // --------------------------
        // Repairing overhead (end)
        // --------------------------

        while (!localTPG.isEmpty() || !localAvailableTasks.isEmpty()) {

            List<DynamicTask> localActiveTasks = filterActiveTasks(localTPG, localAvailableTasks);

            if (localActiveTasks.isEmpty()) {
                SPSPLogger.debug("localAvailableTasks = " + localAvailableTasks);
                throw new RuntimeException("Problem instance not solvable!");
            }

            DedicationMatrix normDM = normalize(dm, localActiveTasks);

            // Duration calculation
            double partialDuration = Double.POSITIVE_INFINITY;

            for (DynamicTask localTask : localActiveTasks) {
                EffortParameters ep = TaskManager.getEffortProperties(localTask, availableEmployees, normDM);
                partialDuration = Math.min(partialDuration, ep.timeSpent);
                double finishedEffort = ep.finishedEffort(partialDuration);
                try {
                    localTask.addFinishedEffort(finishedEffort);
                } catch (IllegalArgumentException iae) {
                    SPSPLogger.trace(iae.getMessage());
                    throw new InvalidSolutionException();
                }
            }

            duration += partialDuration;

            // Cost calculation
            for (DynamicEmployee e : availableEmployees) {
                for (DynamicTask t : localActiveTasks) {
                    partialCost += taskCostByEmployee(e, t, normDM, partialDuration);
                }
            }

            cost += partialCost;

            // Finished tasks removal
            for (DynamicTask localTask : localActiveTasks) {
                if (localTask.isFinished()) {
                    localTask.setAvailable(false);
                    localTPG.remove(localTask.index());
                    localAvailableTasks.remove(localTask);
                }
            }
        }

        return new Efficiency(duration, cost);
    }

    /**
     * First headcount repair heuristic
     * @param dm
     * @param localAvailableTasks
     * @return
     */
    private DedicationMatrix removeNonProficientEmployees(DedicationMatrix dm, final List<DynamicTask> localAvailableTasks) {
        for (DynamicTask t: localAvailableTasks) {
            for (DynamicEmployee e : availableEmployees) {
                if (e.getProficiencyOnTask().get(t.index()) == 0) {
                    dm.setDedication(e.index(), t.index(), 0);
                }
            }
        }
        return dm;
    }

    public List<DynamicEmployee> getSortedTeamByProficiencyInTask(final List<DynamicEmployee> team, final DynamicTask task) {
        List<DynamicEmployee> sortedTeam = new ArrayList<>(team);

        Collections.sort(sortedTeam, new Comparator<DynamicEmployee>() {
            @Override
            public int compare(DynamicEmployee e1, DynamicEmployee e2) {
                double p1 = e1.getProficiencyOnTask().get(task.index());
                double p2 = e2.getProficiencyOnTask().get(task.index());

                if (p1 == p2) {
                    return 0;
                }
                return p1 < p2 ? -1 : 1;
            }
        });
        return sortedTeam;
    }

    public double penalizeDuration(int missingSkills) {
        double minEmployeeDedication = getAvailableEmployeeMinDedication();
        double estimatedRemainingEffort = getTotalEstimatedRemainingEffort();

        return 14 * K * missingSkills * estimatedRemainingEffort / minEmployeeDedication;
    }

    public double penalizeCost(int missingSkills) {
        double cost = 0;
        for (DynamicEmployee employee : getAvailableEmployees()) {
            for (DynamicTask task : getAvailableTasks()) {
                cost += employee.getOvertimeSalary() * task.getRemainingEffort();
            }
        }
        return 14 * missingSkills * cost;
    }

    public double penalizeRobustness(int missingSkills) {
        return 2 * CROB * missingSkills;
    }

    public double penalizeStability(int missingSkills) {
        int numberOfAvailableEmployees = getAvailableEmployees().size();
        int numberOfAvailableTasks = getAvailableTasks().size();
        double maxDedication = getAvailableEmployeeMaxDedication();

        return 2 * missingSkills * numberOfAvailableEmployees * numberOfAvailableTasks * maxDedication;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setInstanceDescription(String instanceDescription) {
        this.instanceDescription = instanceDescription;
    }

    public String getInstanceDescription() {
        return instanceDescription;
    }

    public List<Integer> getLastAvailableEmployees() {
        return this.lastAvailableEmployees;
    }

    private void setLastAvailableEmployees(List<DynamicEmployee> lastAvailableEmployees_) {
        List<Integer> employees = new ArrayList<>();
        for (DynamicEmployee emp: lastAvailableEmployees_) {
            employees.add(emp.index());
        }
        this.lastAvailableEmployees = employees;
    }
}
