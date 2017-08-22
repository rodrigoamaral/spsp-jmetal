package net.rodrigoamaral.dspsp.scenarios;

import org.apache.commons.math3.distribution.NormalDistribution;


public class TaskScenarios {
    public static double generateEffort() {
        NormalDistribution nd = new NormalDistribution();
        return nd.sample();
    }

    public static void main(String[] args) {
        double effort = TaskScenarios.generateEffort();
        System.out.println("Sample effort: " + effort);
    }
}
