package net.rodrigoamaral.dspsp.experiment;

import net.rodrigoamaral.dspsp.DSPSProblem;
import net.rodrigoamaral.dspsp.decision.ComparisonMatrix;
import net.rodrigoamaral.dspsp.decision.DecisionMaker;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.results.SolutionFileWriter;
import net.rodrigoamaral.dspsp.solution.DynamicPopulationCreator;
import net.rodrigoamaral.dspsp.solution.SchedulingHistory;
import net.rodrigoamaral.dspsp.solution.SchedulingResult;
import net.rodrigoamaral.logging.SPSPLogger;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;

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
    private SchedulingHistory history;
    private int reschedulings;

    public ExperimentRunner(final ExperimentSettings experimentSettings) {
        this.experimentSettings = experimentSettings;
        this.history = new SchedulingHistory();
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

    private void runInstance(DSPSProblem problem, AlgorithmAssembler assembler, int run) {

        final String algorithmID = assembler.getAlgorithmID();

        reschedulings = 0;

        SPSPLogger.info("Starting simulation -> algorithm: " + algorithmID + "; " +
                                               "instance: " + problem.getInstanceDescription());

        SPSPLogger.info("Performing initial scheduling...");

        Algorithm<List<DoubleSolution>> algorithm = assembler.assemble(problem);

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute() ;

        List<DoubleSolution> population = algorithm.getResult() ;

        history.put(reschedulings, population);

        long totalComputingTime = algorithmRunner.getComputingTime();

        SPSPLogger.info("Initial scheduling complete.");
        SPSPLogger.info("Elapsed time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));

        new SolutionFileWriter(population)
                .setAlgorithmID(algorithmID)
                .setInstanceID(problem.getInstanceDescription())
                .setRunNumber(run)
                .setSeparator(" ")
                .write();

        // Decides on the best initial schedule
        ComparisonMatrix comparisonMatrix = new ComparisonMatrix();
        DoubleSolution initialSchedule = new DecisionMaker(population, comparisonMatrix)
                .chooseInitialSchedule();

        // Loops through rescheduling points
        DynamicProject project = problem.getProject();
        List<DynamicEvent> reschedulingPoints = project.getEvents();

        DoubleSolution currentSchedule = initialSchedule;

        for (DynamicEvent event: reschedulingPoints) {

            reschedulings++;

            if (project.isFinished()) {
                break;
            }

            SPSPLogger.rescheduling(reschedulings, event, run, experimentSettings.getNumberOfRuns());

            SchedulingResult result = reschedule(project, event, currentSchedule, assembler);

            history.put(reschedulings, result.getSchedules());


            totalComputingTime += result.getComputingTime();

            SPSPLogger.info("Rescheduling "+ reschedulings +" complete in " + DurationFormatUtils.formatDuration(result.getComputingTime(), "HH:mm:ss,SSS") + ". ");
            SPSPLogger.info("Elapsed time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));
            SPSPLogger.info("Project current duration: " + project.getTotalDuration());
            SPSPLogger.info("Project current cost    : " + project.getTotalCost());


            new SolutionFileWriter(result.getSchedules())
                    .setAlgorithmID(algorithmID)
                    .setInstanceID(problem.getInstanceDescription())
                    .setRunNumber(run)
                    .setReschedulingPoint(reschedulings)
                    .setSeparator(" ")
                    .write();

            currentSchedule = new DecisionMaker(result.getSchedules(), comparisonMatrix).chooseNewSchedule();

        }


        SPSPLogger.info("Total execution time: " + DurationFormatUtils.formatDuration(totalComputingTime, "HH:mm:ss,SSS"));

        // TODO: Write final solution files
    }

    private SchedulingResult reschedule(DynamicProject project, DynamicEvent event, DoubleSolution lastSchedule, AlgorithmAssembler assembler) {

        project.update(event, lastSchedule);

        DSPSProblem problem = loadProblemInstance(project);

        Algorithm<List<DoubleSolution>> algorithm;

        // First rescheduling doesn't take initial population
        if (reschedulings > 1) {
            List<DoubleSolution> initialPopulation = new DynamicPopulationCreator(problem, history).create(reschedulings);
            algorithm = assembler.assemble(problem, initialPopulation);
        } else {
            algorithm = assembler.assemble(problem);
        }

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        return new SchedulingResult(algorithm.getResult(),
                algorithmRunner.getComputingTime(),
                problem.getProject().isFinished());
    }

    /**
     * Executes algorithms for each problem instance.
     *
     * Both instances and algorithms are passed in settings file loaded in
     * experimentSettings.
     *
     */
    public void run() {
        for (String instanceFile : experimentSettings.getInstanceFiles()) {
            for (String algorithmID : experimentSettings.getAlgorithms()) {
                final Integer numberOfRuns = experimentSettings.getNumberOfRuns();
                for (int run = 1; run <= numberOfRuns; run++) {
                    SPSPLogger.printRun(run, numberOfRuns);
                    final DSPSProblem problem = loadProblemInstance(instanceFile);
                    AlgorithmAssembler assembler = new AlgorithmAssembler(algorithmID);
                    runInstance(problem, assembler, run);
                }
            }
        }
    }

}
