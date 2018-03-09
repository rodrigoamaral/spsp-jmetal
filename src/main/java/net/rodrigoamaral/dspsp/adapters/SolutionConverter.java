package net.rodrigoamaral.dspsp.adapters;

import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
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

    public SolutionConverter(DynamicProject _project) {
        employees = _project.getEmployees().size();
        tasks = _project.getTasks().size();
    }

    /**
     * Converts a {@link DoubleSolution} into a {@link DedicationMatrix}.
     *
     * @param solution DoubleSolution object
     * @return new instance of DedicationMatrix
     */
    public DedicationMatrix convert(DoubleSolution solution) {
        return convert(solution, false);
    }

    /**
     * Converts a {@link DoubleSolution} into a {@link DedicationMatrix}.
     *
     * @param solution DoubleSolution object
     * @param repair Indicates if dedications too small are normalized to zero
     * @return new instance of DedicationMatrix
     */
    public DedicationMatrix convert(DoubleSolution solution, boolean repair) {
        DedicationMatrix dm = new DedicationMatrix(employees, tasks);
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            Double dedication = solution.getVariableValue(i);
            dedication = repairDedication(repair, dedication);
            dm.setDedication(decodeEmployee(i),
                    decodeTask(i),
                    dedication);
        }
        return dm;
    }

    private Double repairDedication(boolean repair, Double dedication) {
        if (repair) {
            dedication = dedication < 10E-2 ? 0.0 : dedication;
        }
        return dedication;
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

}
