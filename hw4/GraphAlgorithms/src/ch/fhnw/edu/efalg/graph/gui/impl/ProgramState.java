package ch.fhnw.edu.efalg.graph.gui.impl;

import java.awt.Color;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.EdgeFactory;
import ch.fhnw.edu.efalg.graph.EdgePanelHelper;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.GraphListener;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.VertexFactory;
import ch.fhnw.edu.efalg.graph.edges.StandardEdge;
import ch.fhnw.edu.efalg.graph.edges.StandardEdgeFactory;
import ch.fhnw.edu.efalg.graph.edges.StandardEdgePanelHelper;
import ch.fhnw.edu.efalg.graph.gui.ProgramStateListener;
import ch.fhnw.edu.efalg.graph.vertices.IntegerVertex;

/**
 * ProgramState implementation.
 *
 * @author Martin Schaub
 *
 * @param <V> vertex type
 * @param <E> edge type
 */
@SuppressWarnings("unchecked")
public final class ProgramState<V extends Vertex, E extends Edge> implements GraphListener<V, E> {

	/**
	 * Possible program states.
	 */
	public enum State {
		/**
		 * Move nodes around.
		 */
		MOVE_NODES,
		/**
		 * Select start node.
		 */
		START_NODE_SELECTION,
		/**
		 * Select end node.
		 */
		END_NODE_SELECTION,
		/**
		 * Insert new nodes.
		 */
		NODE_INSERT,
		/**
		 * Delete nodes.
		 */
		NODE_DELETE,
		/**
		 * Insert new edges.
		 */
		EDGE_INSERT,
		/**
		 * Remove edges.
		 */
		EDGE_DELETE
	}
	
	/**
	 * All registered listeners.
	 */
	private final Set<ProgramStateListener<V, E>> listeners = new HashSet<ProgramStateListener<V, E>>();
	/**
	 * Used location mapper.
	 */
	private final LocationMapper<V, E> locationMapper;
	/**
	 * Used color mapper.
	 */
	private final ColorMapper<V, E> colorMapper;

	/**
	 * Vertex factory is fixed, since only one vertex type is used.
	 */
	private VertexFactory<V> vertexFactory;
	/**
	 * Currently used graph.
	 */
	private Graph<V, E> graph = new Graph<V, E>(true, StandardEdge.class, IntegerVertex.class);
	/**
	 * The current GUI state.
	 */
	private State state = State.MOVE_NODES;
	/**
	 * Used edge factory.
	 */
	private EdgeFactory<E> edgeFactory = (EdgeFactory<E>) new StandardEdgeFactory();
	/**
	 * Used panel helper.
	 */
	private EdgePanelHelper<E> edgePanelHelper = (EdgePanelHelper<E>) new StandardEdgePanelHelper<E>();
	
	private V startNode;
	private V endNode;
	
	/**
	 * Directory for FileChooser dialog
	 */
	private File fileChooserDirectory;

	/**
	 * Constructor
	 *
	 * @param locationMapper location mapper to use
	 * @param colorMapper color mapper to use
	 * @param vertexFactory vertex factory to use
	 */
	public ProgramState(final LocationMapper<V, E> locationMapper, final ColorMapper<V, E> colorMapper,
			final VertexFactory<V> vertexFactory) {
		if (locationMapper == null || colorMapper == null || vertexFactory == null) {
			throw new NullPointerException();
		}
		this.locationMapper = locationMapper;
		this.colorMapper = colorMapper;
		this.vertexFactory = vertexFactory;
		graph.addGraphListener(this);
		fileChooserDirectory = new File(System.getProperty("user.dir"));
	}

	public synchronized void addStateListener(final ProgramStateListener<V, E> listener) {
		if (listener == null) {
			throw new NullPointerException();
		}

		listeners.add(listener);
	}

	public synchronized Graph<V, E> getGraph() {
		return graph;
	}

	public synchronized void removeStateListener(final ProgramStateListener<V, E> listener) {
		if (listener == null) {
			throw new NullPointerException();
		}

		listeners.remove(listener);
	}

	public synchronized void setGraph(final Graph<V, E> graph) {
		if (graph == null) {
			throw new NullPointerException();
		}
		Graph<V, E> oldGraph = this.graph;
		this.graph = graph;

		// Sets the new listeners
		for (GraphListener<V, E> listener : oldGraph.getListeners()) {
			graph.addGraphListener(listener);
		}

		vertexFactory.reset();

		for (ProgramStateListener<V, E> listener : listeners) {
			listener.newGraphLoaded(graph, oldGraph);
		}
	}

	public EdgeFactory<E> getEdgeFactory() {
		return edgeFactory;
	}

	public void setEdgeFactory(final EdgeFactory<E> factory) {
		if (factory == null) {
			throw new NullPointerException();
		}
		this.edgeFactory = factory;
	}

	public EdgePanelHelper<E> getEdgePanelHelper() {
		return edgePanelHelper;
	}

	public void setEdgePanelHelper(final EdgePanelHelper<E> panelHelper) {
		if (panelHelper == null) {
			throw new NullPointerException();
		}
		this.edgePanelHelper = panelHelper;
	}

	public LocationMapper<V, E> getLocationMapper() {
		return locationMapper;
	}

	public State getState() {
		return state;
	}

	public void setState(final State state) {
		State oldState = this.state;
		this.state = state;
		
		for (ProgramStateListener<V, E> listener : listeners) {
			listener.stateChanged(oldState, state);
		}
	}

	public ColorMapper<V, E> getColorMapper() {
		return colorMapper;
	}

	public VertexFactory<V> getVertexFactory() {
		return vertexFactory;
	}

	public void setVertexFactory(final VertexFactory<V> vertexFactory) {
		if (vertexFactory == null) {
			throw new NullPointerException();
		}
		this.vertexFactory = vertexFactory;
	}

	public V getStartNode() {
		return startNode;
	}

	/**
	 * Set start node, remove the special color from
	 * the old start node and add it to the new start node.
	 * @param startNode
	 */
	public void setStartNode(V startNode) {
		V oldStartNode = this.startNode;
		this.startNode = startNode;
		updateColor(oldStartNode);
		updateColor(this.startNode);
	}

	public V getEndNode() {
		return endNode;
	}

	/**
	 * Set end node, remove the special color from
	 * the old end node and add it to the new end node.
	 * @param endNode
	 */
	public void setEndNode(V endNode) {
		V oldEndNode = this.endNode;
		this.endNode = endNode;
		updateColor(oldEndNode);
		updateColor(this.endNode);
	}

	public File getFileChooserDirectory() {
		return fileChooserDirectory;
	}

	public void setFileChooserDirectory(File directory) {
		this.fileChooserDirectory = directory;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#edgeAdded(ch.fhnw.edu.efalg.graph.Edge)
	 */
	@Override
	public void edgeAdded(final E e) {
		notifyGraphChange();
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#edgeRemoved(ch.fhnw.edu.efalg.graph.Edge)
	 */
	@Override
	public void edgeRemoved(final E e) {
		notifyGraphChange();
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#vertexAdded(ch.fhnw.edu.efalg.graph.Vertex)
	 */
	@Override
	public void vertexAdded(final V v) {
		notifyGraphChange();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.fhnw.edu.efalg.graph.GraphListener#vertexRemoved(ch.fhnw.edu.efalg.graph.Vertex)
	 */
	@Override
	public void vertexRemoved(final V v) {
		notifyGraphChange();
	}

	/**
	 * Private helper method to notify the listeners about program changes.
	 */
	private void notifyGraphChange() {
		for (ProgramStateListener<V, E> listener : listeners) {
			listener.graphChanged();
		}
	}
	
	/**
	 * Update color of a node to the current state
	 * (is it the start node / end node / both / none).
	 * @param node
	 */
	public void updateColor(V node) {
		if (node != null) {
			boolean isStartNode = startNode == node;
			boolean isEndNode = endNode == node;
			boolean isBoth = isStartNode && isEndNode;
			
			Color color;
			if (isBoth) {
				color = ColorMapper.START_AND_END_NODE_COLOR;
			}
			else if (isStartNode) {
				color = ColorMapper.START_NODE_COLOR;
			}
			else if (isEndNode) {
				color = ColorMapper.END_NODE_COLOR;
			}
			else {
				color = ColorMapper.DEFAULT_VERTEX_COLOR;
			}
			colorMapper.setVertexColor(node, color);
		}
	}
}
