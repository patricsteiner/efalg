package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.CostCapacityFlowEdge;

/**
 * Implementation of the Min-Cost Max-Flow Algorithm.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Patric Steiner
 */
public class MinCostMaxFlow<V extends Vertex, E extends CostCapacityFlowEdge> extends AbstractMaxFlowAlgorithm<V, E> {

    /**
     * Constructor
     */
    public MinCostMaxFlow() {
        super("Min-Cost Max-Flow");
    }

    /**
     * Calculates the maximum flow with minimum costs.
     *
     * @param data   algorithm data
     * @param source source vertex
     * @param sink   sink vertex
     */
    protected void calculateMaxFlow(final GraphAlgorithmData<V, E> data, final V source, final V sink) {
        new FordFulkerson<V, E>().execute(data);
        CycleDetection<V, E> cycleDetection = new CycleDetection<>();
        cycleDetection.execute(data);
        cycleDetection.getCycles().stream()
                .filter(cycle -> cycle.stream()
                        .mapToInt(e -> e.getCost() * e.getFlow()).sum() < 0) // only take the cycles with negative total cost
                .forEach(negativeCycle -> {
                    do {
                        negativeCycle.forEach(e -> e.setFlow(e.getFlow() - 1)); // decrease flow in this cycle by 1
                    } while (negativeCycle.stream().mapToInt(e -> e.getCost() * e.getFlow()).sum() < 0); // while cycle is negative
                });
    }

    @Override
    public boolean worksWith(Graph<V, E> graph) {
        return graph.isDirected() && CostCapacityFlowEdge.class.isAssignableFrom(graph.edgeClass());
    }
}
