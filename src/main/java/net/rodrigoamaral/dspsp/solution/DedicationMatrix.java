package net.rodrigoamaral.dspsp.solution;

/**
 *
 * Represents a E x T dedication matrix where each matrix[i][j]
 * element is the dedication degree of employee i to the task j.
 *
 */
public class DedicationMatrix {

    final private double[][] matrix;
    final private int employees;
    final private int tasks;

    public DedicationMatrix(int _employees, int _tasks) {
        employees = _employees;
        tasks = _tasks;
        matrix = new double[employees][tasks];
        for (int i = 0; i < employees; i++) {
            for (int j = 0; j < tasks; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    public int getEmployees() {
        return employees;
    }

    public int getTasks() {
        return tasks;
    }

    public void setDedication(int employee, int task, double dedication) {
        matrix[employee][task] = dedication;
    }

    public double getDedication(int employee, int task) {
        return matrix[employee][task];
    }

}
