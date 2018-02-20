package net.rodrigoamaral.dspsp.solution;

public class SolutionEncoder {
    private int employees;
    private int tasks;
    private double[] dedicationMatrix;

    public int size() {
        return employees * tasks;
    }

    public int encode(int employee, int task) {
        return (employee * this.tasks) + task;
    }

}
