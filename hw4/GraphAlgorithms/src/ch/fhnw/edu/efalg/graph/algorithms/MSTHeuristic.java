package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.IntegerEdge;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the MST Heuristic algorithm.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Patric Steiner
 */
public final class MSTHeuristic<V extends Vertex, E extends Edge> extends AbstractMSTHeuristicAlgorithm<V, E> {

    private GraphAlgorithmData<V, E> graphAlgorithmData;
    private Set<V> visited = new HashSet<>();
    private Set<E> used = new HashSet<>();

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
        new Kruskal<V, E>().execute(data);
        // Remove all edges that are not in the MST
        for (Object e : data.getGraph().getEdges().toArray()) { // toArray to avoid concurrent modification
            if (!data.getColorMapper().getEdgeColor((E) e).equals(Color.RED)) {
                data.getGraph().removeEdge((E) e);
            }
        }
        // duplicate all edges in opposite direction --> euler graph
        for (Object e : data.getGraph().getEdges().toArray()) {
            V v1 = data.getGraph().getEndpoints((E) e).get(0);
            V v2 = data.getGraph().getEndpoints((E) e).get(1);
            data.getGraph().addEdge(v2, v1, data.getEdgeFactory().newEdge());
        }
        List<E> edgesToDelete = new ArrayList<>();
        V skipStart = null;
        V v = data.getStartNode();
        // while not all nodes are visited
        while (visited.size() != data.getGraph().getNumOfVertices()) {
            visited.add(v);
            E e = nextEdge(v);
            V other = otherEndpoint(data, e, v);
            if (!visited.contains(other)) {
                if (skipStart != null) {
                    data.getGraph().addEdge(skipStart, other, data.getEdgeFactory().newEdge());
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

    private E nextEdge(V v) {
        for (E e : graphAlgorithmData.getGraph().getOutgoingEdges(v)) {
            V dst = graphAlgorithmData.getGraph().getEndpoints((E) e).get(1);
            if (!used.contains(graphAlgorithmData.getGraph().getEdge(dst, v))) // if the incoming edge from dst is not yet used: prefer this edge
                return e;
        }
        return graphAlgorithmData.getGraph().getOutgoingEdges(v).stream().findFirst().orElseThrow(
                () -> new IllegalStateException("Vertex must always have at least 1 outgoing edge")
        );
    }

}

