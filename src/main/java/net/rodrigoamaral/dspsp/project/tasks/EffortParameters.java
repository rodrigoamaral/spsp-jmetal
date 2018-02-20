package net.rodrigoamaral.dspsp.project.tasks;

public class EffortParameters {

    public final double totalDedication ;
    public final double costDriveValue ;
    public final double timeSpent;

    public EffortParameters(int taskIndex, double totalDedication, double totalFitness, double costDriveValue, double timeSpent) {
        this.totalDedication = totalDedication;
        this.costDriveValue = costDriveValue;
        this.timeSpent = timeSpent;
    }

    public double finishedEffort(double effortDuration) {
        return effortDuration * (this.totalDedication / this.costDriveValue);
    }
}
