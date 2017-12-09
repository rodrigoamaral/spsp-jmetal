package net.rodrigoamaral.dspsp;

import net.rodrigoamaral.dspsp.decision.ComparisonMatrix;
import net.rodrigoamaral.dspsp.decision.DecisionMaker;
import net.rodrigoamaral.dspsp.nsgaii.DSPSP_NSGAIIBuilder;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.solution.SchedulingResult;
import net.rodrigoamaral.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.algorithm.Algorithm;
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
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.List;

public class DSPSP_NSGAIIRunner extends AbstractAlgorithmRunner {


    public static void main(String[] args) throws Exception {
        String filename = "";
        String referenceParetoFront = "" ;
        if (args.length == 1) {
            filename = args[0];
        }
        run(filename, referenceParetoFront);
    }

    private static void run(String inputFile, String refPF) throws Exception {
        Problem<DoubleSolution> problem = loadProjectInstance(inputFile);
        JMetalLogger.logger.info(problem.getName() +
                "(" + ((DSPSProblem)problem).getProject().getTasks().size() + ", " +
                ((DSPSProblem)problem).getProject().getEmployees().size() +
                ") loaded from " + inputFile);


        AlgorithmAssembler algorithmAssembler = new AlgorithmAssembler(problem).invoke();
        Algorithm<List<DoubleSolution>> algorithm = algorithmAssembler.getAlgorithm();
        DynamicProject project = algorithmAssembler.getProject();

        ComparisonMatrix comparisonMatrix = new ComparisonMatrix();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute() ;

        List<DoubleSolution> population = algorithm.getResult() ;

        long totalComputingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Initial scheduling complete. Elapsed time: " + totalComputingTime + " ms");

        // TODO: Test decision making
        // Decides on the best initial schedule
        DoubleSolution initialSchedule = new DecisionMaker(population, comparisonMatrix)
                .chooseInitialSchedule();

        // Loops through rescheduling points
        List<DynamicEvent> reschedulingPoints = project.getEvents();

        DoubleSolution currentSchedule = initialSchedule;
        for (DynamicEvent event: reschedulingPoints) {
            JMetalLogger.logger.info(event + ". Rescheduling... ");

            SchedulingResult result = reschedule(project, event, currentSchedule);

            totalComputingTime += result.getComputingTime();

            JMetalLogger.logger.info("Rescheduling complete in " + result.getComputingTime() + " ms. " +
                    "Elapsed time: " + totalComputingTime + " ms.");

            currentSchedule = new DecisionMaker(result.getSchedules(), comparisonMatrix).chooseNewSchedule();

        }


        JMetalLogger.logger.info("Total execution time: " + totalComputingTime + " ms");

        printFinalSolutionSet(population);
        if (!refPF.equals("")) {
            printQualityIndicators(population, refPF) ;
        }
    }

    private static SchedulingResult reschedule(DynamicProject project_,
                                               DynamicEvent event,
                                               DoubleSolution currentSchedule)  {
        Problem<DoubleSolution> problem = loadProjectInstance(project_);

        AlgorithmAssembler algorithmAssembler = new AlgorithmAssembler(problem).invoke();
        Algorithm<List<DoubleSolution>> algorithm = algorithmAssembler.getAlgorithm();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        return new SchedulingResult(algorithm.getResult(), algorithmRunner.getComputingTime());
    }

    private static DSPSProblem loadProjectInstance(String projectPropertiesFileName) throws FileNotFoundException {
        return new DSPSProblem(projectPropertiesFileName);
    }

    private static DSPSProblem loadProjectInstance(DynamicProject project) {
        return new DSPSProblem(project);
    }

    public static void printFinalSolutionSet(List<? extends Solution<?>> population) {

        String varFile = "D_VAR_NSGA-II.csv";
        String funFile = "D_FUN_NSGA-II.csv";
        new SolutionListOutput(population)
                .setSeparator(";")
                .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
                .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
                .print();

        JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
        JMetalLogger.logger.info("Objectives values have been written to file " + funFile);
        JMetalLogger.logger.info("Variables values have been written to file " + varFile);
    }

    private static class AlgorithmAssembler {
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
                    .setMaxEvaluations(25000)
                    .setPopulationSize(100)
                    .build() ;
            return this;
        }
    }
}
