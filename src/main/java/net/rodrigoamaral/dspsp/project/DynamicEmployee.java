package net.rodrigoamaral.dspsp.project;


import net.rodrigoamaral.dspsp.project.events.IEventSubject;

import java.util.ArrayList;
import java.util.List;

public class DynamicEmployee extends net.rodrigoamaral.spsp.project.Employee implements IEventSubject {

    private List<Double> skillsProficiency;
    private double overtimeSalary;
    private int originalIndex;
    private boolean available = true;

    public DynamicEmployee(int id, float salary) {
        super(id, salary);
        skillsProficiency = new ArrayList();
    }

    public DynamicEmployee(int id, float salary, float _overtimeSalary, int originalIndex) {
        super(id, salary);
        skillsProficiency = new ArrayList();
        overtimeSalary = _overtimeSalary;
        this.originalIndex = originalIndex;
    }

    public DynamicEmployee(DynamicEmployee employee) {
        super(employee.getId(), employee.getSalary());
        skillsProficiency = new ArrayList<>();
        for (double skp: employee.getSkillsProficiency()) {
            skillsProficiency.add(skp);
        }
        skillsProficiency = employee.getSkillsProficiency();
        overtimeSalary = employee.getOvertimeSalary();
        originalIndex = employee.index();
    }

    public List<Double> getSkillsProficiency() {
        return skillsProficiency;
    }

    public void setSkillsProficiency(List skillsProficiency) {
        this.skillsProficiency = skillsProficiency;
    }

    public double getOvertimeSalary() {
        return overtimeSalary;
    }

    public void setOvertimeSalary(float overtimeSalary) {
        this.overtimeSalary = overtimeSalary;
    }

    public int index() {
        return originalIndex;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "E_" + index();
    }
}
