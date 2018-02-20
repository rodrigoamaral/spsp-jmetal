package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

public interface IConstraintEvaluator {
    IConstraintEvaluator addConstraint(IConstraint constraint);
    DedicationMatrix repair(DedicationMatrix dm, DynamicProject project);
    int size();
}
