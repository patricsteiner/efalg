package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.IntegerEdge;
import ch.fhnw.edu.efalg.graph.gui.impl.Node;

import java.util.*;

/**
 * Implementation of the Kruskal MST algorithm.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Patric Steiner
 */
public final class Kruskal<V extends Vertex, E extends Edge> extends AbstractAlgorithm<V, E> {

    /**
     * Constructor
     */
    public Kruskal() {
        super("Kruskal MST", true);
    }

    /**
     * This algorithm works with all Graph implementations. For Graphs with IntegerEdges, edges with low weight are preferred.
     * Otherwise, shortest euclidean distance is preferred.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean worksWith(final Graph<V, E> graph) {
        return true;
    }

    /**
     * Implementation of the Kruskal algorithm.
     *
     * {@inheritDoc}
     */
    @Override
    public String execute(final GraphAlgorithmData<V, E> data) {
        // Priority queue containing all edges in increasing order (cheapest edges are preferred)
        Queue<E> edges = new PriorityQueue<>((e1, e2) -> {
            if (e1 instanceof IntegerEdge && e2 instanceof IntegerEdge) {
                return ((IntegerEdge) e1).getWeight() - ((IntegerEdge) e2).getWeight();
            }
            return squaredLength(e1, data) - squaredLength(e2, data);
        });
        edges.addAll(data.getGraph().getEdges());
        List<E> usedEdges = new ArrayList<>();
        Map<V, Integer> trees = new HashMap<>(); // at first, every vertex is a tree on its own, represented by having a different Integer
        int tree = 0;
        for (V vertex : data.getGraph().getVertices()) {
            trees.put(vertex, tree++);
        }
        while (!edges.isEmpty() && !allValuesAreEqual(trees)) { // while there are still unused edges and unconnected trees
            E edge = edges.poll();
            int t1 = trees.get(data.getGraph().getEndpoints(edge).get(0));
            int t2 = trees.get(data.getGraph().getEndpoints(edge).get(1));
            if (t1 != t2) { // if trees of this edge are not connected
                trees.forEach((v, i) -> { if (i == t2) trees.put(v, t1); }); // connect trees
                usedEdges.add(edge);
            }
        }
        highlightEdges(data, usedEdges);
        darkenOtherEdges(data, usedEdges);
        return "Finished";
    }

    private boolean allValuesAreEqual(Map<V, Integer> map) {
        int i = -1;
        for (int tree : map.values()) {
            if (i < 0) i = tree;
            if (i != tree) return false;
        }
        return true;
    }

    /**
     * Calculates the squared length of the edge (squared so we don't need to sqrt and deal with float values).
     * Only serves as a comparison criteria between edges if edges have no weight.
     * @param edge
     * @param data
     * @return squared length of the edge in euclidean space.
     */
    private int squaredLength(E edge, GraphAlgorithmData<V, E> data) {
        List<V> connectedVertices = data.getGraph().getEndpoints(edge);
        V src = connectedVertices.get(0);
        V dst = connectedVertices.get(1);
        Node<V> srcNode = data.getLocationMapper().getLocation(src);
        Node<V> dstNode = data.getLocationMapper().getLocation(dst);
        int diffX = Math.abs(srcNode.getX() - dstNode.getX());
        int diffY = Math.abs(srcNode.getY() - dstNode.getY());
        return diffX * diffX + diffY * diffY;
    }

}

