package net.rodrigoamaral.dspsp.solution.mutation;


import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class DSPSPRepairMutation implements MutationOperator<DoubleSolution> {
    private static final double DEFAULT_PROBABILITY = 0.01 ;
    private static final double DEFAULT_DISTRIBUTION_INDEX = 20.0 ;
    private double distributionIndex ;
    private double mutationProbability ;
    private RepairDoubleSolution solutionRepair ;

    private JMetalRandom randomGenerator ;

    /** Constructor */
    public DSPSPRepairMutation() {
        this(DEFAULT_PROBABILITY, DEFAULT_DISTRIBUTION_INDEX) ;
    }

    /** Constructor */
    public DSPSPRepairMutation(DoubleProblem problem, double distributionIndex) {
        this(1.0/problem.getNumberOfVariables(), distributionIndex) ;
    }

    /** Constructor */
    public DSPSPRepairMutation(double mutationProbability, double distributionIndex) {
        this(mutationProbability, distributionIndex, new RepairDoubleSolutionAtBounds()) ;
    }

    /** Constructor */
    public DSPSPRepairMutation(double mutationProbability, double distributionIndex,
                               RepairDoubleSolution solutionRepair) {
        if (mutationProbability < 0) {
            throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
        } else if (distributionIndex < 0) {
            throw new JMetalException("Distribution index is negative: " + distributionIndex) ;
        }
        this.mutationProbability = mutationProbability;
        this.distributionIndex = distributionIndex;
        this.solutionRepair = solutionRepair ;

        randomGenerator = JMetalRandom.getInstance() ;
    }

    /* Getters */
    public double getMutationProbability() {
        return mutationProbability;
    }

    public double getDistributionIndex() {
        return distributionIndex;
    }

    /* Setters */
    public void setMutationProbability(double probability) {
        this.mutationProbability = probability ;
    }

    public void setDistributionIndex(double distributionIndex) {
        this.distributionIndex = distributionIndex ;
    }

    /** Execute() method */
    @Override
    public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
        if (null == solution) {
            throw new JMetalException("Null parameter") ;
        }

        doMutation(mutationProbability, solution);
        return solution;
    }

    /** Perform the mutation operation */
    private void doMutation(double probability, DoubleSolution solution) {
        double rnd, delta1, delta2, mutPow, deltaq;
        double y, yl, yu, val, xy;

        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            if (randomGenerator.nextDouble() <= probability) {
                y = solution.getVariableValue(i);

                // Only mutates if value is not zero
                if (y != 0.0) {
                    yl = solution.getLowerBound(i);
                    yu = solution.getUpperBound(i);
                    if (yl == yu) {
                        y = yl;
                    } else {
                        delta1 = (y - yl) / (yu - yl);
                        delta2 = (yu - y) / (yu - yl);
                        rnd = randomGenerator.nextDouble();
                        mutPow = 1.0 / (distributionIndex + 1.0);
                        if (rnd <= 0.5) {
                            xy = 1.0 - delta1;
                            val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, distributionIndex + 1.0));
                            deltaq = Math.pow(val, mutPow) - 1.0;
                        } else {
                            xy = 1.0 - delta2;
                            val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, distributionIndex + 1.0));
                            deltaq = 1.0 - Math.pow(val, mutPow);
                        }
                        y = y + deltaq * (yu - yl);
                        y = solutionRepair.repairSolutionVariableValue(y, yl, yu);
                    }
                }
                solution.setVariableValue(i, y);
            }
        }
    }
}

