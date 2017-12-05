package ch.fhnw.edu.efalg.graph.edges;

import java.util.Random;

import ch.fhnw.edu.efalg.graph.Edge;

/**
 * Produces new edges for mincost maxflow algorithm. Random numbers are used.
 * 
 * @author Martin Schaub
 */
public final class CostCapacityFlowEdgeFactory extends AbstractEdgeFactory<CostCapacityFlowEdge> {

	private static final int MAX_RANDOM_COST = 40;

	private static final int MAX_RANDOM_CAPACITY = 20;

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -4504291936686734074L;

	/**
	 * Random number generator for capacity and cost.
	 */
	private final Random rand = new Random();

	/**
	 * Constructor
	 */
	public CostCapacityFlowEdgeFactory() {
		super("Mincost Maxflow Edge");
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.EdgeFactory#getEdgeClass()
	 */
	@Override
	public Class<? extends Edge> getEdgeClass() {
		return CostCapacityFlowEdge.class;
	}

	/**
	 * Create edge with random cost and capacity.
	 * @return edge
	 */
	@Override
	public CostCapacityFlowEdge newEdge() {
		return new CostCapacityFlowEdge(rand.nextInt(MAX_RANDOM_CAPACITY) + 1, rand.nextInt(MAX_RANDOM_COST) + 1);
	}
	
	/**
	 * Create edge with specified capacity and random cost.
	 * @param capacity
	 * @return edge
	 */
	public CostCapacityFlowEdge newEdgeWithCapacity(int capacity) {
		return new CostCapacityFlowEdge(capacity, rand.nextInt(MAX_RANDOM_COST));
	}
	
	/**
	 * Create edge with specified cost and random capacity.
	 * @param cost
	 * @return edge
	 */
	public CostCapacityFlowEdge newEdgeWithCost(int cost) {
		return new CostCapacityFlowEdge(rand.nextInt(MAX_RANDOM_CAPACITY), cost);
	}
	
	/**
	 * Create edge with specified cost and capacity.
	 * @param capacity
	 * @param cost
	 * @return edge
	 */
	public CostCapacityFlowEdge newEdgeWithCapacityAndCost(int capacity, int cost) {
		return new CostCapacityFlowEdge(capacity, cost);
	}
}
