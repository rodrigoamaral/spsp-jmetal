package net.rodrigoamaral.dspsp.config;

import net.rodrigoamaral.dspsp.project.*;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.project.events.EventType;
import net.rodrigoamaral.dspsp.project.events.IEventSubject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.util.instances.DynamicInstance;
import net.rodrigoamaral.dspsp.util.instances.InstanceParser;
import net.rodrigoamaral.spsp.project.Project;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rodrigo Amaral
 *
 */
public class DynamicProjectConfigLoader {

    private DynamicInstance config;
    private DynamicProject project;

    /**
     * Loads a dynamic project configuration from a json file
     *
     * @param configFile Relative path to the configuration file
     * @throws FileNotFoundException
     */
    public DynamicProjectConfigLoader(String configFile) throws FileNotFoundException {
        config = loadFromFile(configFile);
    }

    private static DynamicInstance loadFromFile(String fileName) throws FileNotFoundException {
        InstanceParser parser = new InstanceParser();
        return parser.parse(fileName);
    }

    /**
     * Creates a project from the configuration file
     *
     * @return a {@link Project} instance
     */
    public DynamicProject createProject() {
        project = new DynamicProject();
        loadTasks();
        loadEmployees();
        loadTaskPrecedenceGraph(project.getTasks());
        loadAvailableDisconnectedTasks();
        loadEventTimeline();
        loadTaskProficiency();
        project.updateCurrentStatus();
        return project;
    }

    private void loadAvailableDisconnectedTasks() {
        for (int t: project.getTaskPrecedenceGraph().getDisconnectedTasks()) {
            if (t < config.getTask_number()) {
                DynamicTask newTask = project.getTaskByIndex(t);
                boolean available = TaskManager.isAvailable(newTask, project.getEmployees(), project);
                newTask.setAvailable(available);
            }
        }
    }

    private void loadTasks() {

        for (int taskIndex = 0; taskIndex < config.getTask_effort_real_secnario_total().size(); taskIndex++) {
            DynamicTask t = loadTask(taskIndex);
            if (t != null) {
                project.getTasks().add(t);
                project.getTaskIndices().put(t.getId(), t.index());
            }
            loadTaskSkill(taskIndex);
        }
        for (double time: config.getArrival_time()) {
            project.getTaskArrivalTimes().add(time);
        }
    }


    private DynamicTask loadTask(int taskIndex) {
        int taskId = taskIndex + 1;
        double initialEstimatedEffort = config.getTask_effort_mu_total().get(taskIndex);
        double realEffort = config.getTask_effort_real_secnario_total().get(taskIndex);
        double effortDeviation = config.getTask_effort_sigma_total().get(taskIndex);
        int maximumHeadcount = config.getTask_headcount_total().get(taskIndex);
        return new DynamicTask(taskId,
                               initialEstimatedEffort,
                               realEffort,
                               effortDeviation,
                               taskIndex,
                               maximumHeadcount);
    }

    private void loadTaskSkill(int taskIndex) {
        List<Integer> skills = config.getTask_skill_set_total().get(taskIndex);
        DynamicTask t = project.getTasks().get(taskIndex);
        for (Integer skill: skills) {
            t.getSkills().add(skill);
        }
    }

    private void loadEmployees() {
        for (int employeeIndex = 0; employeeIndex < config.getAvailable_employee().size(); employeeIndex++) {
            DynamicEmployee emp = loadEmployee(employeeIndex);
            if (emp != null) {
                project.getEmployees().add(emp);
                project.getEmployeeIndices().put(emp.getId(), emp.index());
            }
            loadEmployeeSkill(employeeIndex);
        }
    }

    private DynamicEmployee loadEmployee(int employeeIndex) {
        int employeeId = config.getAvailable_employee().get(employeeIndex);
        float salary = config.getEmployee_salary().get(employeeIndex);
        float overtimeSalary = config.getEmployee_salary_over().get(employeeIndex);
        double maxDedication = config.getEmployee_maxded().get(employeeIndex);

        DynamicEmployee emp = new DynamicEmployee(employeeId, salary, overtimeSalary, employeeIndex);
        emp.setMaxDedication(maxDedication);
        return emp;
    }

    private void loadEmployeeSkill(int employeeIndex) {
        List<Integer> skills = config.getEmployee_skill_set().get(employeeIndex);
        List<Double> skillsProficiency = config.getEmployee_skill_proficieny_set().get(employeeIndex);

        DynamicEmployee emp = project.getEmployees().get(employeeIndex);
        emp.setSkills(skills);
        emp.setSkillsProficiency(skillsProficiency);
    }

    private void loadTaskPrecedenceGraph(List<DynamicTask> tasks) {
        int initialSize = config.getTask_effort_real_secnario_total().size();
        project.setTaskPrecedenceGraph(new DynamicTaskPrecedenceGraph(initialSize));
        for (List<Integer> edge : config.getEdge_set()) {
            DynamicTask t1 = project.getTaskById(edge.get(0));
            DynamicTask t2 = project.getTaskById(edge.get(1));
            t1.setAvailable(true);
            t2.setAvailable(true);
            project.getTaskPrecedenceGraph().addEdge(t1.index(),
                                                     t2.index());
        }
    }

    private void loadEventTimeline() {
        int numberOfEvents = config.getDynamic_time().size();
        List<DynamicEvent> events = new ArrayList<>(numberOfEvents);
        for (int i = 0; i < numberOfEvents; i++) {
            double time = config.getDynamic_time().get(i);
            int eventCode = config.getDynamic_class().get(i);
            int urgentTaskId = config.getDynamic_rushjob_number().get(i) + config.getTask_number();
            int leavingEmployeeId = config.getDynamic_labour_leave_number().get(i);
            int returningEmployeeId = config.getDynamic_labour_return_number().get(i);
//            double arrivalTime = config.getArrival_time().get(i);
            EventType type = EventType.valueOf(eventCode);
            IEventSubject subject = null;
            switch (type) {
                case NEW_URGENT_TASK:
                    subject = project.getTaskById(urgentTaskId);
                    break;
                case EMPLOYEE_LEAVE:
                    subject = project.getEmployeeById(leavingEmployeeId);
                    break;
                case EMPLOYEE_RETURN:
                    subject = project.getEmployeeById(returningEmployeeId);
                    break;
            }
            events.add(new DynamicEvent(i, time, type, subject));
        }
        project.setEvents(events);
    }

    private void loadTaskProficiency() {
        for (DynamicEmployee employee: project.getEmployees()) {
            int i = employee.index();
            project.getTaskProficiency().put(i, config.getTask_Proficieny_total().get(i));
        }
    }

}
