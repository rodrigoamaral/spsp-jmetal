package net.rodrigoamaral.dspsp.solution;


import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

import java.util.*;

public class SchedulingHistory  {

    Map<Integer, List<DoubleSolution>> history;
    Archive<DoubleSolution> archive;

    public SchedulingHistory() {
        this.history = new HashMap<>();
        this.archive = new NonDominatedSolutionListArchive<>();
    }

    private boolean addToArchive(List<DoubleSolution> population) {
        int solutionsAdded = 0;
        for (DoubleSolution s: population) {
            boolean added = archive.add(s);
            if (added) {
                solutionsAdded++;
            }
        }
        return solutionsAdded > 0;
    }

    /**
     * Adds population to the scheduling history.
     *
     * Instances of this class also keep a non-dominated repairedSolution
     * archive. The first scheduling doesn't go into history, because it
     * corresponds to the initial static scheduling, with only 3 objectives.
     *
     * @param reschedulingIndex
     * @param population
     */
    public void put(Integer reschedulingIndex, List<DoubleSolution> population) {
        if (reschedulingIndex > 0) {
            history.put(reschedulingIndex, population);
            addToArchive(population);
        }
    }

    public List<DoubleSolution> get(Integer index) {
        return history.get(index);
    }

    public List<DoubleSolution> getPrevious(Integer index) {
        if (index <= 0) {
            throw new IllegalArgumentException("Event must be greater than zero.");
        }
        return get(index - 1);
    }

    public List<DoubleSolution> getAllSolutions() {
        List<DoubleSolution> all = new ArrayList<>();
        for (List<DoubleSolution> population: history.values()) {
            all.addAll(population);
        }
        return all;
    }

    public List<DoubleSolution> getNonDominatedSolutions() {
        return archive.getSolutionList();
    }
}
