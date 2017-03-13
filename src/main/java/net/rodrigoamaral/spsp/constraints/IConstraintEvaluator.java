package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

/**
 * Created by rodrigo on 02/03/17.
 */
public interface IConstraintEvaluator {
    double overallConstraintViolationDegree(Project project, DedicationMatrix s);
    int numberOfViolatedConstraints(Project project, DedicationMatrix s);
    IConstraintEvaluator addConstraint(IConstraint constraint);
    int size();
}
