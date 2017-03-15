package net.rodrigoamaral.spsp.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class TaskPrecedenceGraph {
    private static final int DFS_WHITE = -1;
    private static final int DFS_BLACK = 1;
    private static Vector<Vector<Integer>> successors;
    private static Vector<Integer> visited;
    private static Vector<Integer> sorted;
    private static ArrayList<Integer> vertexDegrees;
    private static ArrayList<ArrayList<Integer>> predecessors;

    public TaskPrecedenceGraph(int qtdTasks) {
        visited = new Vector<>();
        successors = new Vector<>();
        vertexDegrees = new ArrayList<>();
        predecessors = new ArrayList<>();
        for(int i = 0; i < qtdTasks; i++){
            successors.add(new Vector<>());
            predecessors.add(new ArrayList<>());
            vertexDegrees.add(0);
        }
    }

    public ArrayList<Integer> getTaskDependencies() {
        return vertexDegrees;
    }

    private void initDFS(int size) {
        visited = new Vector<>();
        visited.addAll(Collections.nCopies(size, DFS_WHITE));
    }

    private void runTopologicalSorting(int size) {
        initDFS(size);
        sorted = new Vector<>();
        for (int i = 0; i < size; i++)
            if (visited.get(i) == DFS_WHITE)
                visit(i);
    }

    private void visit(int u) {
        visited.set(u, DFS_BLACK);
        Iterator it = successors.get(u).iterator();
        while (it.hasNext()) {
            Integer v = (Integer) it.next();
            if (visited.get(v) == DFS_WHITE)
                visit(v);
        }
        sorted.add(u);
    }

    private Vector<Integer> getSorting(){
        runTopologicalSorting(successors.size());
        return sorted;
    }

    public void addEdge(int u, int v){
        successors.get(u).add(v);
        predecessors.get(v).add(u);
        incrementDegree(v);
    }

    private void incrementDegree(int v) {
        int degree = vertexDegrees.get(v);
        vertexDegrees.set(v, degree + 1);
    }

    public ArrayList<Integer> getTaskPredecessors(int task) {
        return predecessors.get(task);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TaskPrecedenceGraph{successors={");
        for (int i = 0; i < successors.size(); i++) {
            sb.append(i).append(":[");
            for (Integer v: successors.get(i)) {
                sb.append(v).append(" ");
            }
            sb.append("] ");
        }
        sb.append("}, ");
        sb.append("sorted={");
        this.getSorting();
        for(int i = sorted.size() - 1; i >= 0; i--) {
            sb.append(sorted.get(i)).append(", ");
        }
        sb.append("}}");
        return sb.toString();
    }
}
