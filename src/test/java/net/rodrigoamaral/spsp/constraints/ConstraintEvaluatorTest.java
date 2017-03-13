package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Employee;
import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.project.Task;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by rodrigo on 02/03/17.
 */
public class ConstraintEvaluatorTest {

    private IConstraintEvaluator evaluator;

    @Before
    public void setUp() {
        evaluator = new SPSPConstraintEvaluator();
        evaluator.addConstraint(new AllTasksAllocatedConstraint())
                 .addConstraint(new EmployeesHaveAllRequiredSkillsConstraint());
    }

    @Test
    public void testNoViolatedConstraints() {
        Project p = new Project();
        p.getTasks().put(0, new Task(0, 10.0));
        p.getTasks().put(1, new Task(1, 20.0));
        p.getTasks().put(2, new Task(2, 30.0));
        p.getTasks().get(0).setSkills(Arrays.asList(0, 1, 2));
        p.getTasks().get(1).setSkills(Arrays.asList(0, 1));
        p.getTasks().get(2).setSkills(Arrays.asList(1, 2));
        p.getEmployees().put(0, new Employee(0, 1000));
        p.getEmployees().put(1, new Employee(0, 2000));
        p.getEmployees().put(2, new Employee(0, 3000));
        p.getEmployees().get(0).setSkills(Arrays.asList(0, 1, 2));
        p.getEmployees().get(1).setSkills(Arrays.asList(0, 1, 2));
        p.getEmployees().get(2).setSkills(Arrays.asList(0, 1, 2));
        DedicationMatrix s = new DedicationMatrix(3, 3);
        for (int i = 0; i < s.getEmployees(); i++) {
            for (int j = 0; j < s.getTasks(); j++) {
                s.setDedication(i, j, 0.5);
            }

        }
        assertEquals(0.0, evaluator.overallConstraintViolationDegree(p, s), 0);
        assertEquals(0, evaluator.numberOfViolatedConstraints(p, s));
    }

    @Test
    public void testViolatesAllTasksAllocatedConstraint() {
        Project p = new Project();
        p.getTasks().put(0, new Task(0, 10.0));
        p.getTasks().put(1, new Task(1, 20.0));
        p.getTasks().put(2, new Task(2, 30.0));
        p.getTasks().get(0).setSkills(Arrays.asList(0, 1, 2));
        p.getTasks().get(1).setSkills(Arrays.asList(0, 1));
        p.getTasks().get(2).setSkills(Arrays.asList(1, 2));
        p.getEmployees().put(0, new Employee(0, 1000));
        p.getEmployees().put(1, new Employee(0, 2000));
        p.getEmployees().put(2, new Employee(0, 3000));
        p.getEmployees().get(0).setSkills(Arrays.asList(0, 1, 2));
        p.getEmployees().get(1).setSkills(Arrays.asList(0, 1, 2));
        p.getEmployees().get(2).setSkills(Arrays.asList(0, 1, 2));

        DedicationMatrix s = new DedicationMatrix(3, 3);
        s.setDedication(0, 0, 0.0);
        s.setDedication(1, 0, 0.0);
        s.setDedication(2, 0, 0.0);
        s.setDedication(0, 1, 0.5);
        s.setDedication(1, 1, 0.5);
        s.setDedication(2, 1, 0.5);
        s.setDedication(0, 2, 0.5);
        s.setDedication(1, 2, 0.5);
        s.setDedication(2, 2, 0.5);
        assertNotEquals(0, evaluator.overallConstraintViolationDegree(p, s), 0);
        assertNotEquals(0, evaluator.numberOfViolatedConstraints(p, s));
    }

    @Test
    public void testViolatesEmployeesHaveAllRequiredSkillsConstraint() {
        Project p = new Project();
        p.getTasks().put(0, new Task(0, 10.0));
        p.getTasks().put(1, new Task(1, 20.0));
        p.getTasks().put(2, new Task(2, 30.0));
        p.getTasks().get(0).setSkills(Arrays.asList(0, 1, 2, 3));
        p.getTasks().get(1).setSkills(Arrays.asList(0, 1));
        p.getTasks().get(2).setSkills(Arrays.asList(1, 2));
        p.getEmployees().put(0, new Employee(0, 1000));
        p.getEmployees().put(1, new Employee(0, 2000));
        p.getEmployees().put(2, new Employee(0, 3000));
        p.getEmployees().get(0).setSkills(Arrays.asList(0, 1, 2));
        p.getEmployees().get(1).setSkills(Arrays.asList(0, 1, 2));
        p.getEmployees().get(2).setSkills(Arrays.asList(0, 1, 2));

        DedicationMatrix s = new DedicationMatrix(3,3);
        s.setDedication(0, 0, 0.5);
        s.setDedication(1, 0, 0.5);
        s.setDedication(2, 0, 0.5);
        s.setDedication(0, 1, 0.5);
        s.setDedication(1, 1, 0.5);
        s.setDedication(2, 1, 0.5);
        s.setDedication(0, 2, 0.5);
        s.setDedication(1, 2, 0.5);
        s.setDedication(2, 2, 0.5);

        assertNotEquals(0, evaluator.overallConstraintViolationDegree(p, s), 0);
        assertNotEquals(0, evaluator.numberOfViolatedConstraints(p, s));
    }

}
