package net.rodrigoamaral.dspsp.solution.repair;

import net.rodrigoamaral.dspsp.adapters.SolutionConverter;
import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.solution.DoubleSolution;

public class EmployeeReturnStrategy extends ScheduleRepairStrategy {

    private final DynamicEmployee employee;

    public EmployeeReturnStrategy(DoubleSolution _solution, DynamicProject _project, DynamicEmployee employee) {
        super(_solution, _project);
        this.employee = employee;
    }

    @Override
    public DoubleSolution repair() {
        for (DynamicTask task: project.getAvailableTasks()) {
            if (TaskManager.teamSize(task, schedule) < task.getMaximumHeadcount()) {
                for (Integer sk: employee.getSkills()) {
                    if (task.getSkills().contains(sk)) {
                        int i = SolutionConverter.encode(employee.index(), task.index());
                        double newDed = 0.1;
                        if (repairedSolution.getVariableValue(i) < DedicationMatrix.MIN_DED_THRESHOLD) {
                            SPSPLogger.debug("Repairing employee return (e = " + employee.index() + ", t = " + task.index() + ") " + repairedSolution.getVariableValue(i) + " -> " + newDed);
                            repairedSolution.setVariableValue(i, newDed);
                        }
                    }
                }
            }
        }
        normalize();
        return repairedSolution;
    }
}
