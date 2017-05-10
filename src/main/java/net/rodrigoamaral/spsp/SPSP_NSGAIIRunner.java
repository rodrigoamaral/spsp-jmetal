package net.rodrigoamaral.spsp;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.List;

public class SPSP_NSGAIIRunner extends AbstractAlgorithmRunner {


    public static void printFinalSolutionSet(List<? extends Solution<?>> population) {

        String varFile = "VAR_NSGA-II.csv";
        String funFile = "FUN_NSGA-II.csv";
        new SolutionListOutput(population)
                .setSeparator(";")
                .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
                .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
                .print();

        JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
        JMetalLogger.logger.info("Objectives values have been written to file " + funFile);
        JMetalLogger.logger.info("Variables values have been written to file " + varFile);
    }

    private static SPSProblem loadProjectInstanceFromFile(String projectPropertiesFileName) throws FileNotFoundException {
        return new SPSProblem(projectPropertiesFileName);
    }

    public static void main(String[] args) throws JMetalException, FileNotFoundException {
        String filename = "";
        String referenceParetoFront = "" ;
        if (args.length == 1) {
            filename = args[0];
        }
        run(filename, referenceParetoFront);
    }

    private static void run(String inputFile, String refPF) throws FileNotFoundException {
        Problem<DoubleSolution> problem;
        Algorithm<List<DoubleSolution>> algorithm;
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        // Creates a SPSP project instance
        problem = loadProjectInstanceFromFile(inputFile);

        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

        selection = new BinaryTournamentSelection<>(
                new RankingAndCrowdingDistanceComparator<>());

        algorithm = new NSGAIIBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .build() ;

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute() ;

        List<DoubleSolution> population = algorithm.getResult() ;
        long computingTime = algorithmRunner.getComputingTime() ;

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!refPF.equals("")) {
            printQualityIndicators(population, refPF) ;
        }
    }
}
