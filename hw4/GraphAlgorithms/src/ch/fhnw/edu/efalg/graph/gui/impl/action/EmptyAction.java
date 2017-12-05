package ch.fhnw.edu.efalg.graph.gui.impl.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState;

/**
 * Deletes the graph (make it empty).
 * 
 * @author Martin Schaub
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public class EmptyAction<V extends Vertex, E extends Edge> extends AbstractAction {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -5599812128300196578L;

	/**
	 * Program state to change
	 */
	private final ProgramState<V, E> programState;

	/**
	 * Constructor
	 * 
	 * @param programState program state
	 */
	public EmptyAction(final ProgramState<V, E> programState) {
		if (programState == null) {
			throw new NullPointerException();
		}
		this.programState = programState;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		Graph<V, E> graph = programState.getGraph();
		for (V v : new ArrayList<V>(graph.getVertices())) {
			graph.removeVertex(v);
		}
		programState.setStartNode(null);
		programState.setEndNode(null);
	}
}
