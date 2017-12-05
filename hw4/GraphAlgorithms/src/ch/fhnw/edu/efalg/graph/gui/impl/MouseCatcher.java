package ch.fhnw.edu.efalg.graph.gui.impl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState.State;

/**
 * Catches the mouse movements.
 * 
 * @author Martin Schaub
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public final class MouseCatcher<V extends Vertex, E extends Edge> implements MouseListener, MouseMotionListener {

	/**
	 * Saves references to some needed data structures.
	 */
	private final ProgramState<V, E> state;
	/**
	 * Used to access edge details textfields.
	 */
	private final GraphGUI<V, E> gui;
	/**
	 * Last selected vertex.
	 */
	private V lastVertex;
	/**
	 * State when last vertex was selected.
	 */
	private State lastState;

	/**
	 * Constructor
	 * 
	 * @param state needed references
	 */
	public MouseCatcher(final GraphGUI<V, E> gui) {
		if (gui == null || gui.getProgramState() == null) {
			throw new NullPointerException();
		}
		this.gui = gui;
		this.state = gui.getProgramState();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		V currentVertex = state.getLocationMapper().getAtPosition(e.getX(), e.getY());
		if (state.getState().equals(State.MOVE_NODES)) {
			lastVertex = currentVertex;
		}
		else if (state.getState().equals(State.START_NODE_SELECTION) || state.getState().equals(State.END_NODE_SELECTION)) {
			if (state.getState().equals(State.START_NODE_SELECTION)) {
				if (currentVertex != null) {
					// Allow user to deselect current start node.
					if (currentVertex == state.getStartNode()) {
						state.setStartNode(null);
					}
					else {
						state.setStartNode(currentVertex);
					}
				}
			}
			else if (state.getState().equals(State.END_NODE_SELECTION)) {
				if (currentVertex != null) {
					// Allow user to deselect current end node.
					if (currentVertex == state.getEndNode()) {
						state.setEndNode(null);
					}
					else {
						state.setEndNode(currentVertex);
					}
				}
			}
			
			lastVertex = null;
		}
		else if (state.getState().equals(State.NODE_DELETE)) {
			if (currentVertex != null) {
				state.getGraph().removeVertex(currentVertex);
				
				// Set start / end node to null if necessary.
				if (state.getStartNode() == currentVertex) {
					state.setStartNode(null);
				}
				if (state.getEndNode() == currentVertex) {
					state.setEndNode(null);
				}
			}
			lastVertex = null;
		}
		else if (state.getState().equals(State.NODE_INSERT)) {
			V newV = state.getVertexFactory().newVertex();
			state.getLocationMapper().setLocation(newV, e.getX(), e.getY());
			state.getGraph().addVertex(newV);
			lastVertex = null;
		}
		else if (state.getState().equals(State.EDGE_INSERT)) {
			if (lastVertex == null) {
				if (currentVertex != null) {
					state.getColorMapper().setVertexBorderColor(currentVertex, ColorMapper.SELECTED_VERTEX_BORDER_COLOR);
					lastVertex = currentVertex;
				}
			}
			// Don't consider selections made in other modes.
			else if (!lastState.equals(State.EDGE_INSERT)) {
				state.getColorMapper().setVertexBorderColor(lastVertex, ColorMapper.DEFAULT_VERTEX_BORDER_COLOR);
				if (currentVertex != null) {
					state.getColorMapper().setVertexBorderColor(currentVertex, ColorMapper.SELECTED_VERTEX_BORDER_COLOR);
				}
				lastVertex = currentVertex;
			}
			else {
				if (currentVertex != null) {
					state.getColorMapper().setVertexBorderColor(lastVertex, ColorMapper.DEFAULT_VERTEX_BORDER_COLOR);
					if (!currentVertex.equals(lastVertex)) {
						// Insert new edge. Use data provided by the user if available.
						JPanel edgeDetailsPanel = (JPanel)gui.getOptionsPanel().getComponent(GraphGUI.EDGE_DETAILS_PANEL_INDEX);
						E edge = state.getEdgePanelHelper()
								.createEdgeFromPanel(edgeDetailsPanel,
										state.getEdgeFactory());
						state.getGraph().addEdge(lastVertex, currentVertex, edge);
					}
					lastVertex = null;
				}
			}
		}
		else if (state.getState().equals(State.EDGE_DELETE)) {
			if (lastVertex == null) {
				if (currentVertex != null) {
					state.getColorMapper().setVertexBorderColor(currentVertex, ColorMapper.SELECTED_VERTEX_BORDER_COLOR);
					lastVertex = currentVertex;
				}
			}
			// Don't consider selections made in other modes.
			else if (!lastState.equals(State.EDGE_DELETE)) {
				state.getColorMapper().setVertexBorderColor(lastVertex, ColorMapper.DEFAULT_VERTEX_BORDER_COLOR);
				if (currentVertex != null) {
					state.getColorMapper().setVertexBorderColor(currentVertex, ColorMapper.SELECTED_VERTEX_BORDER_COLOR);
				}
				lastVertex = currentVertex;
			}
			else {
				if (currentVertex != null) {
					state.getColorMapper().setVertexBorderColor(lastVertex, ColorMapper.DEFAULT_VERTEX_BORDER_COLOR);
					E edge = state.getGraph().getEdge(lastVertex, currentVertex);
					if (edge != null) {
						state.getGraph().removeEdge(edge);
					}
					lastVertex = null;
				}
			}
		}
		else {
			throw new InternalError();
		}
		
		lastState = state.getState();
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (state.getState().equals(State.MOVE_NODES)) {
			if (lastVertex != null) {
				state.getLocationMapper().setLocation(lastVertex, e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {}
	@Override
	public void mouseClicked(final MouseEvent e) {}
	@Override
	public void mouseEntered(final MouseEvent e) {}
	@Override
	public void mouseExited(final MouseEvent e) {}
	@Override
	public void mouseReleased(final MouseEvent e) {}
}
