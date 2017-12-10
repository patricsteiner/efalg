package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.IntegerEdge;
import ch.fhnw.edu.efalg.graph.edges.IntegerEdgeFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Implementation of the MST Heuristic algorithm.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Patric Steiner
 */
public final class MSTHeuristic<V extends Vertex, E extends Edge> extends AbstractMSTHeuristicAlgorithm<V, E> {

    private GraphAlgorithmData<V, E> graphAlgorithmData;
    private Set<V> visited;
    private Set<E> used;

    /**
     * Constructor
     */
    public MSTHeuristic() {
        super("MST Heuristic");
    }


    /**
     * Calculates the path of the travelling salesman according to
     * the MST heuristic and shows this path. Removes other edges.
     *
     * @param data
     * @param start
     * @return sum of weights of the calculated path --> path length
     */
    @Override
    int calculateAndShowPath(GraphAlgorithmData<V, E> data, V start) {
        graphAlgorithmData = data;
        visited = new HashSet<>();
        used = new HashSet<>();
        new Kruskal<V, E>().execute(data);
        // Remove all edges that are not in the MST & duplicate all edges in opposite direction --> euler graph
        for (Object e : data.getGraph().getEdges().toArray()) { // toArray to avoid concurrent modification
            if (!data.getColorMapper().getEdgeColor((E) e).equals(Color.RED)) { // if it is not in the MST
                data.getGraph().removeEdge((E) e);
            } else {
                V v1 = data.getGraph().getEndpoints((E) e).get(0);
                V v2 = data.getGraph().getEndpoints((E) e).get(1);
                data.getGraph().addEdge(v2, v1, (E) ((IntegerEdgeFactory)data.getEdgeFactory()).newEdge(((IntegerEdge) e).getWeight())); // add edge in opposite direction
            }
        }
        List<E> edgesToDelete = new ArrayList<>();
        V skipStart = null;
        V v = data.getStartNode();
        // while not all nodes are visited
        while (visited.size() < data.getGraph().getNumOfVertices()) {
            visited.add(v);
            E e = nextEdge(v);
            used.add(e);
            V other = otherEndpoint(data, e, v);
            if (!visited.contains(other)) {
                if (skipStart != null) {
                    data.getGraph().addEdge(skipStart, other, (E) ((IntegerEdgeFactory)data.getEdgeFactory()).newEdge(((IntegerEdge) e).getWeight()));
                    edgesToDelete.forEach(edge -> data.getGraph().removeEdge(edge));
                    skipStart = null;
                }
            }
            else {
                if (skipStart == null) {
                    skipStart = other;
                }
                edgesToDelete.add(e);
            }
            v = other;
        }
        if (skipStart != null) {
            data.getGraph().addEdge(skipStart, start, data.getEdgeFactory().newEdge());
            graphAlgorithmData.getGraph().getEdges().forEach(e -> {
                if (!used.contains(e)) graphAlgorithmData.getGraph().removeEdge(e);
            });
        }
        return graphAlgorithmData.getGraph().getEdges().stream().mapToInt(e -> {
            if (e instanceof IntegerEdge) return ((IntegerEdge) e).getWeight();
            else return 1;
        }).sum();
    }

    /**
     * Finds the best choice of what edge to take next. If there is an outgoing edge to a node which has a unused outgoing
     * edge to v, then this edge is a good choice.
     * @param v
     * @return Best edge to take.
     */
    private E nextEdge(V v) {
        for (E e : graphAlgorithmData.getGraph().getOutgoingEdges(v)) {
            V dst = graphAlgorithmData.getGraph().getEndpoints(e).get(1);
            if (!used.contains(graphAlgorithmData.getGraph().getEdge(dst, v))) // if the incoming edge from dst is not yet used: prefer this edge
                return e;
        }
        return graphAlgorithmData.getGraph().getOutgoingEdges(v).stream().findFirst().orElseThrow(
                () -> new IllegalStateException("Vertex must always have at least 1 outgoing edge")
        );
    }

}

