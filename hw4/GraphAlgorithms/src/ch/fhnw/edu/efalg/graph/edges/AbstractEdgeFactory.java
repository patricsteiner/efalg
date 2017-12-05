package ch.fhnw.edu.efalg.graph.edges;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.EdgeFactory;

/**
 * Abstract base class for the EdgeFactory implementation hierarchy.
 * 
 * @author Martin Schaub
 * 
 * @param <E> edge type to produce
 */
public abstract class AbstractEdgeFactory<E extends Edge> implements EdgeFactory<E> {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -8979689800462451986L;

	/**
	 * Classification name.
	 */
	private final String name;

	/**
	 * Constructor
	 * 
	 * @param name name of the produced edges.
	 */
	public AbstractEdgeFactory(final String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.edges.EdgeFactory#getName()
	 */
	@Override
	public final String getName() {
		return name;
	}
}
