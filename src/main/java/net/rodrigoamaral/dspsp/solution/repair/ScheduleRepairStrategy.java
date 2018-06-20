package net.rodrigoamaral.dspsp.solution.repair;


import net.rodrigoamaral.dspsp.adapters.SolutionConverter;
import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.max;
import static net.rodrigoamaral.util.DoubleUtils.sum;

abstract public class ScheduleRepairStrategy implements IScheduleRepairStrategy {

    protected final DedicationMatrix schedule;
    protected final DynamicProject project;
    protected DoubleSolution repairedSolution;

    public ScheduleRepairStrategy(DoubleSolution _solution, DynamicProject _project) {
        this.repairedSolution = (DoubleSolution) _solution.copy();
        this.project = _project;
        this.schedule = new SolutionConverter(_project).convert(_solution);
    }

    public void normalize() {
        for (DynamicTask t : project.getActiveTasks()) {
            for (DynamicEmployee e : project.getAvailableEmployees()) {
                final int i = SolutionConverter.encode(e.index(), t.index());
                double n = repairedSolution.getVariableValue(i) / max(1, dedicationSum(repairedSolution, project.getActiveTasks(), e) / e.getMaxDedication());
                repairedSolution.setVariableValue(i, n);
            }
        }
    }

    private double dedicationSum(DoubleSolution repairedSolution, List<DynamicTask> activeTasks, DynamicEmployee e) {
        return sum(activeTasksDedication(repairedSolution, activeTasks, e));
    }

    private List<Double> activeTasksDedication(DoubleSolution repairedSolution, List<DynamicTask> activeTasks, DynamicEmployee e) {
        List<Double> result = new ArrayList<>();
        for (DynamicTask task : activeTasks) {
            result.add(repairedSolution.getVariableValue(SolutionConverter.encode(e.index(), task.index())));
        }
        return result;
    }
}
