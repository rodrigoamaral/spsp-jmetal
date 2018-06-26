package net.rodrigoamaral.algorithms.nsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.List;

public class NSGAIIDynamic extends NSGAII {

    private List<DoubleSolution> initialPopulation;

    public NSGAIIDynamic(Problem problem, int maxEvaluations, int populationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator, SolutionListEvaluator evaluator, List<DoubleSolution> initialPopulation) {
        super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        this.initialPopulation = initialPopulation;
    }

    @Override
    protected List<DoubleSolution> createInitialPopulation() {
        List<DoubleSolution> population = new ArrayList<>(getMaxPopulationSize());
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            DoubleSolution newIndividual;
            if (initialPopulation == null || initialPopulation.isEmpty()) {
                newIndividual = (DoubleSolution) getProblem().createSolution();
            } else {
                newIndividual = new DefaultDoubleSolution((DefaultDoubleSolution) initialPopulation.get(i));
            }
            population.add(newIndividual);
        }
        return population;
    }
}
