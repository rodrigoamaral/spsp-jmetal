package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;


public interface IConstraint {
    DedicationMatrix repair(DedicationMatrix dm, DynamicProject project);
}
