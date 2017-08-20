package net.rodrigoamaral.spsp.net.rodrigoamaral.dspsp.project;

import net.rodrigoamaral.dspsp.project.DynamicTask;
import org.junit.Test;

import static org.junit.Assert.*;

public class DynamicTaskTest {
    @Test
    public void newDynamicTask() {
        DynamicTask t = new DynamicTask(1, 3.0);
        assertNotNull(t);
    }

}
