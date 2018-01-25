package net.rodrigoamaral.jmetal.util.archive.impl;


import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionsInObjectiveSpace;
import org.uma.jmetal.util.comparator.DominanceComparator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class IdealArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {

    private int objectives_;
    private Comparator comparator;


    /**
     * Stores a <code>Distance</code> object, for distances utilities
     */
    private Distance distance_;

    /**
     * Constructor.
     * @param maxSize The maximum size of the archive.
     * @param numberOfObjectives The number of objectives.
     */
    public IdealArchive(int maxSize, int numberOfObjectives) {
        super(maxSize);
        objectives_       = numberOfObjectives;
        distance_		  = new EuclideanDistanceBetweenSolutionsInObjectiveSpace();
        comparator = new DominanceComparator();
    }

    @Override
    public void prune() {
        if (getSolutionList().size() > getMaxSize()) {
            Solution ideal = getIdealSolutions(getSolutionList(), false).get(objectives_).get(0);
            double[] eucDistances = new double[getSolutionList().size()];
            for (int i = 0; i < getSolutionList().size(); i++) {
                Solution s = getSolutionList().get(i);
                eucDistances[i] = distance_.getDistance(ideal, s);
                BigDecimal b = new BigDecimal(eucDistances[i]);
                eucDistances[i] = (b.setScale(5, BigDecimal.ROUND_UP)).doubleValue();
            }

            double highDistanceValue = 0;
            int index = -1;
            for (int i = 0; i < eucDistances.length; i++) {
                if (eucDistances[i] >= highDistanceValue) {
                    highDistanceValue = eucDistances[i];
                    index = i;
                }
            }
            getSolutionList().remove(index);
        }
    }

    public ArrayList<List<S>> getIdealSolutions(List<S> front, boolean prox_ideal) {
        ArrayList<List<S>> extremos2 = new ArrayList<List<S>>();
        for (int i = 0; i < objectives_ + 1; i++) {
            List<S> extremo_i = new ArrayList<>(front.size());
            extremos2.add(extremo_i);
        }

        S ideal = initialIdealSolution(front, objectives_);

        for (int i = 0; i < front.size(); i++) {
            S rep = front.get(i);
            for (int j = 0; j < objectives_; j++) {
                if (rep.getObjective(j) <= ideal.getObjective(j)) {
                    if (rep.getObjective(j) < ideal.getObjective(j))
                        extremos2.get(j).clear();
                    ideal.setObjective(j, rep.getObjective(j));
                    extremos2.get(j).add(rep);
                }
            }
        }
        extremos2.get(objectives_).add(ideal);

        if (prox_ideal) {
            double menorDist = Double.MAX_VALUE;
            for (int i = 0; i < front.size(); i++) {
                S rep = front.get(i);
                double distancia = distance_.getDistance(rep, ideal);
                if (distancia < menorDist) {
                    menorDist = distancia;
                    extremos2.get(objectives_).clear();
                    extremos2.get(objectives_).add(rep);
                }
            }
        }

        return extremos2;
    }

    private S initialIdealSolution(List<S> baseSolutionList, int size) {
        S ideal = (S) baseSolutionList.get(0).copy();
        for(int i = 0; i < size; i++) {
            ideal.setObjective(i, Double.POSITIVE_INFINITY);
        }
        return ideal;
    }

    @Override
    public Comparator<S> getComparator() {
        return comparator;
    }

    @Override
    public void computeDensityEstimator() {

    }
} // IdealArchive
