package net.rodrigoamaral.spsp.meapr;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

public class MultiSwarmMEAPRBuilder implements AlgorithmBuilder {

    private DoubleProblem problem;

    private double c1Max;
    private double c1Min;
    private double c2Max;
    private double c2Min;
    private double r1Max;
    private double r1Min;
    private double r2Max;
    private double r2Min;
    private double weightMax;
    private double weightMin;
    private double changeVelocity1;
    private double changeVelocity2;

    private int numberOfSwarms;
    private int swarmSize;
    private int maxIterations;
    private int swapInterval;

    protected int archiveSize;
    private ArchiveType firstArchiveType;
    private ArchiveType secondArchiveType;

    protected MutationOperator<DoubleSolution> mutationOperator;

    protected SolutionListEvaluator<DoubleSolution> evaluator;

    TopologyType topology;


    public MultiSwarmMEAPRBuilder(DoubleProblem problem) {
        setDefaultParams(problem);
    }

    private void setDefaultParams(DoubleProblem problem) {
        this.problem = problem;
        this.mutationOperator = defaultMutationOperator();
        this.evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;
        this.topology = TopologyType.BROADCAST;
        this.numberOfSwarms = 10; // this.numberOfSwarms = 10;
        this.swarmSize = 80;     // this.swarmSize = 160;
        this.maxIterations = 50; // this.maxIterations = 50;
        this.swapInterval = 10;  // this.swapInterval = 10;
        this.archiveSize = 100;  // this.archiveSize = 100;
//        this.firstArchiveType = ArchiveType.CrowdingDistanceArchive;
//        this.secondArchiveType = ArchiveType.HypervolumeArchive;
        this.firstArchiveType = ArchiveType.CrowdingDistanceArchive;
        this.secondArchiveType = ArchiveType.IdealArchive;

        this.r1Max = 1.0;
        this.r1Min = 0.0;
        this.r2Max = 1.0;
        this.r2Min = 0.0;
        this.c1Max = 2.5;
        this.c1Min = 1.5;
        this.c2Max = 2.5;
        this.c2Min = 1.5;
        this.weightMax = 0.1;
        this.weightMin = 0.1;
        this.changeVelocity1 = -1;
        this.changeVelocity2 = -1;
    }

    private MutationOperator<DoubleSolution> defaultMutationOperator() {
        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;
        return new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;
    }

    public MultiSwarmMEAPR build() {
        return new MultiSwarmMEAPR(problem, swarmSize, numberOfSwarms, mutationOperator, maxIterations,
                swapInterval, topology, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max,
                weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator, archiveSize,
                firstArchiveType, secondArchiveType);
    }

    /* Getters */

    /* Setters */

    public MultiSwarmMEAPRBuilder setRandomGenerator(PseudoRandomGenerator randomGenerator) {
        JMetalRandom.getInstance().setRandomGenerator(randomGenerator);
        return this;
    }

    public MultiSwarmMEAPRBuilder setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
        this.evaluator = evaluator ;
        return this;
    }

}



