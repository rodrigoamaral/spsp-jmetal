package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

public interface IConstraintEvaluator {
    double overallConstraintViolationDegree(Project project, DedicationMatrix s);
    int numberOfViolatedConstraints(Project project, DedicationMatrix s);
    IConstraintEvaluator addConstraint(IConstraint constraint);
    int size();
}
