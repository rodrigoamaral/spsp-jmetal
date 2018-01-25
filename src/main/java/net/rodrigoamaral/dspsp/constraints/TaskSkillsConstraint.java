package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;


public class TaskSkillsConstraint implements IConstraint {
    @Override
    public boolean isViolated(DynamicProject project, DedicationMatrix dm) {
        return violationDegree(project, dm) > 0;
    }

    @Override
    public double violationDegree(DynamicProject project,
                                  DedicationMatrix dm) {
        return project.missingSkills();
    }

    @Override
    public DedicationMatrix repair(DedicationMatrix dm, DynamicProject project) {
        return dm;
    }


}
