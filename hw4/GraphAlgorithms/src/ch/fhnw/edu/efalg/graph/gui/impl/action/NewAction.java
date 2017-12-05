package ch.fhnw.edu.efalg.graph.gui.impl.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.EdgeFactory;
import ch.fhnw.edu.efalg.graph.EdgePanelHelper;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState;

/**
 * Creates a new graph with the selected properties.
 * 
 * @author Martin Schaub
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public class NewAction<V extends Vertex, E extends Edge> extends AbstractAction {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -1453863401230928174L;

	/**
	 * Class of the edge factory for instantiation
	 */
	private final Class<?> edgeFactory;
	/**
	 * Class of the edge panel helper for instantiation
	 */
	private final Class<?> edgePanelHelper;
	/**
	 * Program state to change
	 */
	private final ProgramState<V, E> programState;
	/**
	 * Whether the new graph must be directed or not.
	 */
	private final boolean directed;

	/**
	 * Constructor
	 * 
	 * @param programState program state to change
	 * @param edgeFactory edge factory to set
	 * @param edgePanelHelper edge panel helper to set
	 * @param directed if the new graph should be directed or not.
	 */
	public NewAction(final ProgramState<V, E> programState, final Class<?> edgeFactory, final Class<?> edgePanelHelper, final boolean directed) {
		if (edgeFactory == null || programState == null) {
			throw new NullPointerException();
		}
		this.edgeFactory = edgeFactory;
		this.edgePanelHelper = edgePanelHelper;
		this.programState = programState;
		this.directed = directed;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(final ActionEvent event) {
		synchronized (programState) {
			try {
				EdgeFactory<E> factory = (EdgeFactory<E>) edgeFactory.newInstance();
				EdgePanelHelper<E> panelHelper = (EdgePanelHelper<E>) edgePanelHelper.newInstance();
				programState.getColorMapper().reset();
				programState.getLocationMapper().reset();
				programState.setEdgeFactory(factory);
				programState.setEdgePanelHelper(panelHelper);
				programState.setGraph(new Graph<V, E>(directed, factory.getEdgeClass(), programState
						.getVertexFactory().getVertexClass()));
				programState.setStartNode(null);
				programState.setEndNode(null);
			}
			catch (final InstantiationException e) {
				System.err.println("Error loading " + edgeFactory.getName());
			}
			catch (final IllegalAccessException e) {
				System.err.println("Error loading " + edgeFactory.getName());
			}
		}
	}
}
