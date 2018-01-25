package net.rodrigoamaral.dspsp.project.tasks;

public class FinishedEffort {

    public final int taskIndex;
    public final double totalDedication ;
    public final double totalFitness;
    public final double costDriveValue ;
    public final double timeSpent;
    public final double effort;

    public FinishedEffort(int taskIndex, double totalDedication, double totalFitness, double costDriveValue, double timeSpent, double effort) {
        this.taskIndex = taskIndex;
        this.totalDedication = totalDedication;
        this.totalFitness = totalFitness;
        this.costDriveValue = costDriveValue;
        this.timeSpent = timeSpent;
        this.effort = effort;
    }
}
