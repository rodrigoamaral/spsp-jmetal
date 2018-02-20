package net.rodrigoamaral.dspsp.objectives;

import java.util.Map;

/**
 *
 * Encapsulates a collection of all objectives ({@link IObjective})
 * for a given SPSP formulation.
 *
 * @author Rodrigo Amaral
 *
 */
public class DSPSPObjectiveEvaluator implements IObjectiveEvaluator {

    private Map<Integer, IObjective> objectives;

    public IObjective get(Integer key) {
        return objectives.get(key);
    }

}
