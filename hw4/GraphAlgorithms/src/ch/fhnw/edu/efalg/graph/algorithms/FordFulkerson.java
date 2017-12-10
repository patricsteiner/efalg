package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.CapacityFlowEdge;
import ch.fhnw.edu.efalg.graph.edges.CapacityFlowEdgeFactory;
import ch.fhnw.edu.efalg.graph.edges.CostCapacityFlowEdge;
import ch.fhnw.edu.efalg.graph.edges.CostCapacityFlowEdgeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Ford-Fulkerson Max-Flow Algorithm.
 *
 * @author Patric Steiner
 */
public class FordFulkerson<V extends Vertex, E extends CapacityFlowEdge> extends AbstractMaxFlowAlgorithm<V, E> {

    private GraphAlgorithmData<V, E> graphAlgorithmData;
    private List<E> path;
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
        int flow;
        // find all possible paths through the flow graph
        while ((flow = findPath(source, sink, 0)) > 0) {
            for (E edge : path) { // adjust the flow of all edges (and corresponding forward/backward edges)
                edge.setFlow(edge.getFlow() + flow);
                E otherEdge = backwardEdges.get(edge);
                if (forwardEdges.containsKey(edge)) otherEdge = forwardEdges.get(edge);
                otherEdge.setFlow(otherEdge.getFlow() - flow);
            }
            path = new ArrayList<>();
        }
        // remove all edges with 0 flow at the end (just so it looks prettier)
        for (Object e : data.getGraph().getEdges().toArray()) { // toArray to avoid concurrent modification
            if (((E)e).getFlow() == 0) data.getGraph().removeEdge((E) e);
        }
    }

    /**
     * Finds a path from src to sink.
     *
     * @param src  node to start the search from
     * @param sink target node
     * @param flow current flow. At the first iteration it will be set to the maximum capacity of the chosen edge.
     * @return maximum flow through the found path
     */
    private int findPath(V src, V sink, int flow) {
        for (E edge : graphAlgorithmData.getGraph().getOutgoingEdges(src)) {
            int remainingCapacity = edge.getCapacity() - edge.getFlow();
            if (remainingCapacity > 0 && !path.contains(edge)) { // make sure we only use edges with remaining capacity and edges that we have not used yet.
                if (path.size() == 0)
                    flow = remainingCapacity; // in the first step of the path, we take the maximum possible capacity as flow
                else flow = Math.min(flow, remainingCapacity); // flow can never exceed capacity
                path.add(edge);
                V other = otherEndpoint(graphAlgorithmData, edge, src);
                if (other == sink) return flow; // found the sink!
                return findPath(other, sink, flow); // not arrived at the sink yet, recursively continue the DFS.
            }
        }
        return 0;
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
