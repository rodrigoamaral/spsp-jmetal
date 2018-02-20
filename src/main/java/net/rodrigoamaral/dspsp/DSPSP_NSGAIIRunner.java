package net.rodrigoamaral.dspsp;

import net.rodrigoamaral.dspsp.cli.CLI;
import net.rodrigoamaral.dspsp.decision.ComparisonMatrix;
import net.rodrigoamaral.dspsp.decision.DecisionMaker;
import net.rodrigoamaral.dspsp.nsgaii.DSPSP_NSGAIIBuilder;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.solution.SchedulingResult;
import net.rodrigoamaral.jmetal.util.fileoutput.SolutionListOutput;
import net.rodrigoamaral.logging.SPSPLogger;
import org.apache.commons.lang3.time.DurationFormatUtils;
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
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.List;

public class DSPSP_NSGAIIRunner extends AbstractAlgorithmRunner {

    private static int reschedulings = 0;
    private static double totalDuration = 0;

    private static void incrementCounter() {
        reschedulings += 1;
    }


    public static void main(String[] args) throws Exception {

        final CLI cli = new CLI(args);

        String inputFile = cli.getInstanceInputFile();

        SPSPLogger.info("Parsing instance file: " + inputFile);

        DSPSProblem problem = loadProjectInstance(inputFile);

        SPSPLogger.info("Parsing complete. Performing initial scheduling...");

        run(problem);
    }


    private static void run(DSPSProblem problem) throws Exception {

        AlgorithmAssembler algorithmAssembler = new AlgorithmAssembler(problem).invoke();
        Algorithm<List<DoubleSolution>> algorithm = algorithmAssembler.getAlgorithm();
        DynamicProject project = algorithmAssembler.getProject();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute() ;

        List<DoubleSolution> population = algorithm.getResult() ;

        long totalComputingTime = algorithmRunner.getComputingTime();

        SPSPLogger.info("Initial scheduling complete.");
        SPSPLogger.info("Elapsed time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));

        writeSolutionFile(population, "NSGA2", null);


        // Decides on the best initial schedule
        ComparisonMatrix comparisonMatrix = new ComparisonMatrix();
        DoubleSolution initialSchedule = new DecisionMaker(population, comparisonMatrix)
                .chooseInitialSchedule();

        // Loops through rescheduling points
        List<DynamicEvent> reschedulingPoints = project.getEvents();

        DoubleSolution currentSchedule = initialSchedule;
        for (DynamicEvent event: reschedulingPoints) {

            incrementCounter();

            if (project.isFinished()) {
                break;
            }

//            SPSPLogger.info("\nRescheduling "+ reschedulings + " : " + event.description());
            SPSPLogger.rescheduling(reschedulings, event);
            SchedulingResult result = reschedule(project, event, currentSchedule);

            totalComputingTime += result.getComputingTime();

            SPSPLogger.info("Rescheduling "+ reschedulings +" complete in " + DurationFormatUtils.formatDuration(result.getComputingTime(), "HH:mm:ss,SSS") + ". ");
            SPSPLogger.info("Elapsed time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));
            SPSPLogger.info("Project current duration: " + project.getTotalDuration());
            SPSPLogger.info("Project current cost    : " + project.getTotalCost());


            writeSolutionFile(result.getSchedules(), "NSGA2", reschedulings);

            currentSchedule = new DecisionMaker(result.getSchedules(), comparisonMatrix).chooseNewSchedule();


        }


        SPSPLogger.info("Total execution time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));

        printFinalSolutionSet(population);
//        if (!refPF.equals("")) {
//            printQualityIndicators(population, refPF) ;
//        }
    }

    private static SchedulingResult reschedule(DynamicProject project,
                                               DynamicEvent event,
                                               DoubleSolution lastSchedule) throws Exception {

        totalDuration = project.update(event, lastSchedule);

        DSPSProblem problem = loadProjectInstance(project);


        AlgorithmAssembler algorithmAssembler = new AlgorithmAssembler(problem).invoke();
        Algorithm<List<DoubleSolution>> algorithm = algorithmAssembler.getAlgorithm();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        return new SchedulingResult(algorithm.getResult(),
                                    algorithmRunner.getComputingTime(),
                                    problem.getProject().isFinished());
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

        SPSPLogger.trace("Random seed: " + JMetalRandom.getInstance().getSeed());
        SPSPLogger.info("Objective values file: " + funFile);
        SPSPLogger.info("Variables values file: " + varFile);
    }

    public static void writeSolutionFile(List<? extends Solution<?>> population, String algorithm, Integer reschedulingCounter) {
        String varFile = "D_VAR_" + algorithm + ".initial.csv";
        String funFile = "D_FUN_" + algorithm + ".initial.csv";
        if (reschedulingCounter != null) {
            varFile = "D_VAR_" + algorithm + "." + reschedulingCounter + ".csv";
            funFile = "D_FUN_" + algorithm + "." + reschedulingCounter + ".csv";
        }
        new SolutionListOutput(population)
                .setSeparator(";")
                .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
                .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
                .print();

        SPSPLogger.trace("Random seed: " + JMetalRandom.getInstance().getSeed());
        SPSPLogger.info("Objective values file: " + funFile);
        SPSPLogger.info("Variables values file: " + varFile);
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
                    .setMaxEvaluations(2500)
//                    .setMaxEvaluations(25000)
                    .setPopulationSize(100)
                    .build() ;
            return this;
        }
    }
}
