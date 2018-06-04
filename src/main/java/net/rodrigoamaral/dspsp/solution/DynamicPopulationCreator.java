package net.rodrigoamaral.dspsp.solution;


import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DynamicPopulationCreator {

    private DoubleProblem problem;
    private SchedulingHistory history;
    private int size;
    private double propGlobalSolutions;
    private double propPreviousEventSolutions;

    public DynamicPopulationCreator(DoubleProblem problem, SchedulingHistory history) {
        init(problem, history);
        this.propGlobalSolutions = 0.3;
        this.propPreviousEventSolutions = 0.2;
        this.size = 100;
    }

    public DynamicPopulationCreator(DoubleProblem problem, SchedulingHistory history, double propGlobalSolutions, double propPreviousEventSolutions, int size) {
        if (propGlobalSolutions + propPreviousEventSolutions > 1.0) {
            throw new IllegalArgumentException("Sum of solution proportions must not exceed 1.0");
        }

        init(problem, history);
        this.propGlobalSolutions = propGlobalSolutions;
        this.propPreviousEventSolutions = propPreviousEventSolutions;
        this.size = size;
    }

    private void init(DoubleProblem problem, SchedulingHistory history) {
        this.problem = problem;
        this.history = history;
    }


    public List<DoubleSolution> create(int event) {

        List<DoubleSolution> population = new ArrayList<>(size);

        population.addAll(getRandomSublist(history.getAllSolutions(), propGlobalSolutions));
        population.addAll(getRandomSublist(history.getPrevious(event), propPreviousEventSolutions));
        population.addAll(getRandomSolutions(size - population.size()));

        return population;
    }

    private List<DoubleSolution> getRandomSublist(List<DoubleSolution> list, double proportionalSize) {
        int n = (int) Math.round(size * proportionalSize);
        Collections.shuffle(list);

        try {
            return new ArrayList<>(list.subList(0, n));
        } catch (IndexOutOfBoundsException iobe) {
            SPSPLogger.warning(iobe.getMessage() + " - List size: " + list.size());
            iobe.printStackTrace();
        } finally {
            return new ArrayList<>();
        }

    }

    private List<DoubleSolution> getRandomSolutions(int listSize) {
        List<DoubleSolution> randomList = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            randomList.add(new DefaultDoubleSolution(problem));
        }

        return randomList;
    }
}
