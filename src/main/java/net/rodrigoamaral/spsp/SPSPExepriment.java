package net.rodrigoamaral.spsp;


import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.util.ArrayList;
import java.util.List;

public class SPSPExepriment {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new JMetalException("Missing argument: experimentBaseDirectory") ;
        }
        String experimentBaseDirectory = args[0] ;
        List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    }
}
