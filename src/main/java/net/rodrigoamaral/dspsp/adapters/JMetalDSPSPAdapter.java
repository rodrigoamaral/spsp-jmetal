package net.rodrigoamaral.dspsp.adapters;

import net.rodrigoamaral.dspsp.config.DynamicProjectConfigLoader;
import net.rodrigoamaral.dspsp.objectives.*;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.constraints.*;
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
                .addObjective(new CostObjective())
                .addObjective(new DurationObjective())
                .addObjective(new RobustnessObjective())
                .addObjective(new StabilityObjective());
        this.constraintEvaluator = new DSPSPConstraintEvaluator()
                .addConstraint(new AllTasksAllocatedConstraint())
                .addConstraint(new EmployeesHaveAllRequiredSkillsConstraint())
//                .addConstraint(new NoEmployeeOverworkConstraint())
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

    public double evaluateObjective(int i, DoubleSolution solution) {
        return objectiveEvaluator.evaluate(i, project, converter.convert(solution));
    }

    public Double getConstraintViolationDegree(DoubleSolution solution) {
        return constraintEvaluator.overallConstraintViolationDegree(project, converter.convert(solution));
    }

    public Integer getNumberOfViolatedConstraints(DoubleSolution solution) {
        return constraintEvaluator.numberOfViolatedConstraints(project, converter.convert(solution));
    }

    public int getNumberOfConstraints() {
        return constraintEvaluator.size();
    }
}
