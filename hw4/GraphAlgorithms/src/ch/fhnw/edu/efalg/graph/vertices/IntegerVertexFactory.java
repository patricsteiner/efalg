package ch.fhnw.edu.efalg.graph.vertices;

import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.VertexFactory;

/**
 * Factory which produces Integer Vertices.
 * 
 * @author Martin Schaub
 */
public final class IntegerVertexFactory implements VertexFactory<Vertex> {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -2682543100596368196L;

	/**
	 * Next edges number.
	 */
	private int current = 0;

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

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.VertexFactory#newVertex()
	 */
	@Override
	public IntegerVertex newVertex() {
		return new IntegerVertex(current++);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.VertexFactory#reset()
	 */
	@Override
	public void reset() {
		current = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.VertexFactory#getVertexClass()
	 */
	@Override
	public Class<IntegerVertex> getVertexClass() {
		return IntegerVertex.class;
	}
}
