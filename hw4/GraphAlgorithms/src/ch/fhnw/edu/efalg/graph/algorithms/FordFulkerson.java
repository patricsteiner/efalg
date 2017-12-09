package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.CapacityFlowEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Ford-Fulkerson Max-Flow Algorithm.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Patric Steiner
 */
public class FordFulkerson<V extends Vertex, E extends Edge> extends AbstractMaxFlowAlgorithm<V, E> {

    private GraphAlgorithmData<V, E> graphAlgorithmData;
    private List<Edge> path;

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
        path = new ArrayList<>();
        int flow;
        while ((flow = findPath(source, sink, 0)) > 0) { // find all possible paths through the flow graph
            for (Edge edge : path) edge.adjustFlow(flow);
            path = new ArrayList<>();
        }
    }

    /**
     * Finds a path from src to sink.
     * @param src node to start the search from
     * @param sink target node
     * @param flow current flow. At the first iteration it will be set to the maximum capacity of the chosen edge.
     * @return maximum flow through the found path
     */
    private int findPath(V src, V sink, int flow) {
        List<Edge> edges = new ArrayList<>(); // will contain all possible edges we can choose from src
        graphAlgorithmData.getGraph().getOutgoingEdges(src).forEach(e -> edges.add(new Edge((CapacityFlowEdge) e, false)));
        graphAlgorithmData.getGraph().getIncomingEdges(src).forEach(e -> edges.add(new Edge((CapacityFlowEdge) e, true)));
        for (Edge edge : edges) {
            int remainingCapacity = edge.remainingCapacity();
            if (remainingCapacity > 0 && !path.contains(edge)) { // make sure we only use edges with remaining capacity and edges that we have not used yet.
                if (path.size() == 0) flow = remainingCapacity; // in the first step of the path, we take the maximum possible capacity as flow
                else flow = Math.min(flow, remainingCapacity); // flow can never exceed capacity
                path.add(edge);
                V other = otherEndpoint(graphAlgorithmData, (E) edge.getInner(), src);
                if (other == sink) return flow; // found the sink!
                return findPath(other, sink, flow); // not arrived at the sink yet, recursively continue the DFS.
            }
        }
        return 0;
    }

    /**
     * Decorator for CapacityFlowEdge to provide additional functionality and state.
     */
    class Edge {
        private CapacityFlowEdge capacityFlowEdge;
        private boolean backwards;

        public Edge(CapacityFlowEdge capacityFlowEdge, boolean backwards) {
            this.capacityFlowEdge = capacityFlowEdge;
            this.backwards = backwards;
        }

        public int remainingCapacity() {
            if (backwards) return capacityFlowEdge.getFlow();
            return capacityFlowEdge.getCapacity() - capacityFlowEdge.getFlow();
        }

        /**
         * adjusts the flow of the edge. If it's a backwards edge, flow is reduced, otherwise flow is increased.
         * @param flow the value of adjustment
         */
        public void adjustFlow(int flow) {
            capacityFlowEdge.setFlow(capacityFlowEdge.getFlow() + flow * (backwards ? -1 : 1));
        }

        public CapacityFlowEdge getInner() {
            return capacityFlowEdge;
        }

        @Override
        public String toString() {
            return capacityFlowEdge.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            if (backwards != edge.backwards) return false;
            return capacityFlowEdge != null ? capacityFlowEdge.equals(edge.capacityFlowEdge) : edge.capacityFlowEdge == null;
        }

        @Override
        public int hashCode() {
            int result = capacityFlowEdge != null ? capacityFlowEdge.hashCode() : 0;
            result = 31 * result + (backwards ? 1 : 0);
            return result;
        }
    }

}
