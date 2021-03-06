package ch.fhnw.edu.efalg.graph.edges;

import ch.fhnw.edu.efalg.graph.Edge;

/**
 * Basic edge with nothing but a label.
 * Base class for edge implementations.
 * 
 * @author Martin Schaub
 */
public class StandardEdge implements Edge {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -4982052222915766226L;

	/**
	 * Label.
	 */
	private final String label;

	/**
	 * Constructor
	 * 
	 * @param label edges label
	 */
	public StandardEdge(final String label) {
		if (label == null) {
			throw new NullPointerException();
		}
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.edges.VisualEdge#getLabel()
	 */
	@Override
	public final String getLabel() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return label;
	}

}
