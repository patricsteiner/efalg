package ch.fhnw.edu.efalg.graph.gui.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.GraphListener;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.gui.ColorMapperListener;

/**
 * Simple implementation of the ColorMapper interface. It uses the GraphListener interface to get notified when some
 * objects can be deleted from the datastructure.
 * 
 * @author Martin Schaub
 * @param <V> vertex type
 * @param <E> edge type
 */
public final class ColorMapper<V extends Vertex, E extends Edge> implements GraphListener<V, E> {

	/**
	 * Default edge color, when no user specific is set.
	 */
	public final static Color DEFAULT_EDGE_COLOR = Color.BLACK;
	/**
	 * Default edge color, when no user specific is set.
	 */
	public final static Color DEFAULT_VERTEX_COLOR = Color.GRAY;
	/**
	 * Unselected vertex border color.
	 */
	public final static Color DEFAULT_VERTEX_BORDER_COLOR = Color.BLACK;
	/**
	 * Selected vertex border color.
	 */
	public final static Color SELECTED_VERTEX_BORDER_COLOR = Color.RED;
	/**
	 * Color of start node.
	 */
	public final static Color START_NODE_COLOR = Color.GREEN;
	/**
	 * Color of end node.
	 */
	public final static Color END_NODE_COLOR = Color.YELLOW;
	/**
	 * Mix between yellow and green if a vertex is both
	 * start and end node.
	 */
	public final static Color START_AND_END_NODE_COLOR = new Color(204, 255, 77);
	
	private final Set<ColorMapperListener<V, E>> listeners = new HashSet<ColorMapperListener<V, E>>();

	private final Map<V, Color> vertexColor = new HashMap<V, Color>();
	private final Map<V, Color> vertexBorderColor = new HashMap<V, Color>();
	private final Map<E, Color> edgeColor = new HashMap<E, Color>();

	public synchronized Color getEdgeColor(final E e) {
		if (e == null) {
			throw new NullPointerException();
		}

		if (edgeColor.containsKey(e)) {
			return edgeColor.get(e);
		}

		return DEFAULT_EDGE_COLOR;
	}

	public synchronized Color getVertexColor(final V v) {
		if (v == null) {
			throw new NullPointerException();
		}

		if (vertexColor.containsKey(v)) {
			return vertexColor.get(v);
		}

		return DEFAULT_VERTEX_COLOR;
	}

	public synchronized Color getVertexBorderColor(final V v) {
		if (v == null) {
			throw new NullPointerException();
		}

		if (vertexBorderColor.containsKey(v)) {
			return vertexBorderColor.get(v);
		}

		return DEFAULT_VERTEX_BORDER_COLOR;
	}

	public synchronized void setEdgeColor(final E e, final Color c) {
		if (e == null || c == null) {
			throw new NullPointerException();
		}

		edgeColor.put(e, c);

		for (ColorMapperListener<V, E> listener : listeners) {
			listener.newEdgeColor(e, c);
		}
	}

	public synchronized void setVertexColor(final V v, final Color c) {
		if (v == null || c == null) {
			throw new NullPointerException();
		}

		vertexColor.put(v, c);

		for (ColorMapperListener<V, E> listener : listeners) {
			listener.newVertexColor(v, c);
		}
	}

	public synchronized void setVertexBorderColor(final V v, final Color c) {
		if (v == null || c == null) {
			throw new NullPointerException();
		}

		vertexBorderColor.put(v, c);

		for (ColorMapperListener<V, E> listener : listeners) {
			listener.newVertexBorderColor(v, c);
		}
	}

	/**
	 * Clear colors, notify listeners.
	 */
	public synchronized void reset() {
		vertexBorderColor.clear();
		resetKeepSelection();
	}
	/**
	 * Clear vertex and edge colors, notify listeners.
	 */
	public synchronized void resetKeepSelection() {
		vertexColor.clear();
		edgeColor.clear();
		for (ColorMapperListener<V, E> listener : listeners) {
			listener.colorsCleared();
		}
	}

	public synchronized void addColorMapperListener(final ColorMapperListener<V, E> listener) {
		if (listener == null) {
			throw new NullPointerException();
		}

		listeners.add(listener);
	}

	public synchronized void removeColorMapperListener(final ColorMapperListener<V, E> listener) {
		if (listener == null) {
			throw new NullPointerException();
		}

		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#edgeAdded(java.lang.Object)
	 */
	@Override
	public void edgeAdded(final E e) {
		// nothing to do -> uses the default color automatically
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#edgeRemoved(java.lang.Object)
	 */
	@Override
	public synchronized void edgeRemoved(final E e) {
		edgeColor.remove(e);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#vertexAdded(java.lang.Object)
	 */
	@Override
	public void vertexAdded(final V v) {
		// nothing to do -> uses the default color automatically
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#vertexRemoved(java.lang.Object)
	 */
	@Override
	public synchronized void vertexRemoved(final V v) {
		vertexColor.remove(v);
	}

}
