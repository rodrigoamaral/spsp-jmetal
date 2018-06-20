package net.rodrigoamaral.dspsp.adapters;

import net.rodrigoamaral.dspsp.config.DynamicProjectConfigLoader;
import net.rodrigoamaral.dspsp.constraints.*;
import net.rodrigoamaral.dspsp.exceptions.InvalidSolutionException;
import net.rodrigoamaral.dspsp.objectives.Efficiency;
import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.solution.DoubleSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Adapts Project interface to a JMetal AbstractDoubleProblem
 * that implements ConstrainedProble&lt;DoubleSolution&gt;. {@link JMetalDSPSPAdapter}
 * must provide all the methods needed for these interfaces.
 *
 * @author Rodrigo Amaral
 *
 */
public class JMetalDSPSPAdapter {

    public static final int DURATION = 0;
    public static final int COST = 1;
    public static final int ROBUSTNESS = 2;
    public static final int STABILITY = 3;

    public static final int[] STATIC_OBJECTIVES = {DURATION, COST, ROBUSTNESS};
    public static final int[] DYNAMIC_OBJECTIVES = {DURATION, COST, ROBUSTNESS, STABILITY};

    private static final String problemName = "DSPSP";
    private DynamicProject project;
    private static final double LOWER_LIMIT = 0.0;
    private static final double UPPER_LIMIT = 1.0;
    private static final double MAX_OVERWORK = 0.2;

    private int[] objectives;
    private IConstraintEvaluator constraintEvaluator;

    private SolutionConverter converter;

    /**
     * Creates a {@link DynamicProject} instance and evaluate all objectives and constraints
     * needed for SPSP.
     *
     * @param configFile Relative path to the configuration file
     * @throws FileNotFoundException
     */
    public JMetalDSPSPAdapter(String configFile) throws FileNotFoundException {
        this.project = new DynamicProjectConfigLoader(configFile).createProject();
        init();
    }

    public JMetalDSPSPAdapter(DynamicProject project) {
        this.project = project;
        initDynamic();
    }

    private void init() {
        this.objectives = STATIC_OBJECTIVES;
        this.constraintEvaluator = new DSPSPConstraintEvaluator()
                .addConstraint(new NoEmployeeOverworkConstraint())
                .addConstraint(new AllTasksAllocatedConstraint())
                .addConstraint(new MaximumHeadcountConstraint())
                .addConstraint(new TaskSkillsConstraint())
                ;
        this.converter = new SolutionConverter(this.project);
    }

    private void initDynamic() {
        init();
        this.objectives = DYNAMIC_OBJECTIVES;
    }

    public DynamicProject getProject() {
        return project;
    }

    public String getProblemName() {
        return problemName;
    }

    public int getNumberOfVariables() {
        return project.size();
    }

    public int getNumberOfObjectives() {
        return objectives.length;
    }

    private List<Double> populateLimitList(double value) {
        List<Double> limit = new ArrayList<>(getNumberOfVariables());
        for (int i = 0; i < getNumberOfVariables(); i++) {
            limit.add(value);
        }
        return limit;
    }

    public List<Double> getLowerLimit() {
        return populateLimitList(LOWER_LIMIT);
    }

    public List<Double> getUpperLimit() {
        return populateLimitList(UPPER_LIMIT + MAX_OVERWORK);
    }

    /**
     * Evaluates all objectives registered by the objectiveEvaluator in the
     * constructor. Before evaluation, it repairs the repairedSolution according to
     * the constraints registered by the constraintEvaluator in the
     * constructor. Objectives are penalized if there is any skill missing
     * in the available emplyee team.
     *
     * @param solution
     * @return repaired repairedSolution
     */
    public DoubleSolution evaluateObjectives(DoubleSolution solution) {

        DedicationMatrix dm = repair(solution);
        int missingSkills = missingSkills();

        if (missingSkills > 0) {

            solution.setObjective(DURATION, project.penalizeDuration(missingSkills));
            solution.setObjective(COST, project.penalizeCost(missingSkills));
            solution.setObjective(ROBUSTNESS, project.penalizeRobustness(missingSkills));

            if (mustIncludeStability(solution)) {
                solution.setObjective(STABILITY, project.penalizeStability(missingSkills));
            }

        } else {

            try {
                Efficiency efficiency = project.evaluateEfficiency(dm);
                double robustness = project.calculateRobustness(dm, efficiency);

                solution.setObjective(DURATION, efficiency.duration);
                solution.setObjective(COST, efficiency.cost);
                solution.setObjective(ROBUSTNESS, robustness);

                if (mustIncludeStability(solution)) {
                    double stability = project.calculateStability(dm);
                    solution.setObjective(STABILITY, stability);
                }
            } catch (InvalidSolutionException e) {

                SPSPLogger.trace("Penalizing invalid repairedSolution: " + dm);

                solution.setObjective(DURATION, project.penalizeDuration(1));
                solution.setObjective(COST, project.penalizeCost(1));
                solution.setObjective(ROBUSTNESS, project.penalizeRobustness(1));

                if (mustIncludeStability(solution)) {
                    solution.setObjective(STABILITY, project.penalizeStability(1));
                }
            }
        }

        return solution;
    }

    private boolean mustIncludeStability(DoubleSolution solution) {
        return project.getPreviousSchedule() != null && solution.getNumberOfObjectives() > 3;
    }

    private DedicationMatrix repair(DoubleSolution solution) {
//        repairedSolution = enableOnlyAvailableEmployees(repairedSolution);
//        repairedSolution = enableOnlyAvailableTasks(repairedSolution);
        return constraintEvaluator.repair(converter.convert(solution), project);
    }

//    private DoubleSolution enableOnlyAvailableTasks(DoubleSolution repairedSolution) {
//        for (DynamicTask task : getProject().getTasks()) {
//            if (!task.isAvailable()) {
//                for (DynamicEmployee employee : getProject().getEmployees()) {
//                    repairedSolution.setVariableValue(
//                            SolutionConverter.encode(employee.index(), task.index()),
//                            0.0
//                    );
//                }
//            }
//        }
//        return repairedSolution;
//    }
//
//    private DoubleSolution enableOnlyAvailableEmployees(DoubleSolution repairedSolution) {
//        for (DynamicEmployee employee : getProject().getEmployees()) {
//            if (!employee.isAvailable()) {
//                for (DynamicTask task : getProject().getAvailableTasks()) {
//                    repairedSolution.setVariableValue(
//                            SolutionConverter.encode(employee.index(), task.index()),
//                            0.0
//                    );
//                }
//            }
//        }
//        return repairedSolution;
//    }

    public int getNumberOfConstraints() {
        return constraintEvaluator.size();
    }

    public int missingSkills() {
        return project.missingSkills();
    }

}
