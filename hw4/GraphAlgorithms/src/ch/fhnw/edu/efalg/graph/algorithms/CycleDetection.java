package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the cycle detection algorithm. Detecting a closing edge of a cycle can simply be done by using
 * DFS and checking if we ever reach a node that was already visited. To display all edges of the cycle this class
 * keeps track of the order of the used vertices and edges, so that when a cycle is found, the program can simply go
 * back in the history and mark all edges that belong to the cycle.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Patric Steiner
 */
public final class CycleDetection<V extends Vertex, E extends Edge> extends AbstractAlgorithm<V, E> {

    private Set<V> visited;
    private List<V> startHistory;
    private List<E> edgeHistory;
    private Set<E> cycleEdges;
    private GraphAlgorithmData<V, E> graphAlgorithmData;

    /**
     * Constructor
     */
    public CycleDetection() {
        super("Cycle Detection", true);
    }

    @Override
    public String execute(GraphAlgorithmData<V, E> data) {
        graphAlgorithmData = data;
        visited = new HashSet<>();
        startHistory = new ArrayList<>();
        edgeHistory = new ArrayList<>();
        cycleEdges = new HashSet<>();
        V start = getStartNode(data);
        if (start == null) {
            return "Empty graph,\nnothing to do";
        }
        findCycles(start);
        List<E> edges = new ArrayList<>(cycleEdges);
        highlightEdges(data, edges);
        darkenOtherEdges(data, edges);
        return "Finished";
    }

    /**
     * A cycle consists of 3 or more vertices. Find cycles by doing a DFS. If we ever hit a node we already visited, there is a cycle.
     *
     * @param start the vertex to start the search with.
     */
    private void findCycles(V start) {
        visited.add(start);
        startHistory.add(start);
        for (E edge : graphAlgorithmData.getGraph().getOutgoingEdges(start)) {
            V dst = otherEndpoint(graphAlgorithmData, edge, start);
            if (startHistory.size() <= 1 || dst != startHistory.get(startHistory.size() - 2)) { // don't check the node we are coming from (cycle must have at least 3 nodes)
                edgeHistory.add(edge);
                if (visited.contains(dst)) { // there is a cycle!
                    if (!cycleEdges.contains(edge)) { // make sure we only detect it once (without this check, we would detect it from both sides)
                        int index = startHistory.size() - 1;
                        // go back in the history until we reach the beginning of the cycle and add all these edges to the cycleEdges
                        do {
                            cycleEdges.add(edgeHistory.get(index));
                        } while (index > 0 && startHistory.get(index--) != dst);
                    }
                } else {
                    findCycles(dst);
                }
                edgeHistory.remove(edgeHistory.size() - 1);
            }
        }
        startHistory.remove(startHistory.size() - 1);
    }

    @Override
    public boolean worksWith(Graph graph) {
        return true;
    }
}