package net.rodrigoamaral.spsp.meapr;

import net.rodrigoamaral.jmetal.util.archive.impl.IdealArchive;
import net.rodrigoamaral.jmetal.util.archive.impl.MGAArchive;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

public class MEAPRBuilder implements AlgorithmBuilder<MEAPR> {

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

    private int swarmSize;
    private int maxIterations;

    protected int archiveSize;

    protected MutationOperator<DoubleSolution> mutationOperator;

    protected BoundedArchive<DoubleSolution> leaders;

    protected SolutionListEvaluator<DoubleSolution> evaluator;


    public MEAPRBuilder(DoubleProblem problem, int swarmSize, int archiveSize, ArchiveType archiveType,
                        MutationOperator<DoubleSolution> mutationOperator,
                        SolutionListEvaluator<DoubleSolution> evaluator) {
        this.problem = problem;
        this.swarmSize = swarmSize;
        this.archiveSize = archiveSize;
        this.mutationOperator = mutationOperator;
        this.evaluator = evaluator;

        maxIterations = 250;
        r1Max = 1.0;
        r1Min = 0.0;
        r2Max = 1.0;
        r2Min = 0.0;
        c1Max = 2.5;
        c1Min = 1.5;
        c2Max = 2.5;
        c2Min = 1.5;
        weightMax = 0.1;
        weightMin = 0.1;
        changeVelocity1 = -1;
        changeVelocity2 = -1;


        switch (archiveType) {
            case CrowdingDistanceArchive:
                this.leaders = new CrowdingDistanceArchive<>(archiveSize);
                break;
            case MGAArchive:
                this.leaders = new MGAArchive(archiveSize, problem.getNumberOfObjectives());
                break;
            case IdealArchive:
                this.leaders = new IdealArchive<>(archiveSize, problem.getNumberOfObjectives());
                break;
            case SPEA2DensityArchive:
//                this.leaders = new SPEA2DensityArchive<>(archiveSize, problem.getNumberOfObjectives());
                break;
            case AdaptiveGridArchive:
                this.leaders = new AdaptiveGridArchive<>(archiveSize, 5, problem.getNumberOfObjectives());
                break;
            case HypervolumeArchive:
                this.leaders = new HypervolumeArchive<>(archiveSize, new PISAHypervolume<DoubleSolution>());
                break;
        }

    }

    public MEAPR build() {
        return new MEAPR(problem, swarmSize, leaders, mutationOperator, maxIterations, r1Min, r1Max,
                r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1,
                changeVelocity2, evaluator);
    }

    public MEAPRBuilder setRandomGenerator(PseudoRandomGenerator randomGenerator) {
        JMetalRandom.getInstance().setRandomGenerator(randomGenerator);

        return this;
    }

}



