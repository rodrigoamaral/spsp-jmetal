package net.rodrigoamaral.dspsp.project;

import java.util.*;

public class DynamicTaskPrecedenceGraph {
    private static final int DFS_WHITE = -1;
    private static final int DFS_BLACK = 1;
    private Vector<Vector<Integer>> successors;
    private Vector<Integer> visited;
    private Vector<Integer> sorted;
    private ArrayList<Integer> vertexDegrees;
    private Vector<Vector<Integer>> predecessors;

    public DynamicTaskPrecedenceGraph(int qtdTasks) {
        visited = new Vector<>();
        successors = new Vector<>();
        vertexDegrees = new ArrayList<>();
        predecessors = new Vector<>();
        for(int i = 0; i < qtdTasks; i++){
            successors.add(new Vector<>());
            predecessors.add(new Vector<>());
            vertexDegrees.add(0);
        }
    }

    public DynamicTaskPrecedenceGraph(DynamicTaskPrecedenceGraph tpg) {
        visited = new Vector<>(tpg.visited);
        successors = new Vector<>();
        for (Vector<Integer> vs: tpg.successors) {
            successors.add(new Vector<>(vs));
        }
        vertexDegrees = new ArrayList<>(tpg.vertexDegrees);
        predecessors = new Vector<>();
        for (Vector<Integer> vs: tpg.predecessors) {
            predecessors.add(new Vector<>(vs));
        }
    }

    public DynamicTaskPrecedenceGraph copy() {
        return new DynamicTaskPrecedenceGraph(this);
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

    public Vector<Integer> getSorting(){
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

    public Vector<Integer> getTaskPredecessors(int task) {
        return predecessors.get(task);
    }

    public Vector<Integer> getTaskSuccessors(int task) {
        return successors.get(task);
    }

    public List<Integer> getIndependentTasks() {
        List<Integer> independentTasks = new ArrayList<>();
        for (int task = 0; task < predecessors.size(); task++) {
            if (getTaskPredecessors(task).isEmpty()) {
                independentTasks.add(task);
            }
        }
        return independentTasks;
    }

    public List<Integer> getDisconnectedTasks() {
        List<Integer> disconnectedTasks = new ArrayList<>();
        int size = vertexDegrees.size();
        for (int t = 0; t < size; t++) {
            if (getTaskPredecessors(t).isEmpty() && getTaskSuccessors(t).isEmpty()) {
                disconnectedTasks.add(t);
            }
        }
        return disconnectedTasks;
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

    public boolean isEmpty() {
        for (Vector<Integer> vs: predecessors) {
            if (!vs.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void remove(int index) {
        for (Vector<Integer> vs: predecessors) {
            if (vs.contains(index)) {
                vs.removeElement(index);
            }
        }
        successors.get(index).clear();
        predecessors.get(index).clear();
        vertexDegrees.set(index, 0);
    }
}
