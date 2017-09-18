package net.rodrigoamaral.dspsp.scenarios;

import net.rodrigoamaral.dspsp.project.DynamicTask;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.List;

public class TaskScenario {

    private double[] taskEfforts;
    private double[] remainingTaskEfforts;

    public TaskScenario(List<DynamicTask> tasks) {
        int j = tasks.size();
        taskEfforts = new double[j];
        for (int i = 0; i < j; i++) {
            taskEfforts[i] = tasks.get(i).getEffort();
        }
        remainingTaskEfforts = new double[j];
        for (int i = 0; i < j; i++) {
            remainingTaskEfforts[i] = generateRemainingEffort(tasks.get(i));
        }
    }

    public int size() {
        return remainingTaskEfforts.length;
    }

    private double generateRemainingEffort(DynamicTask task) {
        double mean = task.getEffort();
        double sd = mean * 0.5;
        NormalDistribution nd = new NormalDistribution(mean, sd);
        double totalEffort = nd.sample();
        // TODO: Replace this with the actual finishedEffort from task
        double finishedEffort = task.getEffort() * 0.1;
        while (!validEffortValue(totalEffort, finishedEffort)) {
            totalEffort = nd.sample();
        }
        return totalEffort - finishedEffort;
    }

    private boolean validEffortValue(double totalEffort, double finishedEffort) {
        return totalEffort > finishedEffort;
    }

    public double[] getRemainingTaskEfforts() {
        return remainingTaskEfforts;
    }
}
