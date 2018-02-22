package net.rodrigoamaral.spsp.objectives;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

public interface IObjective {
    double evaluate(Project project, DedicationMatrix solution);
}
