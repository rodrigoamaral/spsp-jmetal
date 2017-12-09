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

    public SchedulingResult(List<DoubleSolution> result, long computingTime) {
        this.schedules = result;
        this.computingTime = computingTime;
    }

    public List<DoubleSolution> getSchedules() {
        return schedules;
    }

    public long getComputingTime() {
        return computingTime;
    }
}
