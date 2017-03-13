package net.rodrigoamaral.spsp.solution;

/**
 * Created by rodrigo on 08/10/16.
 */
public class SolutionEncoder {
    private int employees;
    private int tasks;
    private double[] dedicationMatrix;

    public SolutionEncoder(int employees, int tasks) {
        this.employees = employees;
        this.tasks = tasks;
        this.dedicationMatrix = new double[size()];
//        populateFakeSolution();
    }

    public int getEmployeeNumber() {
        return employees;
    }

    public int getTaskNumber() {
        return tasks;
    }

    public int size() {
        return employees * tasks;
    }

    public double[] getDedicationMatrix() {
        return dedicationMatrix;
    }

    public int decodeTask(int index) {
        return index % this.tasks;
    }

    public int decodeEmployee(int index) {
        return Math.floorDiv(index, this.tasks);
    }

    public int encode(int employee, int task) {
        return (employee * this.tasks) + task;
    }

    public double getVariableValue(int employee, int task) {
        return dedicationMatrix[encode(employee, task)];
    }

    public void setVariableValue(int employee, int task, double value) {
        dedicationMatrix[encode(employee, task)] = value;
    }

//    private void populateFakeSolution() {
//        for (int i = 0; i < dedicationMatrix.length; i++) {
//            dedicationMatrix[i] = Math.random();
//        }
//    }

}
