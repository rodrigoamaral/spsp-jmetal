package net.rodrigoamaral.dspsp.adapters;

import net.rodrigoamaral.dspsp.config.DynamicProjectConfigLoader;
import net.rodrigoamaral.dspsp.objectives.*;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.constraints.*;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
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

    private static final String problemName = "DSPSP";
    private DynamicProject project;
    private static final double LOWER_LIMIT = 0.0;
    private static final double UPPER_LIMIT = 1.0;
    private static final double MAX_OVERWORK = 0.2;
    private IObjectiveEvaluator objectiveEvaluator;
    private IConstraintEvaluator constraintEvaluator;

    private SolutionConverter converter;

    /**
     * Creates a {@link DynamicProject} instance and all objectives and constraints
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
        init();
    }

    private void init() {
        this.objectiveEvaluator = new DSPSPObjectiveEvaluator()
                .addObjective(new DurationObjective())
                .addObjective(new CostObjective())
                .addObjective(new RobustnessObjective())
                .addObjective(new StabilityObjective());
        this.constraintEvaluator = new DSPSPConstraintEvaluator()
                .addConstraint(new NoEmployeeOverworkConstraint())
                .addConstraint(new AllTasksAllocatedConstraint())
                .addConstraint(new MaximumHeadcountConstraint())
                .addConstraint(new TaskSkillsConstraint())
                ;
        this.converter = new SolutionConverter(this.project);
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
        return objectiveEvaluator.size();
    }

    public List<IObjective> getObjectives() {
        return objectiveEvaluator.getObjectives();
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

//    public double evaluateObjective(int i, DoubleSolution solution) {
//        DedicationMatrix dm = repair(solution);
//        double evaluation = objectiveEvaluator.evaluate(i, project, dm);
//        evaluation = penalizeObjective(evaluation, dm);
//        return evaluation;
//    }

    /**
     * Evaluates all objectives registered by the objectiveEvaluator in the
     * constructor. Before evaluation, it repairs the solution according to
     * the constraints registered by the constraintEvaluator in the
     * constructor. Objectives are penalized if there is any skill missing
     * in the available emplyee team.
     *
     * @param solution
     * @return repaired solution
     */
    public DoubleSolution evaluateObjectives(DoubleSolution solution) {
        DedicationMatrix dm = repair(solution);
        int missingSkills = missingSkills();
        if (missingSkills > 0) {
            for (int i = 0; i < getNumberOfObjectives(); i++) {
                solution.setObjective(i, penalizeObjective(i, dm, missingSkills));
            }
        } else {
            for (int i = 0; i < getNumberOfObjectives(); i++) {
                double evaluation = objectiveEvaluator.evaluate(i, project, dm);
                solution.setObjective(i, evaluation);
            }
        }
        return solution;
    }

    private DedicationMatrix repair(DoubleSolution solution) {
        return constraintEvaluator.repair(converter.convert(solution), project);
    }

//    private double penalizeObjective(double evaluation, DedicationMatrix dm) {
////        return objectiveEvaluator.penalize(evaluation, dm, project);
//        return 0;
//    }

    public Double getConstraintViolationDegree(DoubleSolution solution) {
        return constraintEvaluator.overallConstraintViolationDegree(project, converter.convert(solution));
    }

    public Integer getNumberOfViolatedConstraints(DoubleSolution solution) {
        return constraintEvaluator.numberOfViolatedConstraints(project, converter.convert(solution));
    }

    public int getNumberOfConstraints() {
        return constraintEvaluator.size();
    }

    public int missingSkills() {
        return project.missingSkills();
    }

    public double penalizeObjective(int objectiveIndex, DoubleSolution solution, int missingSkills) {
        return getObjectives().get(objectiveIndex).penalize(project, converter.convert(solution), missingSkills);
    }

    public double penalizeObjective(int objectiveIndex, DedicationMatrix dm, int missingSkills) {
        return getObjectives().get(objectiveIndex).penalize(project, dm, missingSkills);
    }
}
