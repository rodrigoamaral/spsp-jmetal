package net.rodrigoamaral.spsp.adapters;

import net.rodrigoamaral.spsp.config.ProjectConfigLoader;
import net.rodrigoamaral.spsp.constraints.*;
import net.rodrigoamaral.spsp.objectives.CostObjective;
import net.rodrigoamaral.spsp.objectives.DurationObjective;
import net.rodrigoamaral.spsp.objectives.IObjectiveEvaluator;
import net.rodrigoamaral.spsp.objectives.SPSPObjectiveEvaluator;
import net.rodrigoamaral.spsp.project.Project;
import org.uma.jmetal.solution.DoubleSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Adapts Project interface to a JMetal AbstractDoubleProblem
 * that implements ConstrainedProble&lt;DoubleSolution&gt;. {@link JMetalSPSPAdapter}
 * must provide all the methods needed for these interfaces.
 *
 * @author Rodrigo Amaral
 *
 */
public class JMetalSPSPAdapter {

    private static final String problemName = "SPSP";
    private Project project;
    private static final double LOWER_LIMIT = 0.0;
    private static final double UPPER_LIMIT = 1.0;
    private IObjectiveEvaluator objectiveEvaluator;
    private IConstraintEvaluator constraintEvaluator;

    private SolutionConverter converter;

    /**
     * Creates a {@link Project} instance and all objectives and constraints
     * needed for SPSP.
     *
     * @param configFile Relative path to the configuration file
     * @throws FileNotFoundException
     */
    public JMetalSPSPAdapter(String configFile) throws FileNotFoundException {
        this.project = new ProjectConfigLoader(configFile).createProject();
        this.objectiveEvaluator = new SPSPObjectiveEvaluator()
                .addObjective(new CostObjective())
                .addObjective(new DurationObjective());
        this.constraintEvaluator = new SPSPConstraintEvaluator()
                .addConstraint(new AllTasksAllocatedConstraint())
                .addConstraint(new EmployeesHaveAllRequiredSkillsConstraint())
                .addConstraint(new NoEmployeeOverworkConstraint());
        this.converter = new SolutionConverter(this.project);
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
        return populateLimitList(UPPER_LIMIT);
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
