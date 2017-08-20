package net.rodrigoamaral.spsp.net.rodrigoamaral.dspsp.project;

import net.rodrigoamaral.dspsp.project.Employee;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeTest {
    @Test
    public void newDynamicEmployee() {
        Employee e = new Employee(1, 1000);
        assertNotNull(e);
    }

    @Test
    public void newDynamicEmployeeOvertime() {
        Employee e = new Employee(1, 1000, 1200);
        assertNotNull(e);
        assertEquals(1200, e.getOvertimeSalary(), 0);
    }
}
