package net.rodrigoamaral.algorithms.smpso;

import net.rodrigoamaral.algorithms.ISwarm;
import net.rodrigoamaral.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the initial swarm from a solution list.
 *
 */
public class SMPSODynamic extends SMPSO implements ISwarm {

    private List<DoubleSolution> initialPopulation;

    public SMPSODynamic(DoubleProblem problem, int swarmSize, BoundedArchive<DoubleSolution> leaders, MutationOperator<DoubleSolution> mutationOperator, int maxIterations, double r1Min, double r1Max, double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max, double weightMin, double weightMax, double changeVelocity1, double changeVelocity2, SolutionListEvaluator<DoubleSolution> evaluator, List<DoubleSolution> initialPopulation) {
        super(problem, swarmSize, leaders, mutationOperator, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator);
        this.initialPopulation = initialPopulation;
    }

    /**
     * Allows to incorporate dynamic features to the PSO.
     *
     * @return
     */
    @Override
    protected List<DoubleSolution> createInitialSwarm() {

        assert initialPopulation.size() >= getSwarmSize();

        List<DoubleSolution> swarm = new ArrayList<>(getSwarmSize());

        DoubleSolution newSolution;
        for (int i = 0; i < getSwarmSize(); i++) {
            if (initialPopulation == null || initialPopulation.isEmpty()) {
                newSolution = getProblem().createSolution();
            } else {
                newSolution = new DefaultDoubleSolution((DefaultDoubleSolution) initialPopulation.get(i));
            }
            swarm.add(newSolution);
        }

        return swarm;
    }
}
