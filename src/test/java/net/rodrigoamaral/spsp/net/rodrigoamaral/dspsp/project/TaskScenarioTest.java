package net.rodrigoamaral.spsp.net.rodrigoamaral.dspsp.project;


import net.rodrigoamaral.dspsp.project.DynamicTask;
import net.rodrigoamaral.dspsp.scenarios.TaskScenario;
import net.rodrigoamaral.dspsp.scenarios.TaskScenarioFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class TaskScenarioTest {

    private TaskScenarioFactory scenarioFactory;

    @Before
    public void setUp() {
        scenarioFactory = new TaskScenarioFactory();
    }

    @Test
    public void testCreateEmptyTaskScenario() {
        TaskScenario ts = scenarioFactory.create(new ArrayList<DynamicTask>());
        assertNotNull(ts);
    }

    @Test
    public void testCreateTaskScenarioSize1() {
        List<DynamicTask> tasks = new ArrayList<>();
        tasks.add(new DynamicTask(1, 1));
        TaskScenario ts = scenarioFactory.create(tasks);
        assertEquals(1, ts.size());
    }

    @Test
    public void testCreateTaskScenarioSize5() {
        List<DynamicTask> tasks = new ArrayList<>();
        tasks.add(new DynamicTask(1, 1));
        tasks.add(new DynamicTask(2, 2));
        tasks.add(new DynamicTask(3, 3));
        tasks.add(new DynamicTask(4, 4));
        tasks.add(new DynamicTask(5, 5));
        TaskScenario ts = scenarioFactory.create(tasks);
        assertEquals(5, ts.size());
    }

    @Test
    public void testTaskScenarioConstructorFromTaskList() {
        List<DynamicTask> tasks = new ArrayList<>();
        tasks.add(new DynamicTask(1, 1));
        tasks.add(new DynamicTask(2, 2));
        TaskScenario ts = new TaskScenario(tasks);
        assertEquals(2, ts.size());
    }

    @Test
    public void testGetTaskEfforts() {
        List<DynamicTask> tasks = new ArrayList<>();
        tasks.add(new DynamicTask(1, 1));
        tasks.add(new DynamicTask(2, 2));
        TaskScenario ts = new TaskScenario(tasks);
        double[] remainingTaskEfforts = ts.getRemainingTaskEfforts();
        assertNotNull(remainingTaskEfforts);
        assertEquals(2, remainingTaskEfforts.length);
    }

}
