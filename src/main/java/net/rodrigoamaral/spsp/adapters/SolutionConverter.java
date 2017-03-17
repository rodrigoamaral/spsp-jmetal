package net.rodrigoamaral.spsp.adapters;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Converts a jMetal {@link DoubleSolution} into a {@link DedicationMatrix}.
 * It also provides public methods to encode and decode vector indices
 * of a DoubleSolution into matrix indices of a DedicationMatrix.
 *
 */
public class SolutionConverter {

    private static int employees;
    private static int tasks;

    public SolutionConverter(Project _project) {
        employees = _project.getEmployees().size();
        tasks = _project.getTasks().size();
    }

    public SolutionConverter(int _employees, int _tasks) {
        employees = _employees;
        tasks = _tasks;
    }


    /**
     * Converts a {@link DoubleSolution} into a {@link DedicationMatrix}.
     *
     * @param solution DoubleSolution object
     * @return new instance of DedicationMatrix
     */
    public DedicationMatrix convert(DoubleSolution solution) {
        DedicationMatrix dm = new DedicationMatrix(employees, tasks);
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            dm.setDedication(decodeEmployee(i),
                             decodeTask(i),
                             solution.getVariableValue(i));
        }
        return dm;
    }

    /**
     * Decode a DoubleSolution vector index into a task number/id/column index.
     *
     * @param index vector index
     * @return task number (matrix column index)
     */
    public static int decodeTask(int index) {
        return index % tasks;
    }

    /**
     * Decode a DoubleSolution vector index into a employee number/id/row index.
     *
     * @param index vector index
     * @return employee number (matrix row index)
     */
    public static int decodeEmployee(int index) {
        return Math.floorDiv(index, tasks);
    }

    /**
     * Encode a employee-task tuple into a DoubleSolution vector index.
     *
     * @param employee employee number (matrix row index)
     * @param task task number (matrix column index)
     * @return vector index
     */
    public static int encode(int employee, int task) {
        return (employee * tasks) + task;
    }
}
