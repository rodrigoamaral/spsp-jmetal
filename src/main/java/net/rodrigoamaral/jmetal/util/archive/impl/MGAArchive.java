package net.rodrigoamaral.jmetal.util.archive.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Implements a Multi-level Grid Archiver
 * <p>
 * M. Laumanns and R. Zenklusen, “Stochastic convergence of random search methods
 * to fixed size pareto front approximations,” European Journal of Operational
 * Research, vol. 213, no. 2, pp. 414 – 421, 2011.
 *
 * @param <S>
 */

public class MGAArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {

    public static int DOMINATED_BY = -1;
    public static int DOMINATES = 1;
    public static int NON_DOMINATED = 0;
    public static int EQUALS = 2;

    private int objectives_;
    private Comparator comparator;


    public MGAArchive(int maxSize, int numberOfObjectives) {
        super(maxSize);
        objectives_ = numberOfObjectives;
        comparator = new DominanceComparator();
    }

    //    TODO: Fix MGAArchive.prune()
    @Override
    public void prune() {
        if (getSolutionList().size() > getMaxSize()) {
            List<S> solutions = getSolutionList();
            Collections.shuffle(solutions);
            int b = compute_b_mga(solutions);
            int index_removed = -1;
            while (index_removed == -1) {
                for (int i = solutions.size() - 1; i >= 0; i--) {
                    Solution solution_i = solutions.get(i);
                    double box_i[] = box_mga(solution_i, b);
                    for (int j = solutions.size() - 1; j >= 0; j--) {
                        if (i != j) {
                            Solution solution_j = solutions.get(j);
                            double box_j[] = box_mga(solution_j, b);
                            int comparation = compareObjectiveVector(box_i, box_j);
                            if (comparation == DOMINATED_BY || comparation == EQUALS) {
                                index_removed = i;
                                break;
                            }
                        }
                    }
                    if (index_removed != -1) {
                        //System.out.println(b);
                        break;
                    }
                }
                b--;
            }
            solutions.remove(index_removed);
        }
    }

    // TODO: compute_b_mga() calculates the initial "size" of the box?
    private int compute_b_mga(List<S> solutions) {
        double max_value = 0;
        for (int i = 0; i < solutions.size(); i++) {
            Solution s = solutions.get(i);
            for (int j = 0; j < objectives_; j++) {
                if (Math.abs(s.getObjective(j)) > max_value) {
                    max_value = Math.abs(s.getObjective(j));
                }
            }
        }
        return (int) Math.floor(log2(max_value)) + 1;
    }

    private double[] box_mga(Solution solution, int b) {
        double[] box = new double[objectives_];
        for (int i = 0; i < objectives_; i++) {
            double z = Math.abs(solution.getObjective(i));
            box[i] = (int) Math.floor(z / Math.pow(2.0, b) + 0.5);
        }
        return box;
    }

    private int compareObjectiveVector(double[] sol1, double[] sol2) {
        int cont = 0;
        int cont2 = sol1.length;
        for (int i = 0; i < sol1.length; i++) {

            double sol1_i = sol1[i];
            double sol2_i = sol2[i];

            if (sol1_i * -1 > sol2_i * -1) {
                ++cont;
            } else {
                if (sol1_i == sol2_i) {
                    --cont2;
                }
            }
        }
        if (cont == 0) {
            if (cont2 == 0)
                return EQUALS;
            else
                return DOMINATED_BY;
        } else {
            if (cont > 0 && cont < cont2)
                return NON_DOMINATED;
            else return DOMINATES;
        }
    }

    public static double log2(double num) {
        return Math.log10(num) / Math.log10(2);
    }

    @Override
    public Comparator<S> getComparator() {
        return comparator;
    }

    @Override
    public void computeDensityEstimator() {

    }
}
