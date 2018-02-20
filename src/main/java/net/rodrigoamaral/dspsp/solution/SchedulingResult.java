package net.rodrigoamaral.dspsp.solution;

import org.uma.jmetal.solution.DoubleSolution;

import java.util.List;

/**
 * Represents the result of a scheduling procedure.
 *
 * Scheduling must return:
 *      - a list of candidate solutions
 *      - time spent on processing,for logging and profiling purposes
 *
 */
public class SchedulingResult {
    private final List<DoubleSolution> schedules;
    private final long computingTime;

    private final boolean projectFinished;

    public SchedulingResult(List<DoubleSolution> result, long computingTime, boolean projectFinished) {
        this.schedules = result;
        this.computingTime = computingTime;
        this.projectFinished = projectFinished;
    }

    public List<DoubleSolution> getSchedules() {
        return schedules;
    }

    public long getComputingTime() {
        return computingTime;
    }

}
