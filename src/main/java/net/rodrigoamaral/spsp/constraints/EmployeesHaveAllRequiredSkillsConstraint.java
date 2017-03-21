package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

import java.util.Collections;

/**
 *
 * Constraint 2: The set of required skills of a task must be included in the
 * union of the skills of the employees performing the task.
 *
 */
public class EmployeesHaveAllRequiredSkillsConstraint implements IConstraint {
    @Override
    public boolean isViolated(Project project, DedicationMatrix s) {
        return violationDegree(project, s) > 0;
    }

    @Override
    public double violationDegree(Project project, DedicationMatrix s) {
        double degree = 0.0;
        for (int i = 0; i < s.getEmployees(); i++) {
            for (int j = 0; j < s.getTasks(); j++) {
                if (s.getDedication(i, j) > 0) {
                    for (Integer skill: project.getTasks().get(j).getSkills()) {
                        if (!hasSkill(project, i, skill)) {
                            degree = degree + 1;
                        }
                    }
                }
            }
        }
        return degree;
    }

    private boolean hasSkill(Project project, int employee, Integer skill) {
        return Collections.binarySearch(project.getEmployees().get(employee).getSkills(), skill) >= 0;
    }
}
