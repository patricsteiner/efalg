package ch.fhnw.edu.efalg.graph.gui;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState.State;

/**
 * Observes the ProgramState.
 * 
 * @author Martin Schaub
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public interface ProgramStateListener<V extends Vertex, E extends Edge> {

	/**
	 * A new graph is set.
	 * 
	 * @param currentGraph new graph (already set)
	 * @param oldGraph old graph.
	 */
	void newGraphLoaded(Graph<V, E> currentGraph, Graph<V, E> oldGraph);

	/**
	 * The graph was changed. For more information the GraphListener Interface needs to be implemented.
	 */
	void graphChanged();
	
	/**
	 * Program state has changed.
	 * @param oldState old state
	 * @param newState new state
	 */
	void stateChanged(State oldState, State newState);
}
