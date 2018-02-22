package net.rodrigoamaral.dspsp.decision;

import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

import static net.rodrigoamaral.util.DoubleUtils.max;
import static net.rodrigoamaral.util.DoubleUtils.min;

/**
 *  Implements the decision making method.
 *
 *  The solution with the maximum utility value is chosen
 *  as the final schedule.
 *
 */
public class DecisionMaker {

    private List<DoubleSolution> schedules;
    final private ComparisonMatrix cm;
    final static private double ALPHA = 0.001;

    public DecisionMaker(List<DoubleSolution> schedules_, ComparisonMatrix cm) {
        if (schedules_ == null || schedules_.size() < 1) {
           throw new IllegalArgumentException("Schedule list cannot be null or empty");
        }
        this.schedules = schedules_;
        this.cm = cm;
    }

    private DoubleSolution choose(double[] weights) {
        double[][] normalizedSolutions = new double[weights.length][this.schedules.size()];
        for (int i = 0; i < weights.length; i++) {
            double minObjectiveValue = min(getObjectiveValues(i));
            double maxObjectiveValue = max(getObjectiveValues(i));
            int j = 0;
            for (DoubleSolution s: this.schedules) {
                normalizedSolutions[i][j] = (maxObjectiveValue - s.getObjective(i) + ALPHA) / (maxObjectiveValue - minObjectiveValue + ALPHA);
                j++;
            }
        }

        double weightSum = weightSum(weights);

        DoubleSolution bestSchedule = null;
        double bestUtilityValue = 0;
        for (int sol = 0; sol < this.schedules.size(); sol++) {
            DoubleSolution schedule = this.schedules.get(sol);
            double utilityValue = 1;
            for (int obj = 0; obj < weights.length; obj++) {
                utilityValue *= Math.pow(normalizedSolutions[obj][sol], weights[obj] / weightSum);
            }
            if (utilityValue > bestUtilityValue) {
                bestUtilityValue = utilityValue;
                bestSchedule = schedule;
            }
        }

        return bestSchedule;
    }

    public DoubleSolution chooseInitialSchedule() {
        return choose(cm.initialWeights());
    }

    public DoubleSolution chooseNewSchedule() {
        return choose(cm.reschedulingWeights());
    }



    private List<Double> getObjectiveValues(int objectiveIndex) {
        List<Double> values = new ArrayList<>();
        for (DoubleSolution s: this.schedules) {
            values.add(s.getObjective(objectiveIndex));
        }
        return values;
    }

    private double weightSum(double[] weights) {
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
        }
        return sum;
    }
}
