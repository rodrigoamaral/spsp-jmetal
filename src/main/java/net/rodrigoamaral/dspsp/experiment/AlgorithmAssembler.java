package net.rodrigoamaral.dspsp.experiment;

import net.rodrigoamaral.algorithms.ISwarm;
import net.rodrigoamaral.algorithms.ms2mo.MS2MOBuilder;
import net.rodrigoamaral.algorithms.nsgaii.NSGAIIDynamicBuilder;
import net.rodrigoamaral.algorithms.smpso.SMPSOBuilder;
import net.rodrigoamaral.algorithms.smpso.SMPSODynamic;
import net.rodrigoamaral.algorithms.smpso.SMPSODynamicBuilder;
import net.rodrigoamaral.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.util.List;

/**
 * Assembles an algorithm from given parameters for experiment purposes.
 *
 * @author Rodrigo Amaral
 */
public class AlgorithmAssembler {

    private final String algorithmID;
    private List<DoubleSolution> initialPopulation;

    public AlgorithmAssembler(final String algorithmID) {
        this.algorithmID = algorithmID;
    }

    public String getAlgorithmID() {
        return algorithmID;
    }

    public Algorithm<List<DoubleSolution>> assemble(Problem<DoubleSolution> problem, List<DoubleSolution> initialPopulation) {
        this.initialPopulation = initialPopulation;
        return assemble(problem);
    }

    public Algorithm<List<DoubleSolution>> assemble(Problem<DoubleSolution> problem) {
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;

        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;

        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        // REFACTOR: Make algorithm builder instantiation dynamic
        if ("NSGAII".equals(algorithmID.toUpperCase())) {
            return new NSGAIIBuilder<>(problem, crossover, mutation)
                    .setSelectionOperator(selection)
                    .setMaxEvaluations(2500)
                    .setPopulationSize(100)
                    .build();
        } else if ("NSGAIIDYNAMIC".equals(algorithmID.toUpperCase())) {
                return new NSGAIIDynamicBuilder(problem, crossover, mutation)
                        .setInitialPopulation(initialPopulation)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(2500)
                        .setPopulationSize(100)
                        .build();
        } else if ("SMPSO".equals(algorithmID.toUpperCase())) {
            BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;
            return new SMPSOBuilder((DoubleProblem) problem, archive)
                    .setMutation(mutation)
                    .setMaxIterations(50)
                    .setSwarmSize(100)
                    .setRandomGenerator(new MersenneTwisterGenerator())
                    .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                    .build();
        } else if ("SMPSODYNAMIC".equals(algorithmID.toUpperCase())) {
            BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;
            return new SMPSODynamicBuilder((DoubleProblem) problem, archive)
                    .setInitialPopulation(initialPopulation)
                    .setMutation(mutation)
                    .setMaxIterations(50)
                    .setSwarmSize(100)
                    .setRandomGenerator(new MersenneTwisterGenerator())
                    .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                    .build();
        }  else if ("MS2MO".equals(algorithmID.toUpperCase())) {
            BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

            ISwarm swarm = new SMPSOBuilder((DoubleProblem)problem, archive)
                    .setMutation(mutation)
                    .setMaxIterations(50)
                    .setSwarmSize(100)
                    .setRandomGenerator(new MersenneTwisterGenerator())
                    .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                    .build();

            ISwarm swarm2 = new SMPSOBuilder((DoubleProblem)problem, archive)
                    .setMutation(mutation)
                    .setMaxIterations(50)
                    .setSwarmSize(100)
                    .setRandomGenerator(new MersenneTwisterGenerator())
                    .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                    .build();

            return new MS2MOBuilder((DoubleProblem)problem)
                    .addSwarm(swarm)
                    .addSwarm(swarm2)
                    .setMaxIterations(1)
                    .build();
        }
        else {
            throw new IllegalArgumentException("Invalid algorithm ID: " + algorithmID);
        }

    }
}