package net.rodrigoamaral.dspsp.project;


import net.rodrigoamaral.dspsp.project.events.IEventSubject;

public class DynamicTask extends net.rodrigoamaral.spsp.project.Task implements IEventSubject {
    private double finishedEffort;
    private int originalIndex;

    public DynamicTask(int id, double effort, int originalIndex) {
        super(id, effort);
        this.originalIndex = originalIndex;
        this.finishedEffort = 0;
    }

     public DynamicTask(DynamicTask task) {
         super(task.getId(), task.getEffort());
         this.setDuration(task.getDuration());
         this.setStart(task.getStart());
         this.setFinish(task.getFinish());
         this.setFinishedEffort(task.getFinishedEffort());
         this.originalIndex = task.getOriginalIndex();
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

    public void increaseFinishedEffort(double effort) {
        this.finishedEffort += effort;
    }

    public boolean isAvailable() {
        return getFinishedEffort() < getEffort();
    }
}
