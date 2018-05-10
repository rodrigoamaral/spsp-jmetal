package net.rodrigoamaral.algorithms.ms2mo;

import net.rodrigoamaral.algorithms.ISwarm;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.List;

public class MS2MO implements Algorithm {

    private List<ISwarm> swarms;

    private int maxIterations;
    private int swapInterval;

    private int archiveSize;
    private ArchiveType firstArchiveType;
    private ArchiveType secondArchiveType;
    private ArchiveType currentArchiveType;

    private TopologyType topology;
    private Archive globalArchive;

    /**
     * Constructor
     */
    public MS2MO(List<ISwarm> swarms, int maxIterations, int swapInterval, TopologyType topology,
                 int archiveSize, ArchiveType firstArchiveType, ArchiveType secondArchiveType) {
        this.swarms = swarms;
        this.maxIterations = maxIterations;
        this.swapInterval = swapInterval;
        this.topology = topology;
        this.archiveSize = archiveSize;
        this.firstArchiveType = firstArchiveType;
        this.secondArchiveType = secondArchiveType;
        this.currentArchiveType = firstArchiveType;
        this.globalArchive = new NonDominatedSolutionListArchive();
    }

    @Override
    public void run() {
        runIterations();
        mergeGlobalArchive();
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
//            System.out.println("i = " + i);
//            System.out.println("--------------");
            runSwarms();
            if (isTimeToSwap(i)) {
                swap();
            }
        }
    }

    private void runSwarms() {
        for (ISwarm swarm: swarms) {
//            System.out.println("swarm = " + swarm);
            swarm.run();
        }
    }

    private boolean isTimeToSwap(int iteration) {
        return iteration % swapInterval == 0;
    }

    private void swap() {
//        System.out.println("Swapping: " + topology);
        if (topology == TopologyType.RING) {
            makeRingCommunication();
        } else if (topology == TopologyType.BROADCAST) {
            makeBroadcastCommunication();
        }
    }

    private void makeRingCommunication() {
        int numberOfSwarms = swarms.size();
        int last = numberOfSwarms - 1;
        for (int i = 0; i < numberOfSwarms; i++) {
            int previous = i != 0 ? i - 1 : last;
            int next = i != last ? i + 1 : 0;
            mergeArchives(previous, i);
            mergeArchives(next, i);
        }
    }

    private void makeBroadcastCommunication() {
        int numberOfSwarms = swarms.size();
        for (int i = 0; i < numberOfSwarms; i++) {
            for (int j = 0; j < numberOfSwarms; j++) {
                mergeArchives(i, j);
            }
        }
    }

    private void mergeArchives(int fromIndex, int toIndex) {
        if (fromIndex != toIndex) {
            ISwarm swarm = swarms.get(toIndex);
            Archive<DoubleSolution> archive = swarms.get(fromIndex).getLeaders();
            swarm.mergeArchive(archive);
        }
    }

    /**
     * Merges all leaders into a global archive
     */
    private void mergeGlobalArchive() {
        for (ISwarm swarm: swarms) {
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
        return this.getClass().getName();
    }

    @Override
    public String getDescription() {
        return "Multiple Swarm Multiple Strategy Many Objective Algorithm";
    }
}
