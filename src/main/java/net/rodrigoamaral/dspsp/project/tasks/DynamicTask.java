package net.rodrigoamaral.dspsp.project.tasks;


import net.rodrigoamaral.dspsp.project.events.IEventSubject;

public class DynamicTask extends net.rodrigoamaral.spsp.project.Task implements IEventSubject {
    private double finishedEffort;
    private int originalIndex;
    private int maximumHeadcount;
    private static final double FINISH_THRESHOLD = 10E-10;

    private boolean available = false;

    public DynamicTask(int id, double effort, int originalIndex, int maximumHeadcount) {
        super(id, effort);
        this.originalIndex = originalIndex;
        this.finishedEffort = 0;
        this.maximumHeadcount = maximumHeadcount;
    }

     public DynamicTask(DynamicTask task) {
         super(task.getId(), task.getEffort());
         this.setDuration(task.getDuration());
         this.setStart(task.getStart());
         this.setFinish(task.getFinish());
         this.setFinishedEffort(task.getFinishedEffort());
         this.originalIndex = task.index();
         this.maximumHeadcount = task.getMaximumHeadcount();
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

    public int getMaximumHeadcount() {
        return maximumHeadcount;
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
