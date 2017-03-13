package net.rodrigoamaral.spsp.objectives;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

/**
 * Created by rodrigo on 04/03/17.
 */
public interface IObjective {
    double evaluate(Project project, DedicationMatrix solution);
}
