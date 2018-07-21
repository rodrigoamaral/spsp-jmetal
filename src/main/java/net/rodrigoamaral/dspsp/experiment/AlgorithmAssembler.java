package net.rodrigoamaral.dspsp.experiment;

import net.rodrigoamaral.algorithms.ISwarm;
import net.rodrigoamaral.algorithms.ms2mo.MS2MOBuilder;
import net.rodrigoamaral.algorithms.nsgaii.NSGAIIDynamicBuilder;
import net.rodrigoamaral.algorithms.smpso.SMPSOBuilder;
import net.rodrigoamaral.algorithms.smpso.SMPSODynamicBuilder;
import net.rodrigoamaral.dspsp.solution.mutation.DSPSPRepairMutation;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Assembles an algorithm from given parameters for experiment purposes.
 *
 * @author Rodrigo Amaral
 */
public class AlgorithmAssembler {

    private final String algorithmID;
    private List<DoubleSolution> initialPopulation;
    private int objectiveEvaluations = 12000;
    private int numberOfSwarms = 30;
    private int swarmSize = 160;
    private final int maxMultiSwarmIterations;
    private int populationSize = 100;

    public AlgorithmAssembler(final String algorithmID, ExperimentSettings settings) {
        this.objectiveEvaluations = settings.getObjectiveEvaluations();
        this.numberOfSwarms = settings.getNumberOfSwarms();
        this.swarmSize = settings.getSwarmSize();
        this.algorithmID = algorithmID;
        this.populationSize = settings.getPopulationSize();
        maxMultiSwarmIterations = getMaxMultiSwarmIterations();
    }

    private int getMaxMultiSwarmIterations() {
        return (objectiveEvaluations / numberOfSwarms) / swarmSize;
    }

    private int getMaxIterations() {
        return objectiveEvaluations / populationSize;
    }

    public String getAlgorithmID() {
        return algorithmID;
    }

    public Algorithm<List<DoubleSolution>> assemble(Problem<DoubleSolution> problem, List<DoubleSolution> initialPopulation) {
        this.initialPopulation = initialPopulation;
        return assemble(problem);
    }

    public void setObjectiveEvaluations(int objectiveEvaluations) {
        this.objectiveEvaluations = objectiveEvaluations;
    }

    public int getObjectiveEvaluations() {
        return objectiveEvaluations;
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
                    .setMaxEvaluations(getMaxIterations())
                    .setPopulationSize(populationSize)
                    .build();
        } else if ("NSGAIIDYNAMIC".equals(algorithmID.toUpperCase())) {
                return new NSGAIIDynamicBuilder(problem, crossover, mutation)
                        .setInitialPopulation(initialPopulation)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(getMaxIterations())
                        .setPopulationSize(populationSize)
                        .build();
        } else if ("SMPSO".equals(algorithmID.toUpperCase())) {
            BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(populationSize) ;
            return new SMPSOBuilder((DoubleProblem) problem, archive)
                    .setMutation(mutation)
                    .setMaxIterations(getMaxIterations())
                    .setSwarmSize(populationSize)
                    .setRandomGenerator(new MersenneTwisterGenerator())
                    .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                    .build();
        } else if ("SMPSODYNAMIC".equals(algorithmID.toUpperCase())) {
            BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(populationSize) ;
            return new SMPSODynamicBuilder((DoubleProblem) problem, archive)
                    .setInitialPopulation(initialPopulation)
                    .setMutation(mutation)
                    .setMaxIterations(getMaxIterations())
                    .setSwarmSize(populationSize)
                    .setRandomGenerator(new MersenneTwisterGenerator())
                    .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                    .build();
        }  else if ("MS2MO".equals(algorithmID.toUpperCase())) {

            List<ISwarm> swarms = createSwarms((DoubleProblem) problem, mutation, null);

            return new MS2MOBuilder((DoubleProblem)problem)
                    .addSwarms(swarms)
                    .setMaxIterations(maxMultiSwarmIterations)
                    .build();

        } else if ("MS2MODYNAMIC".equals(algorithmID.toUpperCase())) {
//            mutation = new DSPSPRepairMutation();

            List<ISwarm> swarms = createSwarms((DoubleProblem) problem, mutation, initialPopulation);

            return new MS2MOBuilder((DoubleProblem)problem)
                    .addSwarms(swarms)
                    .setMaxIterations(maxMultiSwarmIterations)
                    .build();
        }
        else {
            throw new IllegalArgumentException("Invalid algorithm ID: " + algorithmID);
        }

    }

    private List<ISwarm> createSwarms(DoubleProblem problem, MutationOperator<DoubleSolution> mutation, List<DoubleSolution> initialPopulation_) {
        BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

        List<ISwarm> swarms = new ArrayList<>();
        for (int i = 0; i < numberOfSwarms; i++) {
            if (initialPopulation_ == null) {
                swarms.add(
                    new SMPSOBuilder(problem, archive)
                            .setMutation(mutation)
                            .setMaxIterations(1)
                            .setSwarmSize(swarmSize)
                            .setRandomGenerator(new MersenneTwisterGenerator())
                            .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                            .build()
                );
            } else {
                swarms.add(
                    new SMPSODynamicBuilder(problem, archive)
                            .setInitialPopulation(initialPopulation_)
                            .setMutation(mutation)
                            .setMaxIterations(1)
                            .setSwarmSize(swarmSize)
                            .setRandomGenerator(new MersenneTwisterGenerator())
                            .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                            .build()
                );
            }
        }
        return swarms;
    }


}