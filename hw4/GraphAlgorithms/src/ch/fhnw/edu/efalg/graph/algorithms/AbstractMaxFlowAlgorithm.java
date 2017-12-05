package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.CapacityFlowEdge;

/**
 * Abstract base class for max flow implementations.
 * 
 * @author Martin Schaub
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public abstract class AbstractMaxFlowAlgorithm<V extends Vertex, E extends Edge> extends AbstractAlgorithm<V, E> {

	/**
	 * Constructor
	 * 
	 * @param name algorithm name
	 */
	public AbstractMaxFlowAlgorithm(final String name) {
		super(name, true);
	}

	/**
	 * Calculate the maximum flow from source to sink.
	 */
	@Override
	public String execute(final GraphAlgorithmData<V, E> data) {
		// Get source and sink node
		V source = getStartNode(data);
		if (source == null) {
			return "Graph is empty\nnothing to do";
		}
		else if (data.getGraph().getIncomingEdges(source).size() > 0) {
			return "Start node is not a source\nplease choose a correct start node";
		}

		V sink = getEndNode(data);
		if (sink == null || source == sink) {
			return "Source can't be = sink";
		}
		else if (data.getGraph().getOutgoingEdges(sink).size() > 0) {
			return "End node is not a sink\nplease choose a correct end node";
		}

		resetEdgeFlows(data);
		calculateMaxFlow(data, source, sink);

		return "Total Flow " + Integer.toString(calculateFlow(data, source));
	}

	/**
	 * Calculates the maximum flow.
	 * 
	 * YOUR IMPLEMENTATION OVERRIDES THIS METHOD
	 * 
	 * @param data algorithm data
	 * @param source source vertex
	 * @param sink sink vertex
	 */
	protected abstract void calculateMaxFlow(final GraphAlgorithmData<V, E> data, final V source, final V sink);

	/**
	 * Sets the flow of all edges to zero.
	 * 
	 * @param data algorithm data for obtaining the graph
	 */
	protected void resetEdgeFlows(final GraphAlgorithmData<V, E> data) {
		for (E e : data.getGraph().getEdges()) {
			((CapacityFlowEdge) e).setFlow(0);
		}
	}

	/**
	 * Calculates the flow of the network (all outgoing nodes)
	 * 
	 * @param data algorithm data for obtaining the graph
	 * @param source source vertex
	 * @return maximal flow
	 */
	protected int calculateFlow(final GraphAlgorithmData<V, E> data, final V source) {
		int flow = 0;
		for (Edge e : data.getGraph().getOutgoingEdges(source)) {
			flow += ((CapacityFlowEdge) e).getFlow();
		}
		return flow;
	}

	/**
	 * This algorithm works only with edges with flow and capacity.
	 */
	@Override
	public boolean worksWith(final Graph<V, E> graph) {
		return graph.isDirected() && CapacityFlowEdge.class.isAssignableFrom(graph.edgeClass());
	}
}
