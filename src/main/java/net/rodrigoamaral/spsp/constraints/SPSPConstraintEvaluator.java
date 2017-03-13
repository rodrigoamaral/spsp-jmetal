package net.rodrigoamaral.spsp.constraints;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodrigo on 02/03/17.
 */
public class SPSPConstraintEvaluator implements IConstraintEvaluator {
    private List<IConstraint> constraints;

    public SPSPConstraintEvaluator() {
        constraints = new ArrayList<>();
    }

    @Override
    public IConstraintEvaluator addConstraint(IConstraint constraint) {
        constraints.add(constraint);
        return this;
    }

    @Override
    public int size() {
        return constraints.size();
    }

    @Override
    public double overallConstraintViolationDegree(Project project, DedicationMatrix s) {
        double violationDegree = 0.0;
        for (IConstraint c: constraints) {
            violationDegree = violationDegree + c.violationDegree(project, s);
        }
        return violationDegree;
    }

    @Override
    public int numberOfViolatedConstraints(Project project, DedicationMatrix s) {
        int violatedConstraints = 0;
        for (IConstraint c: constraints) {
            if (c.isViolated(project, s)) {
                violatedConstraints++;
            }
        }
        return violatedConstraints;
    }

}
