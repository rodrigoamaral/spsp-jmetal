package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.List;

public class NoEmployeeOverworkConstraint implements IConstraint {

    public DedicationMatrix repair(DedicationMatrix dm, DynamicProject project) {
        DedicationMatrix repaired = dm;
        List<DynamicEmployee> availableEmployees = project.getAvailableEmployees();
        List<DynamicTask> activeTasks = project.getActiveTasks();
        for (DynamicEmployee e: availableEmployees) {
            double employeeDedication = 0.0;
            for (DynamicTask t: activeTasks) {
                employeeDedication += dm.getDedication(e.index(), t.index());
            }
            if (employeeDedication > e.getMaxDedication()) {
                for (DynamicTask t: activeTasks) {
                    double normalizedDedication = dm.getDedication(e.index(), t.index()) / Math.max(1, employeeDedication/e.getMaxDedication());
                    repaired.setDedication(e.index(), t.index(), normalizedDedication);
                }
            }
        }
        return repaired;
    }

}
