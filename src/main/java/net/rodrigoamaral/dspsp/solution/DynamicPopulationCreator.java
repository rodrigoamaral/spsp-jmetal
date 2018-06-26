package net.rodrigoamaral.dspsp.solution;


import net.rodrigoamaral.dspsp.adapters.SolutionConverter;
import net.rodrigoamaral.dspsp.experiment.ExperimentSettings;
import net.rodrigoamaral.dspsp.solution.repair.IScheduleRepairStrategy;
import net.rodrigoamaral.dspsp.solution.repair.ScheduleRepairStrategy;
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
    private double propRepairedSolutions;
    private double propPreviousEventSolutions;
    private IScheduleRepairStrategy repairStrategy;

//    REFACTOR: Remove string literals
    public DynamicPopulationCreator(DoubleProblem problem, SchedulingHistory history, ExperimentSettings settings, String algorithmID, IScheduleRepairStrategy repairStrategy) {

        this.problem = problem;
        this.history = history;
        this.repairStrategy = repairStrategy;
        this.propRepairedSolutions = settings.getRepairedSolutions();
        this.propPreviousEventSolutions = settings.getHistPropPreviousEventSolutions();
        if (propRepairedSolutions + propPreviousEventSolutions > 1.0) {
            throw new IllegalArgumentException("Sum of repairedSolution proportions must not exceed 1.0");
        }
        if (algorithmID.equals("MS2MO") ||
                algorithmID.equals("MS2MODynamic") ||
                algorithmID.equals("SMPSO") ||
                algorithmID.equals("SMPSODynamic")) {
            this.size = settings.getSwarmSize();
        } else {
            this.size = settings.getPopulationSize();
        }
    }

    public List<DoubleSolution> create(int event) {

        List<DoubleSolution> population = new ArrayList<>(size);

//        population.addAll(getRandomSublist(history.getAllSolutions(), propRepairedSolutions));
//        population.addAll(getRandomSublist(history.getNonDominatedSolutions(), propRepairedSolutions));
        if (repairStrategy != null) {
            population.addAll(repairedSolutions());
        }
        population.addAll(getRandomSublist(history.getPrevious(event), propPreviousEventSolutions));
        population.addAll(getRandomSolutions(size - population.size()));

        return population;
    }

    private List<DoubleSolution> repairedSolutions() {
        List<DoubleSolution> repairedSolutions = new ArrayList<>();

        DoubleSolution seed = this.repairStrategy.repair();
        repairedSolutions.add(seed);
// TODO: generate some variations
//        getRandomSublist(repairedSolutions(), propRepairedSolutions);
        return repairedSolutions;
    }


    private List<DoubleSolution> getRandomSublist(List<DoubleSolution> list, double proportionalSize) {
        List<DoubleSolution> randomSublist = new ArrayList<>();

        int n = (int) Math.round(size * proportionalSize);
        Collections.shuffle(list);

        try {
            randomSublist = new ArrayList<>(list.subList(0, n));
        } catch (IndexOutOfBoundsException iobe) {
            SPSPLogger.warning(iobe.getMessage() + " - List size: " + list.size());
            iobe.printStackTrace();
        } finally {
            return randomSublist;
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
