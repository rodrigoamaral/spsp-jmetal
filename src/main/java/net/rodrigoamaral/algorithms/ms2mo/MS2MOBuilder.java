package net.rodrigoamaral.algorithms.ms2mo;

import net.rodrigoamaral.algorithms.ISwarm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.ArrayList;
import java.util.List;

public class MS2MOBuilder implements AlgorithmBuilder {

    private DoubleProblem problem;
    private int maxIterations;
    private int swapInterval;
    protected int archiveSize;
    private ArchiveType firstArchiveType;
    private ArchiveType secondArchiveType;
    protected MutationOperator<DoubleSolution> mutationOperator;
    protected SolutionListEvaluator<DoubleSolution> evaluator;
    TopologyType topology;
    private List<ISwarm> swarms;

    public MS2MOBuilder(DoubleProblem problem) {
        setDefaultParams(problem);
    }

    private void setDefaultParams(DoubleProblem problem) {
        swarms = new ArrayList<>();
        this.problem = problem;
        this.mutationOperator = defaultMutationOperator();
        this.evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;
        this.topology = TopologyType.BROADCAST;
        this.maxIterations = 50;
        this.swapInterval = 10;
        this.archiveSize = 100;
        this.firstArchiveType = ArchiveType.CrowdingDistanceArchive;
        this.secondArchiveType = ArchiveType.IdealArchive;
    }

    private MutationOperator<DoubleSolution> defaultMutationOperator() {
        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;
        return new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;
    }

    public MS2MO build() {
        if (swarms == null || swarms.size() == 0) {
            throw new RuntimeException("MS2MO must have at least one swarm.");
        }
        return new MS2MO(swarms, maxIterations, swapInterval, topology);
    }

    public MS2MOBuilder addSwarm(ISwarm swarm) {
        swarms.add(swarm);
        return this;
    }


    public MS2MOBuilder setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }
}



