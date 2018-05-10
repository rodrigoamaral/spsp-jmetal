package net.rodrigoamaral.algorithms;


import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;

public interface ISwarm {
    void mergeArchive(Archive<DoubleSolution> archive);
    Archive<DoubleSolution> getLeaders();
    void run();
}
