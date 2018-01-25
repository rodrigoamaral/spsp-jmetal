package net.rodrigoamaral.spsp.meapr;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MultiSwarmMEAPR implements Algorithm {

    private List<MEAPR> swarms;
    private DoubleProblem problem;

    private double c1Max;
    private double c1Min;

    private double c2Max;
    private double c2Min;
    private double r1Max;
    private double r1Min;
    private double r2Max;
    private double r2Min;
    private double weightMax;
    private double weightMin;
    private double changeVelocity1;
    private double changeVelocity2;
    private int swarmSize;
    private int numberOfSwarms;

    private int maxIterations;
    private int swapInterval;
    private int iterations;

    private int archiveSize;
    private ArchiveType firstArchiveType;
    private ArchiveType secondArchiveType;
    private ArchiveType currentArchiveType;

    private GenericSolutionAttribute<DoubleSolution, DoubleSolution> localBest;
    private double[][] speed;

    private JMetalRandom randomGenerator;

    private Comparator<DoubleSolution> dominanceComparator;

    private MutationOperator<DoubleSolution> mutation;

    private double deltaMax[];
    private double deltaMin[];

    private SolutionListEvaluator<DoubleSolution> evaluator;
    private TopologyType topology;
    private Archive globalArchive;

    /**
     * Constructor
     */
    public MultiSwarmMEAPR(DoubleProblem problem, int swarmSize,
                           int numberOfSwarms, MutationOperator<DoubleSolution> mutationOperator, int maxIterations, int swapInterval, TopologyType topology, double r1Min, double r1Max,
                           double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                           double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                           SolutionListEvaluator<DoubleSolution> evaluator, int archiveSize, ArchiveType firstArchiveType, ArchiveType secondArchiveType) {

        swarms = new ArrayList<>();

        this.problem = problem;
        this.swarmSize = swarmSize;
        this.numberOfSwarms = numberOfSwarms;
        this.mutation = mutationOperator;
        this.maxIterations = maxIterations;
        this.swapInterval = swapInterval;
        this.topology = topology;
        this.archiveSize = archiveSize;
        this.firstArchiveType = firstArchiveType;
        this.secondArchiveType = secondArchiveType;
        this.currentArchiveType = firstArchiveType;
        this.globalArchive = new NonDominatedSolutionListArchive();

        this.r1Max = r1Max;
        this.r1Min = r1Min;
        this.r2Max = r2Max;
        this.r2Min = r2Min;
        this.c1Max = c1Max;
        this.c1Min = c1Min;
        this.c2Max = c2Max;
        this.c2Min = c2Min;
        this.weightMax = weightMax;
        this.weightMin = weightMin;
        this.changeVelocity1 = changeVelocity1;
        this.changeVelocity2 = changeVelocity2;

        randomGenerator = JMetalRandom.getInstance();
        this.evaluator = evaluator;

        dominanceComparator = new DominanceComparator<DoubleSolution>();
        localBest = new GenericSolutionAttribute<DoubleSolution, DoubleSolution>();
        speed = new double[swarmSize][problem.getNumberOfVariables()];

        deltaMax = new double[problem.getNumberOfVariables()];
        deltaMin = new double[problem.getNumberOfVariables()];
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            deltaMax[i] = (problem.getUpperBound(i) - problem.getLowerBound(i)) / 2.0;
            deltaMin[i] = -deltaMax[i];
        }
    }

    @Override
    public void run() {
        System.out.println("BEGIN EXECUTION - " + System.currentTimeMillis());
        System.out.println("Max iterations: " + maxIterations);
        System.out.println("Number of swarms: " + numberOfSwarms);
        System.out.println("Swarm size: " + swarmSize);
        System.out.println("----------------------------");

        initializeSwarms();
        runIterations();
        mergeGlobalArchive();

        System.out.println("----------------------------");
        System.out.println("END EXECUTION - " + System.currentTimeMillis());
    }

    private void initializeSwarms() {
        for (int i = 0; i < numberOfSwarms; i++) {
            MEAPR swarm = new MEAPRBuilder(problem, swarmSize, archiveSize, getCurrentArchiveType(),
                    mutation, evaluator)
                    .build();
            swarms.add(swarm);
        }
    }

    private ArchiveType getCurrentArchiveType() {
        System.out.println(currentArchiveType);
        if (currentArchiveType == firstArchiveType) {
            currentArchiveType = secondArchiveType;
        } else {
            currentArchiveType = firstArchiveType;
        }
        return currentArchiveType;
    }

    private void runIterations() {
        for (int i = 0; i < maxIterations; i++) {
            System.out.println("\tMultiSwarm Iteration " + (i+1) + "/" + maxIterations);
            runSwarms();
            if (isTimeToSwap(i)) {
                swap();
            }
        }
    }

    private void runSwarms() {
        int count = 0;
        for (MEAPR swarm: swarms) {
            System.out.println("\t\tSwarm " + (count+1) + "/" + swarms.size());
            swarm.run();
            count++;
        }
    }

    private boolean isTimeToSwap(int iteration) {
        return iteration % swapInterval == 0;
    }

    private void swap() {
        if (topology == TopologyType.RING) {
            makeRingCommunication();
        } else if (topology == TopologyType.BROADCAST) {
            makeBroadcastCommunication();
        }
    }

    private void makeRingCommunication() {
        int last = numberOfSwarms - 1;
        for (int i = 0; i < numberOfSwarms; i++) {
            int previous = i != 0 ? i - 1 : last;
            int next = i != last ? i + 1 : 0;
            mergeArchives(previous, i);
            mergeArchives(next, i);
        }
    }

    private void makeBroadcastCommunication() {
        for (int i = 0; i < numberOfSwarms; i++) {
            for (int j = 0; j < numberOfSwarms; j++) {
                mergeArchives(i, j);
            }
        }
    }

    private void mergeArchives(int fromIndex, int toIndex) {
        if (fromIndex != toIndex) {
            MEAPR swarm = swarms.get(toIndex);
            Archive<DoubleSolution> archive = swarms.get(fromIndex).getLeaders();
            swarm.mergeArchive(archive);
        }
    }

    /**
     * Merges all leaders into a global archive
     */
    private void mergeGlobalArchive() {
        for (MEAPR swarm: swarms) {
            Archive archive = swarm.getLeaders();
            for (int i = 0; i < archive.size(); i++) {
                globalArchive.add(archive.get(i));
            }
        }
    }

    @Override
    public Object getResult() {
        return globalArchive.getSolutionList();
    }

    @Override
    public String getName() {
        return "MultiSwarmMEAPR";
    }

    @Override
    public String getDescription() {
        return "MEAPR using multiple swarms";
    }
}
