package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;


public interface IConstraint {
    boolean isViolated(DynamicProject project, DedicationMatrix s);
    double violationDegree(DynamicProject project, DedicationMatrix s);
}
