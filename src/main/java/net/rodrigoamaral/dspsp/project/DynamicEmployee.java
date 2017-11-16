package net.rodrigoamaral.dspsp.project;


import net.rodrigoamaral.dspsp.project.events.IEventSubject;

import java.util.ArrayList;
import java.util.List;

public class DynamicEmployee extends net.rodrigoamaral.spsp.project.Employee implements IEventSubject {

    private List skillsProficiency;
    private float overtimeSalary;
    private int originalIndex;

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

    public List getSkillsProficiency() {
        return skillsProficiency;
    }

    public void setSkillsProficiency(List skillsProficiency) {
        this.skillsProficiency = skillsProficiency;
    }

    public float getOvertimeSalary() {
        return overtimeSalary;
    }

    public void setOvertimeSalary(float overtimeSalary) {
        this.overtimeSalary = overtimeSalary;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }
}
