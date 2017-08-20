package net.rodrigoamaral.spsp.project;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Represents an employee and its attriubtes according to SPSP definition.
 *
 * @author Rodrigo Amaral
 *
 */
public class Employee {
    private int id;
    private double salary;
    private List<Integer> skills;
    private double maxDedication;

    public Employee(int id, float salary) {
        this.id = id;
        this.salary = salary;
        this.skills = new ArrayList<>();
        this.maxDedication = 1.0;
    }

    public int getId() {
        return id;
    }

    public double getSalary() {
        return salary;
    }

    public List<Integer> getSkills() {
        return skills;
    }

    public void setSkills(List<Integer> skills) {
        this.skills = skills;
    }

    public double getMaxDedication() {
        return maxDedication;
    }

    @Override
    public String toString() {
        return "DynamicEmployee{" +
                "id=" + id +
                ", salary=" + salary +
                ", skills=" + skills +
                ", maxDedication=" + maxDedication +
                '}';
    }
}
