package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;


public interface IObjective {
    double evaluate(DynamicProject project, DedicationMatrix solution);
}
