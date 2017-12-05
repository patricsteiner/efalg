package ch.fhnw.edu.efalg.graph;

import java.io.Serializable;

/**
 * Represents a Vertex.
 * 
 * @author Martin Schaub
 */
public interface Vertex extends Serializable {

	/**
	 * Gets the label of the Vertex.
	 * 
	 * @return vertex's label.
	 */
	public String getLabel();
}
