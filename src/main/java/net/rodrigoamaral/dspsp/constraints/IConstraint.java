package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.List;


public interface IConstraint {
    boolean isViolated(DynamicProject project, DedicationMatrix dm);
    double violationDegree(DynamicProject project, DedicationMatrix dm);
    DedicationMatrix repair(DedicationMatrix dm, DynamicProject project);
}
