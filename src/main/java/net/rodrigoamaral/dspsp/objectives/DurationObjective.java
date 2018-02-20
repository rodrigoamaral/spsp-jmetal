package net.rodrigoamaral.dspsp.objectives;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

/**
 * Created by rodrigo on 08/03/17.
 */
public class DurationObjective implements IObjective {

    // REVIEW: We're dismissing the influence of K in this implementation by setting it to 1. Is this correct?
    public static final int K = 1;

    @Override
    public double evaluate(DynamicProject project, DedicationMatrix solution) {
        return project.evaluateEfficiency(solution).duration;
    }

    @Override
    public double penalize(DynamicProject project, DedicationMatrix solution, int missingSkills) {

        double minEmployeeDedication = project.getAvailableEmployeeMinDedication();
        double estimatedRemainingEffort = project.getTotalEstimatedRemainingEffort();

        return 14 * K * missingSkills *  estimatedRemainingEffort / minEmployeeDedication ;
    }
}
