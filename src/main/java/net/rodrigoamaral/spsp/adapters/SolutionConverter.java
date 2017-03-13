package net.rodrigoamaral.spsp.adapters;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Created by rodrigo on 06/03/17.
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


    public DedicationMatrix convert(DoubleSolution solution) {
        DedicationMatrix dm = new DedicationMatrix(employees, tasks);
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            dm.setDedication(decodeEmployee(i),
                             decodeTask(i),
                             solution.getVariableValue(i));
        }
        return dm;
    }

    public static int decodeTask(int index) {
        return index % tasks;
    }

    public static int decodeEmployee(int index) {
        return Math.floorDiv(index, tasks);
    }

    public static int encode(int employee, int task) {
        return (employee * tasks) + task;
    }
}
