package net.rodrigoamaral.dspsp.experiment;

import net.rodrigoamaral.dspsp.DSPSProblem;
import net.rodrigoamaral.dspsp.decision.ComparisonMatrix;
import net.rodrigoamaral.dspsp.decision.DecisionMaker;
import net.rodrigoamaral.dspsp.nsgaii.DSPSP_NSGAIIBuilder;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.results.SolutionFileWriter;
import net.rodrigoamaral.dspsp.solution.SchedulingResult;
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
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Runs experiments on DSPSP problem.
 *
 * ExperimentRunner can read problem instances from JSON files in a specific
 * directory.
 *
 * @author Rodrigo Amaral
 *
 */
public class ExperimentRunner {

    private final ExperimentSettings experimentSettings;

    public ExperimentRunner(final ExperimentSettings experimentSettings) {
        this.experimentSettings = experimentSettings;
    }

    private DSPSProblem loadProblemInstance(final String instanceFile) {
        try {
            return new DSPSProblem(instanceFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private DSPSProblem loadProblemInstance(final DynamicProject project) {
        return new DSPSProblem(project);
    }

    public ExperimentSettings getExperimentSettings() {
        return experimentSettings;
    }

    private void runInstance(DSPSProblem problem) {
        SPSPLogger.info("Starting simulation for instance " + problem.getInstanceDescription());

        SPSPLogger.info("Performing initial scheduling...");

        AlgorithmAssembler algorithmAssembler = new AlgorithmAssembler(problem).invoke();
        Algorithm<List<DoubleSolution>> algorithm = algorithmAssembler.getAlgorithm();
        DynamicProject project = algorithmAssembler.getProject();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute() ;

        List<DoubleSolution> population = algorithm.getResult() ;

        long totalComputingTime = algorithmRunner.getComputingTime();

        SPSPLogger.info("Initial scheduling complete.");
        SPSPLogger.info("Elapsed time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));

        new SolutionFileWriter(population)
                .setAlgorithmID("nsga2")
                .setInstanceID(problem.getInstanceDescription())
                .write();

        // Decides on the best initial schedule
        ComparisonMatrix comparisonMatrix = new ComparisonMatrix();
        DoubleSolution initialSchedule = new DecisionMaker(population, comparisonMatrix)
                .chooseInitialSchedule();

        // Loops through rescheduling points
        List<DynamicEvent> reschedulingPoints = project.getEvents();

        DoubleSolution currentSchedule = initialSchedule;

        int reschedulings = 0;
        for (DynamicEvent event: reschedulingPoints) {

            reschedulings++;

            if (project.isFinished()) {
                break;
            }

            SPSPLogger.rescheduling(reschedulings, event);

            SchedulingResult result = reschedule(project, event, currentSchedule);

            totalComputingTime += result.getComputingTime();

            SPSPLogger.info("Rescheduling "+ reschedulings +" complete in " + DurationFormatUtils.formatDuration(result.getComputingTime(), "HH:mm:ss,SSS") + ". ");
            SPSPLogger.info("Elapsed time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));
            SPSPLogger.info("Project current duration: " + project.getTotalDuration());
            SPSPLogger.info("Project current cost    : " + project.getTotalCost());


            new SolutionFileWriter(result.getSchedules())
                    .setAlgorithmID("nsga2")
                    .setInstanceID(problem.getInstanceDescription())
                    .setReschedulingPoint(reschedulings)
                    .write();

            currentSchedule = new DecisionMaker(result.getSchedules(), comparisonMatrix).chooseNewSchedule();

        }


        SPSPLogger.info("Total execution time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));

        // TODO: Write final solution files
    }

    private SchedulingResult reschedule(DynamicProject project, DynamicEvent event, DoubleSolution lastSchedule) {
        project.update(event, lastSchedule);

        DSPSProblem problem = loadProblemInstance(project);


        AlgorithmAssembler algorithmAssembler = new AlgorithmAssembler(problem).invoke();
        Algorithm<List<DoubleSolution>> algorithm = algorithmAssembler.getAlgorithm();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        return new SchedulingResult(algorithm.getResult(),
                algorithmRunner.getComputingTime(),
                problem.getProject().isFinished());
    }

    private void run() {
        for (String instanceFile : experimentSettings.getInstanceFiles()) {
            final DSPSProblem problem = loadProblemInstance(instanceFile);
            runInstance(problem);
        }
    }

    public static void main(String[] args) {
        ExperimentCLI cli = new ExperimentCLI(args);
        ExperimentRunner runner = new ExperimentRunner(cli.getExperimentSettings());
        runner.run();
    }

    // REFACTOR: ExperimentRunner: check if AlgorithmAssembler can go outside this class
//    private static class AlgorithmAssembler {
//        private Problem<DoubleSolution> problem;
//        private Algorithm<List<DoubleSolution>> algorithm;
//        private DynamicProject project;
//
//        public AlgorithmAssembler(Problem<DoubleSolution> problem) {
//            this.problem = problem;
//        }
//
//        public Algorithm<List<DoubleSolution>> getAlgorithm() {
//            return algorithm;
//        }
//
//        public DynamicProject getProject() {
//            return project;
//        }
//
//        public ExperimentRunner.AlgorithmAssembler invoke() {
//            CrossoverOperator<DoubleSolution> crossover;
//            MutationOperator<DoubleSolution> mutation;
//            SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
//
//            project = ((DSPSProblem) problem).getProject();
//
//            double crossoverProbability = 0.9 ;
//            double crossoverDistributionIndex = 20.0 ;
//
//            crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;
//
//            double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
//            double mutationDistributionIndex = 20.0 ;
//
//            mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;
//
//            selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());
//
//            algorithm = new DSPSP_NSGAIIBuilder<>(problem, crossover, mutation)
//                    .setSelectionOperator(selection)
//                    .setMaxEvaluations(2500)
//                    .setPopulationSize(100)
//                    .build() ;
//            return this;
//        }
//    }

}
