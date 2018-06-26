package net.rodrigoamaral.dspsp.project.tasks;


import net.rodrigoamaral.dspsp.project.events.IEventSubject;

import java.util.ArrayList;

public class DynamicTask extends net.rodrigoamaral.spsp.project.Task implements IEventSubject {
    private double meanEstimatedEffort;
    private double effortDeviation;
    private double finishedEffort;
    private int originalIndex;
    private int maximumHeadcount;
    private static final double FINISH_THRESHOLD = 10E-10;
    private boolean available = false;

    public DynamicTask(int id,
                       double initialEstimatedEffort,
                       double meanEstimatedEffort,
                       double effortDeviation,
                       int originalIndex,
                       int maximumHeadcount) {

        super(id, initialEstimatedEffort);
        this.meanEstimatedEffort = meanEstimatedEffort;
        this.effortDeviation = effortDeviation;
        this.originalIndex = originalIndex;
        this.finishedEffort = 0;
        this.maximumHeadcount = maximumHeadcount;
    }

     public DynamicTask(DynamicTask task) {
         super(task.getId(), task.getEffort());
         this.setMeanEstimatedEffort(task.getMeanEstimatedEffort());
         this.setEffortDeviation(task.getEffortDeviation());
         this.setDuration(task.getDuration());
         this.setStart(task.getStart());
         this.setFinish(task.getFinish());
         this.setFinishedEffort(task.getFinishedEffort());
         this.setSkills(new ArrayList<>(task.getSkills()));
         this.originalIndex = task.index();
         this.maximumHeadcount = task.getMaximumHeadcount();
         this.available = task.isAvailable();
     }

    public double getMeanEstimatedEffort() {
        return meanEstimatedEffort;
    }

    public void setMeanEstimatedEffort(double meanEstimatedEffort) {
        this.meanEstimatedEffort = meanEstimatedEffort;
    }

    public double getEffortDeviation() {
        return effortDeviation;
    }

    public void setEffortDeviation(double effortDeviation) {
        this.effortDeviation = effortDeviation;
    }

    public double getFinishedEffort() {
        return finishedEffort;
    }

    public void setFinishedEffort(double finishedEffort) {
        this.finishedEffort = finishedEffort;
    }

    public int index() {
        return originalIndex;
    }

    public void addFinishedEffort(double effort) {
        if (effort <= 0) {
            throw new IllegalArgumentException("Task " + this.index() + ": Finished effort value must be greater than zero.");
        }
        this.finishedEffort += effort;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isFinished() {
        return getRemainingEffort() < FINISH_THRESHOLD;
    }

    public double getRemainingEffort() {
        return getEffort() - getFinishedEffort();
    }

    public double finishedEffortRatio() {
        return getFinishedEffort() / getEffort();
    }

    public int getMaximumHeadcount() {
        return maximumHeadcount;
    }

    public void setEffort(double effort) {
        this.effort = effort;
    }

    @Override
    public String toString() {
        return "T_" + index();
//        return "DynamicTask{" +
//                "id=" + getId() +
//                ", index=" + index() +
//                ", available=" + isAvailable() +
////                ", effort=" + getEffort() +
////                ", duration=" + getDuration() +
////                ", start=" + getStart() +
////                ", finish=" + getFinish() +
////                ", skills=" + getSkills() +
//                '}';
    }
}
