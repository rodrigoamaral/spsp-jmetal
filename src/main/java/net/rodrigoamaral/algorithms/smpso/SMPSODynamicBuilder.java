package net.rodrigoamaral.algorithms.smpso;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.List;

public class SMPSODynamicBuilder extends SMPSOBuilder {

    private List<DoubleSolution> initialPopulation;

    public SMPSODynamicBuilder(DoubleProblem problem, BoundedArchive<DoubleSolution> leaders) {
        super(problem, leaders);
    }

    public SMPSODynamicBuilder setInitialPopulation(List<DoubleSolution> initialPopulation) {
        this.initialPopulation = initialPopulation;
        return this;
    }

    public SMPSO build() {
        return new SMPSODynamic(getProblem(), getSwarmSize(), leaders, mutationOperator, getMaxIterations(), getR1Min(), getR1Max(),
                    getR2Min(), getR2Max(), getC1Min(), getC1Max(), getC2Min(), getC2Max(), getWeightMin(), getWeightMax(), getChangeVelocity1(),
                    getChangeVelocity2(), evaluator, initialPopulation);
    }

}
