package net.rodrigoamaral.dspsp.project.tasks;

public class EffortParameters {

    public final int taskIndex;
    public final double totalDedication ;
    public final double totalFitness;
    public final double costDriveValue ;
    public final double timeSpent;

    public EffortParameters(int taskIndex, double totalDedication, double totalFitness, double costDriveValue, double timeSpent) {
        this.taskIndex = taskIndex;
        this.totalDedication = totalDedication;
        this.totalFitness = totalFitness;
        this.costDriveValue = costDriveValue;
        this.timeSpent = timeSpent;
    }

    public double finishedEffort(double effortDuration) {
        return effortDuration * (this.totalDedication / this.costDriveValue);
    }
}
