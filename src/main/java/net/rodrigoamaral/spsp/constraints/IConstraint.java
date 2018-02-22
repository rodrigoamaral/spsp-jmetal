package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

public interface IConstraint {
    boolean isViolated(Project project, DedicationMatrix s);
    double violationDegree(Project project, DedicationMatrix s);
}
