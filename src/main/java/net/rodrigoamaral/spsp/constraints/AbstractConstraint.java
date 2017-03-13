package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Employee;
import net.rodrigoamaral.spsp.project.Task;

import java.util.Map;

/**
 * Created by rodrigo on 03/03/17.
 */
public abstract class AbstractConstraint implements IConstraint {
    private Map<Integer, Task> tasks;
    private Map<Integer, Employee> employees;

    @Override
    public void setTasks(Map<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void setEmployees(Map<Integer, Employee> employees) {
        this.employees = employees;
    }
}
