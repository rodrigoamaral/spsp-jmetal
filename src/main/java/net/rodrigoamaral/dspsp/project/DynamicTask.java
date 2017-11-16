package net.rodrigoamaral.dspsp.project;


import net.rodrigoamaral.dspsp.project.events.IEventSubject;

public class DynamicTask extends net.rodrigoamaral.spsp.project.Task implements IEventSubject {
    private double finishedEffort;
    private int originalIndex;

    public DynamicTask(int id, double effort, int originalIndex) {
        super(id, effort);
        this.originalIndex = originalIndex;
    }

    public double getFinishedEffort() {
        return finishedEffort;
    }

    public void setFinishedEffort(double finishedEffort) {
        this.finishedEffort = finishedEffort;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }
}
