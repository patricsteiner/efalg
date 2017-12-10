package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.CapacityFlowEdge;
import ch.fhnw.edu.efalg.graph.edges.CapacityFlowEdgeFactory;
import ch.fhnw.edu.efalg.graph.edges.CostCapacityFlowEdge;
import ch.fhnw.edu.efalg.graph.edges.CostCapacityFlowEdgeFactory;

import java.util.*;

/**
 * Implementation of the Ford-Fulkerson Max-Flow Algorithm.
 *
 * @author Patric Steiner
 */
public class FordFulkerson<V extends Vertex, E extends CapacityFlowEdge> extends AbstractMaxFlowAlgorithm<V, E> {

    private GraphAlgorithmData<V, E> graphAlgorithmData;
    private List<E> path;
    private Set<V> visited;
    private Map<E, E> forwardEdges = new HashMap<>();
    private Map<E, E> backwardEdges = new HashMap<>();

    /**
     * Constructor
     */
    public FordFulkerson() {
        super("Ford-Fulkerson Max-Flow");
    }

    /**
     * Calculates the maximum flow.
     *
     * @param data   algorithm data
     * @param source source vertex
     * @param sink   sink vertex
     */
    protected void calculateMaxFlow(final GraphAlgorithmData<V, E> data, final V source, final V sink) {
        graphAlgorithmData = data;
        createBackwardEdges(data);
        path = new ArrayList<>();
        visited = new HashSet<>();
        // find all possible paths through the flow graph
        while (findPath(source, sink)) {
            path.forEach(p -> System.out.print(p.getLabel() + " "));
            System.out.println();
            int maxFlow = path.stream()
                    .mapToInt(e -> e.getCapacity() - e.getFlow()) // remaining capacity
                    .min()
                    .orElseThrow(() -> new IllegalStateException("Path is empty"));
            for (E edge : path) { // adjust the flow of all edges (and corresponding forward/backward edges)
                edge.setFlow(edge.getFlow() + maxFlow);
                E otherEdge = backwardEdges.get(edge);
                if (forwardEdges.containsKey(edge)) otherEdge = forwardEdges.get(edge);
                otherEdge.setFlow(otherEdge.getFlow() - maxFlow);
            }
            path = new ArrayList<>();
            visited = new HashSet<>();
        }
        // remove all edges with 0 flow at the end (just so it looks prettier)
        for (Object e : data.getGraph().getEdges().toArray()) { // toArray to avoid concurrent modification
            if (((E) e).getFlow() == 0) data.getGraph().removeEdge((E) e);
        }
    }

    /**
     * Finds a path from src to sink.
     *
     * @param src  node to start the search from
     * @param sink target node
     * @return true if there is a path with at least flow 1, false otherwise.
     */
    private boolean findPath(V src, V sink) {
        visited.add(src);
        for (E edge : graphAlgorithmData.getGraph().getOutgoingEdges(src)) {
            V other = otherEndpoint(graphAlgorithmData, edge, src);
            int remainingCapacity = edge.getCapacity() - edge.getFlow();
            if (remainingCapacity > 0 && !visited.contains(other)) { // make sure we only use edges with remaining capacity
                path.add(edge);
                if (other == sink || findPath(other, sink)) return true;
            }
        }
        return false;
    }

    /**
     * Creates backwards edges for all existing edges with same capacity, full flow and cost = -cost
     *
     * @param data
     */
    private void createBackwardEdges(GraphAlgorithmData<V, E> data) {
        for (Object e : data.getGraph().getEdges().toArray()) { // toArray to avoid concurrent modification
            V v1 = data.getGraph().getEndpoints((E) e).get(0);
            V v2 = data.getGraph().getEndpoints((E) e).get(1);
            E newEdge = null;
            if (data.getEdgeFactory() instanceof CapacityFlowEdgeFactory) // need to to ugly class casts because the factories have not same hierarchy as edges
                newEdge = (E) ((CapacityFlowEdgeFactory) data.getEdgeFactory())
                        .newEdge(((E) e).getCapacity());
            if (data.getEdgeFactory() instanceof CostCapacityFlowEdgeFactory)
                newEdge = (E) ((CostCapacityFlowEdgeFactory) data.getEdgeFactory())
                        .newEdgeWithCapacityAndCost(((CostCapacityFlowEdge) e).getCapacity(), -((CostCapacityFlowEdge) e).getCost());
            newEdge.setFlow(newEdge.getCapacity());
            data.getGraph().addEdge(v2, v1, newEdge);
            forwardEdges.put(newEdge, (E) e);
            backwardEdges.put((E) e, newEdge);
        }
    }
}
