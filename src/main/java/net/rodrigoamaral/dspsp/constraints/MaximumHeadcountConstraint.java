package net.rodrigoamaral.dspsp.constraints;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

import java.util.*;

public class MaximumHeadcountConstraint implements IConstraint {

    @Override
    public DedicationMatrix repair(DedicationMatrix dm, DynamicProject project) {
        for (DynamicTask task: project.getAvailableTasks()) {
            List<DynamicEmployee> team = getTeamSortedByProficiencyInTask(dm, project, task);

            while (team.size() > task.getMaximumHeadcount()) {
                int employeeIndex = team.get(0).index();
                team.remove(0);
                if (project.missingSkills(task, team) == 0) {
                    dm.setDedication(employeeIndex, task.index(), 0);
                }
            }
        }
        return dm;
    }

    private List<DynamicEmployee> getTeamSortedByProficiencyInTask(DedicationMatrix dm, final DynamicProject project, final DynamicTask task) {
        List<DynamicEmployee> team = new ArrayList<>(project.taskTeam(task, dm));

        Collections.sort(team, new Comparator<DynamicEmployee>() {
            @Override
            public int compare(DynamicEmployee e1, DynamicEmployee e2) {
                double p1 = project.getTaskProficiency().get(e1.index()).get(task.index());
                double p2 = project.getTaskProficiency().get(e2.index()).get(task.index());
                if (p1 == p2) {
                    return 0;
                }
                return p1 < p2 ? -1 : 1;
            }
        });
        return team;
    }


}
