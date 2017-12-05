package ch.fhnw.edu.efalg.graph.gui.impl.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState;

/**
 * Reset all colors of vertices and edges.
 * Set colors of selected start node and end node.
 * 
 * @author Michel Pluess
 *
 * @param <V>
 * @param <E>
 */
@SuppressWarnings("serial")
public class ResetColorsAction<V extends Vertex, E extends Edge> extends AbstractAction {
	private final ProgramState<V, E> state;

	public ResetColorsAction(final ProgramState<V, E> state) {
		if (state == null) {
			throw new NullPointerException();
		}
		this.state = state;
	}
	
	@Override
	/**
	 * Reset colors. Set color of start node and end node.
	 */
	public void actionPerformed(ActionEvent e) {
		state.getColorMapper().resetKeepSelection();
		state.updateColor(state.getStartNode());
		state.updateColor(state.getEndNode());
	}

}
