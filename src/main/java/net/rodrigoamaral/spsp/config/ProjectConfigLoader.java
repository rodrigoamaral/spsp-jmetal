package net.rodrigoamaral.spsp.config;

import net.rodrigoamaral.spsp.graph.TaskPrecedenceGraph;
import net.rodrigoamaral.spsp.project.Employee;
import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.project.Task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project configuration loader from a properties file according
 * to the syntax defined by Alba and Chicano (2007)
 *
 * @author Rodrigo Amaral
 * @see <a href="http://tracer.lcc.uma.es/problems/psp/generator.html">http://tracer.lcc.uma.es/problems/psp/generator.html</a>
 */
public class ProjectConfigLoader {

    private Properties config;
    private Project project;

    /**
     * Loads a project configuration from a properties file
     *
     * @param configFile Relative path to the configuration file
     * @throws FileNotFoundException
     */
    public ProjectConfigLoader(String configFile) throws FileNotFoundException {
        config = loadFromFile(configFile);
    }

    /**
     * Creates a project from the configuration file
     *
     * @return a {@link Project} instance
     */
    public Project createProject() {
        project = new Project();
        loadTasks();
        loadEmployees();
        loadTaskPrecedenceGraph(project.getTasks());
        return project;
    }

    private static Properties loadFromFile(String fileName) throws FileNotFoundException {
        InputStream is = new FileInputStream(fileName);
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return prop;
    }

    private void loadTasks() {
        for (Map.Entry<Object, Object> e : config.entrySet()) {
            Task t = loadTask(e);
            if (t != null) {
                project.getTasks().put(t.getId(), t);
            }
        }
        for (Map.Entry<Object, Object> e : config.entrySet()) {
            loadTaskSkill(e);
        }
    }

    private Task loadTask(Map.Entry<Object, Object> e) {
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

    private void loadTaskSkill(Map.Entry<Object, Object> e) {
        String pattern = "^(task)(\\.)(\\d+)(\\.)(skill)(\\.)(\\d+)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getKey().toString());
        if (m.find()) {
            int id = Integer.parseInt(m.group(3));
            int skill = Integer.parseInt(e.getValue().toString());
            Task t = project.getTasks().get(id);
            t.getSkills().add(skill);
        }
    }

    private void loadEmployees() {
        for (Map.Entry<Object, Object> e : config.entrySet()) {
            Employee emp = loadEmployee(e);
            if (emp != null) {
                project.getEmployees().put(emp.getId(), emp);
            }
        }
        for (Map.Entry<Object, Object> e : config.entrySet()) {
            loadEmployeeSkill(e);
        }
    }

    private Employee loadEmployee(Map.Entry<Object, Object> e) {
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

    private void loadEmployeeSkill(Map.Entry<Object, Object> e) {
        String pattern = "^(employee)(\\.)(\\d+)(\\.)(skill)(\\.)(\\d+)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getKey().toString());
        if (m.find()) {
            int id = Integer.parseInt(m.group(3));
            int skill = Integer.parseInt(e.getValue().toString());
            Employee emp = project.getEmployees().get(id);
            emp.getSkills().add(skill);
        }
    }

    private void loadTaskPrecedenceGraph(Map<Integer, Task> taskMap) {
        project.setTaskPrecedenceGraph(new TaskPrecedenceGraph(taskMap.size()));
        for (Map.Entry<Object, Object> e : config.entrySet()) {
            String strEdge = loadTPGEdge(e);
            if (strEdge != null) {
                String[] vertices = strEdge.split("\\s+");
                project.getTaskPrecedenceGraph().addEdge(
                        Integer.parseInt(vertices[0]),
                        Integer.parseInt(vertices[1])
                );
            }
        }
    }

    private String loadTPGEdge(Map.Entry<Object, Object> e) {
        String pattern = "^(graph)(\\.)(arc)(\\.)(\\d+)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getKey().toString());
        if (m.find()) {
            return e.getValue().toString();
        }
        return null;
    }
}
