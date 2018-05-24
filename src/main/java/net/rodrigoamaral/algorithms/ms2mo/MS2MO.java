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
    private TopologyType topology;
    private Archive globalArchive;

    /**
     * Constructor
     */
    public MS2MO(List<ISwarm> swarms, int maxIterations, int swapInterval, TopologyType topology) {
        this.swarms = swarms;
        this.maxIterations = maxIterations;
        this.swapInterval = swapInterval;
        this.topology = topology;
        this.globalArchive = new NonDominatedSolutionListArchive();
    }

    @Override
    public void run() {
        runIterations();
        mergeGlobalArchive();
    }

    private void runIterations() {
        for (int i = 0; i < maxIterations; i++) {
            runSwarms();
            if (isTimeToSwap(i)) {
                swap();
            }
        }
    }

    private void runSwarms() {
        for (ISwarm swarm: swarms) {
            swarm.run();
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
