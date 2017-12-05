package ch.fhnw.edu.efalg.graph.gui.impl;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.GraphListener;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.gui.LocationListener;

/**
 * Simple location mapper that listens for changes in the graph and then updates its coordinates. Its important that it
 * receives events before other graph listeners do, because they might ask for new locations.
 *
 * @author Martin Schaub
 *
 * @param <V> vertex type.
 * @param <E> edge type.
 */
public class LocationMapper<V extends Vertex, E extends Edge> implements GraphListener<V, E> {

	/**
	 * How much is calculated to a query position.
	 */
	public final static int TOLERANCE_POS = 2;
	/**
	 * How much is calculated to a vertex position.
	 */
	public final static int TOLERANCE_VERTICES = 10;

	/**
	 * Stores the mapping.
	 */
	private final Map<V, Node<V>> mapping = new HashMap<V, Node<V>>();
	/**
	 * Stores all registered listeners.
	 */
	private final Set<LocationListener<V>> listeners = new HashSet<LocationListener<V>>();

	/**
	 * Gets the vertex closest to the location x,y or null if there is no vertex.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the vertex closest to this location or null if there is no vertex.
	 */
	public synchronized V getAtPosition(final int x, final int y) {
		Node<V> selected = null;
		double distance = Double.MAX_VALUE;

		for (Node<V> node : mapping.values()) {
			double d = Math.sqrt((x - node.getX()) * (x - node.getX()) + (y - node.getY()) * (y - node.getY()));
			if (selected == null || d < distance) {
				distance = d;
				selected = node;
			}
		}

		if (selected != null) {
			Rectangle2D pos = new Rectangle2D.Double(x - TOLERANCE_POS, y - TOLERANCE_POS, x + TOLERANCE_POS, y
					+ TOLERANCE_POS);
			Rectangle2D nodePos = new Rectangle2D.Double(selected.getX() - TOLERANCE_VERTICES, selected.getY()
					- TOLERANCE_VERTICES, selected.getX() + TOLERANCE_VERTICES, selected.getY() + TOLERANCE_VERTICES);
			if (pos.intersects(nodePos)) {
				return selected.getVertex();
			}
		}

		return null;
	}

	/**
	 * Gets the x coordinate of the parameter
	 * @param v the vertex whose x coordinate will be returned.
	 * @return node that maps the vertex to the location.
	 */
	public synchronized Node<V> getLocation(final V v) {
		if (!mapping.containsKey(v)) {
			throw new IllegalArgumentException();
		}

		return mapping.get(v);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#edgeAdded(java.lang.Object)
	 */
	@Override
	public void edgeAdded(final E e) {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#edgeRemoved(java.lang.Object)
	 */
	@Override
	public void edgeRemoved(final E e) {
		// nothing to do
	}

	/**
	 * Vertex's location must be known before it is added to the graph.
	 */
	@Override
	public synchronized void vertexAdded(final V v) {
		if (!mapping.containsKey(v)) {
			throw new IllegalStateException();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphListener#vertexRemoved(java.lang.Object)
	 */
	@Override
	public synchronized void vertexRemoved(final V v) {
		mapping.remove(v);
	}

	/**
	 * Sets the location of the vertex.
	 * 
	 * @param v vertex
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public synchronized void setLocation(final V v, final int x, final int y) {
		Node<V> node = new Node<V>(v);
		node.setX(x);
		node.setY(y);
		mapping.put(v, node);

		for (final LocationListener<V> listener : listeners) {
			listener.locationChanged(v);
		}
	}

	/**
	 * Removes all mappings.
	 */
	public synchronized void reset() {
		mapping.clear();
	}

	public void addLocationListener(final LocationListener<V> listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		listeners.add(listener);
	}

	public void removeLocationListener(final LocationListener<V> listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		listeners.remove(listener);
	}
}
