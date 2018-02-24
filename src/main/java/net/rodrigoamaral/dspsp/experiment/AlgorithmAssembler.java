package net.rodrigoamaral.dspsp.experiment;

import net.rodrigoamaral.dspsp.DSPSProblem;
import net.rodrigoamaral.dspsp.nsgaii.DSPSP_NSGAIIBuilder;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.util.List;

/**
 * Assembles an algorithm from given parameters for experiment purposes.
 *
 * @author Rodrigo Amaral
 */
public class AlgorithmAssembler {
    private Problem<DoubleSolution> problem;
    private Algorithm<List<DoubleSolution>> algorithm;
    private DynamicProject project;

    public AlgorithmAssembler(Problem<DoubleSolution> problem) {
        this.problem = problem;
    }

    public Algorithm<List<DoubleSolution>> getAlgorithm() {
        return algorithm;
    }

    public DynamicProject getProject() {
        return project;
    }

    public AlgorithmAssembler invoke() {
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        project = ((DSPSProblem) problem).getProject();

        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;

        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;

        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        algorithm = new DSPSP_NSGAIIBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxEvaluations(2500)
                .setPopulationSize(100)
                .build() ;
        return this;
    }
}