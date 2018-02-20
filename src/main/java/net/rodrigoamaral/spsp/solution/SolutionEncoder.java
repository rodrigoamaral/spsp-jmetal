package net.rodrigoamaral.spsp.solution;

public class SolutionEncoder {
    private int employees;
    private int tasks;
    private double[] dedicationMatrix;

    public SolutionEncoder(int employees, int tasks) {
        this.employees = employees;
        this.tasks = tasks;
        this.dedicationMatrix = new double[size()];
    }

    public int size() {
        return employees * tasks;
    }

    public int encode(int employee, int task) {
        return (employee * this.tasks) + task;
    }

    //    private void populateFakeSolution() {
//        for (int i = 0; i < dedicationMatrix.length; i++) {
//            dedicationMatrix[i] = Math.random();
//        }
//    }

}
