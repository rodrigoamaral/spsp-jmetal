package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

/**
 * Created by rodrigo on 02/03/17.
 */
public interface IConstraintEvaluator {
    double overallConstraintViolationDegree(DynamicProject project, DedicationMatrix s);
    int numberOfViolatedConstraints(DynamicProject project, DedicationMatrix s);
    IConstraintEvaluator addConstraint(IConstraint constraint);
    DedicationMatrix repair(DedicationMatrix dm, DynamicProject project);
    int size();
}
