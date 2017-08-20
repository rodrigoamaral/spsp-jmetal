package net.rodrigoamaral.dspsp.project;


import java.util.ArrayList;
import java.util.List;

public class DynamicEmployee extends net.rodrigoamaral.spsp.project.Employee {

    private List skillsProficiency;
    private float overtimeSalary;

    public DynamicEmployee(int id, float salary) {
        super(id, salary);
        skillsProficiency = new ArrayList();
    }

    public DynamicEmployee(int id, float salary, float _overtimeSalary) {
        super(id, salary);
        skillsProficiency = new ArrayList();
        overtimeSalary = _overtimeSalary;
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
}
