package net.rodrigoamaral.spsp.project;

import net.rodrigoamaral.spsp.constraints.IConstraintEvaluator;
import net.rodrigoamaral.spsp.constraints.SPSPConstraintEvaluator;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;
import net.rodrigoamaral.spsp.solution.SolutionEncoder;
import net.rodrigoamaral.spsp.graph.TaskPrecedenceGraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representa uma instância do projeto carregada a partir dos dados
 * do arquivo de properties definido por (ALBA; CHICANO, 2007)
 */
public class Project {

    private Map<Integer, Task> tasks;
    private Map<Integer, Employee> employees;
    private TaskPrecedenceGraph taskPrecedenceGraph;
    private IConstraintEvaluator constraintEvaluator;

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Employee> getEmployees() {
        return employees;
    }

    public TaskPrecedenceGraph getTaskPrecedenceGraph() {
        return taskPrecedenceGraph;
    }

    public Project() {
        tasks = new HashMap<>();
        employees = new HashMap<>();
        taskPrecedenceGraph = new TaskPrecedenceGraph(tasks.size());
//        constraintEvaluator = new SPSPConstraintEvaluator();
    }

//    public Project(IConstraintEvaluator evaluator) {
//        tasks = new HashMap<>();
//        employees = new HashMap<>();
//        taskPrecedenceGraph = new TaskPrecedenceGraph(tasks.size());
//        constraintEvaluator = evaluator;
//    }

    public Project(String configFile) {
        // TODO: Move file reading and loading operations to its own class
        Properties data = loadFromFile(configFile);
        loadTasks(data);
        loadEmployees(data);
        loadTaskPrecedenceGraph(data, tasks);
    }

    private void loadTaskPrecedenceGraph(Properties data, Map<Integer, Task> taskMap) {
        taskPrecedenceGraph = new TaskPrecedenceGraph(taskMap.size());
        for (Entry<Object, Object> e : data.entrySet()) {
            String strEdge = loadTPGEdge(e);
            if (strEdge != null) {
                String[] vertices = strEdge.split("\\s+");;
                taskPrecedenceGraph.addEdge(
                        Integer.parseInt(vertices[0]),
                        Integer.parseInt(vertices[1])
                );
            }
        }
    }

    private void loadEmployees(Properties data) {
        employees = new HashMap<>();
        for (Entry<Object, Object> e : data.entrySet()) {
            Employee emp = loadEmployee(e);
            if (emp != null) {
                employees.put(emp.getId(), emp);
            }
        }
        for (Entry<Object, Object> e : data.entrySet()) {
            loadEmployeeSkill(e);
        }
    }

    private void loadEmployeeSkill(Entry<Object, Object> e) {
        String pattern = "^(employee)(\\.)(\\d+)(\\.)(skill)(\\.)(\\d+)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getKey().toString());
        if (m.find()) {
            int id = Integer.parseInt(m.group(3));
            int skill = Integer.parseInt(e.getValue().toString());
            Employee emp = getEmployees().get(id);
            emp.getSkills().add(skill);
        }
    }

    private void loadTasks(Properties data) {
        tasks = new HashMap<>();
        for (Entry<Object, Object> e : data.entrySet()) {
            Task t = loadTask(e);
            if (t != null) {
                tasks.put(t.getId(), t);
            }
        }
        for (Entry<Object, Object> e : data.entrySet()) {
            loadTaskSkill(e);
        }
    }

    public int size() {
        return getEmployees().size() * getTasks().size();
    }

    private Task loadTask(Entry<Object, Object> e) {
        String pattern = "^(task)(\\.)(\\d+)(\\.)(cost)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getKey().toString());
        if (m.find()) {
            int id = Integer.parseInt(m.group(3));
            float cost = Float.parseFloat(e.getValue().toString());
            return new Task(id, cost) ;
        }
        return null;
    }

    private void loadTaskSkill(Entry<Object, Object> e) {
//        task.9.skill.1=0
        String pattern = "^(task)(\\.)(\\d+)(\\.)(skill)(\\.)(\\d+)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getKey().toString());
        if (m.find()) {
            int id = Integer.parseInt(m.group(3));
            int skill = Integer.parseInt(e.getValue().toString());
            Task t = getTasks().get(id);
            t.getSkills().add(skill);
        }
    }

    private Employee loadEmployee(Entry<Object, Object> e) {
        String pattern = "^(employee)(\\.)(\\d+)(\\.)(salary)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getKey().toString());
        if (m.find()) {
            int id = Integer.parseInt(m.group(3));
            float salary = Float.parseFloat(e.getValue().toString());
            return new Employee(id, salary) ;
        }
        return null;
    }


    private String loadTPGEdge(Entry<Object, Object> e) {
        String pattern = "^(graph)(\\.)(arc)(\\.)(\\d+)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getKey().toString());
        if (m.find()) {
            return e.getValue().toString();
        }
        return null;
    }

    private Properties loadFromFile(String fileName) {
        // TODO: 02/09/16 Allow data loading from any file, not only the ones in resource folders
        InputStream is = getClass().getResourceAsStream("/" + fileName);
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public void setTasksDuration(DedicationMatrix dm) {
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

    public void setTasksStartAndFinish(DedicationMatrix dm) {
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

    public static void main(String[] args) {
        Project p = new Project("sps-config-file2.properties");
        System.out.println(p);
    }


}
