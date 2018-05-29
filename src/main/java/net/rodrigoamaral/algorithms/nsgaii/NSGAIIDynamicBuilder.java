package net.rodrigoamaral.algorithms.nsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.List;

public class NSGAIIDynamicBuilder extends NSGAIIBuilder{

    private List<DoubleSolution> initialPopulation;

    public NSGAIIDynamicBuilder(Problem problem, CrossoverOperator crossoverOperator, MutationOperator mutationOperator) {
        super(problem, crossoverOperator, mutationOperator);
    }

    public NSGAIIDynamicBuilder setInitialPopulation(List<DoubleSolution> initialPopulation) {
        this.initialPopulation = initialPopulation;
        return this;
    }

    @Override
    public NSGAII build() {
        return new NSGAIIDynamic(getProblem(), getMaxIterations(), getPopulationSize(), getCrossoverOperator(),
                getMutationOperator(), getSelectionOperator(), getSolutionListEvaluator(), initialPopulation);
    }
}
