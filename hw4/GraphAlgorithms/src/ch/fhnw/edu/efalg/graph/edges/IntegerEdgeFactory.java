package ch.fhnw.edu.efalg.graph.edges;

import ch.fhnw.edu.efalg.graph.Edge;

/**
 * Produces Integer Edges.
 * 
 * @author Martin Schaub
 */
public class IntegerEdgeFactory extends AbstractEdgeFactory<Edge> {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -7527489771199279060L;

	/**
	 * Next edges number.
	 */
	private int current = 0;

	/**
	 * Constructor
	 */
	public IntegerEdgeFactory() {
		super("Integer Edge");
	}

	/**
	 * Getter method for the current property.
	 * 
	 * @return the current property's value
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 * Setter method for the current property.
	 * 
	 * @param current the new current to set
	 */
	public void setCurrent(final int current) {
		this.current = current;
	}

	/**
	 * Create new IntegerEdge with sequentially increasing weight.
	 */
	@Override
	public Edge newEdge() {
		return new IntegerEdge(current++);
	}

	/**
	 * Create new IntegerEdge with set weight.
	 * @param weight
	 * @return new IntegerEdge
	 */
	public Edge newEdge(int weight) {
		return new IntegerEdge(weight);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.EdgeFactory#getEdgeClass()
	 */
	@Override
	public Class<IntegerEdge> getEdgeClass() {
		return IntegerEdge.class;
	}
}
