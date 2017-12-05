package ch.fhnw.edu.efalg.graph.edges;

import java.util.Random;

import ch.fhnw.edu.efalg.graph.Edge;

/**
 * Produces new edges with a random flow.
 * 
 * @author Martin Schaub
 */
public final class CapacityFlowEdgeFactory extends AbstractEdgeFactory<CapacityFlowEdge> {

	private static final int MAX_RANDOM_CAPACITY = 20;

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 5700182492806614697L;

	/**
	 * For creating new edges.
	 */
	private final Random rand = new Random();

	/**
	 * Constructor
	 */
	public CapacityFlowEdgeFactory() {
		super("Max Flow Edge");
	}

	/**
	 * Create edge with random capacity.
	 * @return edge
	 */
	@Override
	public CapacityFlowEdge newEdge() {
		return new CapacityFlowEdge(rand.nextInt(MAX_RANDOM_CAPACITY) + 1);
	}
	
	/**
	 * Create edge with specified capacity.
	 * @param capacity
	 * @return edge
	 */
	public CapacityFlowEdge newEdge(int capacity) {
		return new CapacityFlowEdge(capacity);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.EdgeFactory#getEdgeClass()
	 */
	@Override
	public Class<? extends Edge> getEdgeClass() {
		return CapacityFlowEdge.class;
	}

}
