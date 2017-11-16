package net.rodrigoamaral.spsp.net.rodrigoamaral.dspsp.project;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import org.junit.Test;

import static org.junit.Assert.*;

public class DynamicEmployeeTest {
    @Test
    public void newDynamicEmployee() {
        DynamicEmployee e = new DynamicEmployee(1, 1000);
        assertNotNull(e);
    }

    @Test
    public void newDynamicEmployeeOvertime() {
        DynamicEmployee e = new DynamicEmployee(1, 1000, 1200, 0);
        assertNotNull(e);
        assertEquals(1200, e.getOvertimeSalary(), 0);
    }
}
