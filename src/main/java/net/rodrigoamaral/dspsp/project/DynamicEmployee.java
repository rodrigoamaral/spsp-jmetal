package net.rodrigoamaral.dspsp.project;


import net.rodrigoamaral.dspsp.project.events.IEventSubject;

import java.util.HashMap;

public class DynamicEmployee extends net.rodrigoamaral.spsp.project.Employee implements IEventSubject {

    private HashMap<Integer, Double> skillsProficiency;
    private double overtimeSalary;
    private int originalIndex;
    private boolean available = true;
    private HashMap<Integer, Double> proficiencyOnTask;

    public DynamicEmployee(int id, float salary, float _overtimeSalary, int originalIndex) {
        super(id, salary);
        skillsProficiency = new HashMap<>();
        proficiencyOnTask= new HashMap<>();
        overtimeSalary = _overtimeSalary;
        this.originalIndex = originalIndex;
    }

    public HashMap<Integer, Double> getSkillsProficiency() {
        return skillsProficiency;
    }

    public HashMap<Integer, Double> getProficiencyOnTask() {
        return proficiencyOnTask;
    }

    public void setSkillsProficiency(HashMap<Integer, Double> skillsProficiency) {
        this.skillsProficiency = skillsProficiency;
    }

    public double getOvertimeSalary() {
        return overtimeSalary;
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
