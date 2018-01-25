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

    public int getNumberOfSwarms() {
        return numberOfSwarms;
    }

    public int getSwarmSize() {
        return swarmSize;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public double getR1Max() {
        return r1Max;
    }

    public double getR1Min() {
        return r1Min;
    }

    public double getR2Max() {
        return r2Max;
    }

    public double getR2Min() {
        return r2Min;
    }

    public double getC1Max() {
        return c1Max;
    }

    public double getC1Min() {
        return c1Min;
    }

    public double getC2Max() {
        return c2Max;
    }

    public double getC2Min() {
        return c2Min;
    }

    public MutationOperator<DoubleSolution> getMutation() {
        return mutationOperator;
    }

    public double getWeightMax() {
        return weightMax;
    }

    public double getWeightMin() {
        return weightMin;
    }

    public double getChangeVelocity1() {
        return changeVelocity1;
    }

    public double getChangeVelocity2() {
        return changeVelocity2;
    }

    public DoubleProblem getProblem() {
        return problem;
    }

    public int getArchiveSize() {
        return archiveSize;
    }

    public MutationOperator<DoubleSolution> getMutationOperator() {
        return mutationOperator;
    }

    public SolutionListEvaluator<DoubleSolution> getEvaluator() {
        return evaluator;
    }

    public TopologyType getTopology() {
        return topology;
    }

    public int getSwapInterval() {
        return swapInterval;
    }

    /* Setters */

    public MultiSwarmMEAPRBuilder setNumberOfSwarms(int numberOfSwarms) {
        this.numberOfSwarms = numberOfSwarms;
        return this;
    }

    public MultiSwarmMEAPRBuilder setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;
        return this;
    }

    public MultiSwarmMEAPRBuilder setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    public MultiSwarmMEAPRBuilder setMutation(MutationOperator<DoubleSolution> mutation) {
        mutationOperator = mutation;
        return this;
    }

    public MultiSwarmMEAPRBuilder setSwapInterval(int swapInterval) {
        this.swapInterval = swapInterval;
        return this;
    }

    public MultiSwarmMEAPRBuilder setC1Max(double c1Max) {
        this.c1Max = c1Max;
        return this;
    }

    public MultiSwarmMEAPRBuilder setC1Min(double c1Min) {
        this.c1Min = c1Min;
        return this;
    }

    public MultiSwarmMEAPRBuilder setC2Max(double c2Max) {
        this.c2Max = c2Max;
        return this;
    }

    public MultiSwarmMEAPRBuilder setC2Min(double c2Min) {
        this.c2Min = c2Min;
        return this;
    }

    public MultiSwarmMEAPRBuilder setR1Max(double r1Max) {
        this.r1Max = r1Max;
        return this;
    }

    public MultiSwarmMEAPRBuilder setR1Min(double r1Min) {
        this.r1Min = r1Min;
        return this;
    }

    public MultiSwarmMEAPRBuilder setR2Max(double r2Max) {
        this.r2Max = r2Max;
        return this;
    }

    public MultiSwarmMEAPRBuilder setR2Min(double r2Min) {
        this.r2Min = r2Min;
        return this;
    }

    public MultiSwarmMEAPRBuilder setWeightMax(double weightMax) {
        this.weightMax = weightMax;
        return this;
    }

    public MultiSwarmMEAPRBuilder setWeightMin(double weightMin) {
        this.weightMin = weightMin;
        return this;
    }

    public MultiSwarmMEAPRBuilder setChangeVelocity1(double changeVelocity1) {
        this.changeVelocity1 = changeVelocity1;
        return this;
    }

    public MultiSwarmMEAPRBuilder setChangeVelocity2(double changeVelocity2) {
        this.changeVelocity2 = changeVelocity2;
        return this;
    }

    public MultiSwarmMEAPRBuilder setRandomGenerator(PseudoRandomGenerator randomGenerator) {
        JMetalRandom.getInstance().setRandomGenerator(randomGenerator);
        return this;
    }

    public MultiSwarmMEAPRBuilder setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
        this.evaluator = evaluator ;
        return this;
    }

    public MultiSwarmMEAPRBuilder setTopology(TopologyType topology) {
        this.topology = topology;
        return this;
    }

    public MultiSwarmMEAPRBuilder setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;
        return this;
    }
}



