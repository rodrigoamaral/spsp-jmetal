package net.rodrigoamaral.dspsp.scenarios;

import net.rodrigoamaral.dspsp.project.DynamicTask;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.List;


public class TaskScenarioFactory {
    public static double generateEffort() {
        NormalDistribution nd = new NormalDistribution();
//        Qual o domínio dos valores de esforço?
        return nd.sample();
    }

    public static void main(String[] args) {
        double effort = TaskScenarioFactory.generateEffort();
        System.out.println("Sample effort: " + effort);
    }

    public TaskScenario create(List<DynamicTask> availableTasks) {
        return new TaskScenario(availableTasks);
    }
}
