package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

/**
 * Created by rodrigo on 02/03/17.
 */
public interface IConstraint {
    boolean isViolated(Project project, DedicationMatrix s);
    double violationDegree(Project project, DedicationMatrix s);
}
