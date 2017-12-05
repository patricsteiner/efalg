package ch.fhnw.edu.efalg.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * A graph implementation using HashMaps and HashSets to store the needed information. This graph can be used with
 * either directed or undirected edges.
 * 
 * @author Martin Schaub
 * 
 * @param <V> Vertex type
 * @param <E> Edge type
 */
public class Graph<V extends Vertex, E extends Edge> {
	/**
	 * Defines whether the graph is directed or not.
	 */
	private final boolean directed;
	/**
	 * Instantiated vertex class
	 */
	private final Class<? extends Vertex> vertexClass;
	/**
	 * Instantiated edge class
	 */
	private final Class<? extends Edge> edgeClass;

	/**
	 * Outgoing Edge list.
	 */
	private Map<V, Set<E>> outgoingEdges = new HashMap<V, Set<E>>();
	/**
	 * Incoming Edge list.
	 */
	private Map<V, Set<E>> incomingEdges = new HashMap<V, Set<E>>();
	/**
	 * Outgoing Vertex list.
	 */
	private Map<V, Set<V>> outgoingVertices = new HashMap<V, Set<V>>();
	/**
	 * Incoming Vertex list.
	 */
	private Map<V, Set<V>> incomingVertices = new HashMap<V, Set<V>>();
	/**
	 * Saves the source of a edge.
	 */
	private Map<E, V> sources = new HashMap<E, V>();
	/**
	 * Stores the destination of a edge.
	 */
	private Map<E, V> destination = new HashMap<E, V>();

	/**
	 * For faster access this set stores all vertices
	 */
	private Set<V> vertices = new HashSet<V>();
	/**
	 * For faster access to all edges this set was introduced.
	 */
	private Set<E> edges = new HashSet<E>();
	/**
	 * Stores all registered graph listeners.
	 */
	private Set<GraphListener<V, E>> listeners = new HashSet<GraphListener<V, E>>();

	/**
	 * Constructor
	 * 
	 * @param directed true, if the graph should be directed or false otherwise
	 * @param edgeClass edge class
	 * @param vertexClass vertex class
	 */
	public Graph(final boolean directed, final Class<? extends Edge> edgeClass,
			final Class<? extends Vertex> vertexClass) {
		if (edgeClass == null || vertexClass == null) {
			throw new NullPointerException();
		}
		this.directed = directed;
		this.edgeClass = edgeClass;
		this.vertexClass = vertexClass;
	}

	/**
	 * Adds a new edge to the graph. If the graph is undirected the edge will also be added in the other direction. Self
	 * loops are not allowed.
	 * 
	 * @param from source vertex.
	 * @param to destination vertex.
	 * @param e edge to add.
	 * @return true, if the edge wasn't already in the graph. If there was already a different edge in this direction, the
	 *         answer is still true and this edge will be removed.
	 */
	public synchronized boolean addEdge(final V from, final V to, final E e) {
		if (!vertices.contains(from) || !vertices.contains(to)) {
			throw new IllegalArgumentException("Vertices must be in graph");
		}
		if (e == null) {
			throw new NullPointerException("Edge mustn't be null");
		}
		if (from.equals(to)) {
			throw new IllegalArgumentException("Self loops are not allowed");
		}
		if (edges.contains(e)) {
			return false;
		}

		E prevEdge = getEdge(from, to);
		if (prevEdge != null) {
			removeEdge(prevEdge);
		}

		edges.add(e);
		checkRetNull(sources.put(e, from));
		checkRetNull(destination.put(e, to));

		addEdgeInternal(from, to, e);
		if (!directed) {
			addEdgeInternal(to, from, e);
		}

		// Notify listeners
		for (GraphListener<V, E> listener : listeners) {
			listener.edgeAdded(e);
		}

		assert getEdge(from, to).equals(e);
		assert getEndpoints(e).get(0).equals(from);
		assert getEndpoints(e).get(1).equals(to);
		assert directed || getEdge(to, from).equals(e);
		assert getEdges().contains(e);

		return true;
	}

	/**
	 * Adds an edge to the various data structures.
	 * 
	 * @param from source
	 * @param to destination
	 * @param e edge to add
	 */
	private void addEdgeInternal(final V from, final V to, final E e) {
		checkRetTrue(incomingEdges.get(to).add(e));
		checkRetTrue(incomingVertices.get(to).add(from));
		checkRetTrue(outgoingEdges.get(from).add(e));
		checkRetTrue(outgoingVertices.get(from).add(to));
	}

	/**
	 * Adds a vertex to the graph.
	 * 
	 * @param v vertex to add.
	 * @return true, if the vertex wasn't already in the graph and false otherwise.
	 */
	public synchronized boolean addVertex(final V v) {
		if (v == null) {
			throw new NullPointerException("Vertex mustn't be null");
		}

		if (vertices.contains(v)) {
			return false;
		}

		checkRetNull(incomingEdges.put(v, new HashSet<E>()));
		checkRetNull(incomingVertices.put(v, new HashSet<V>()));
		checkRetNull(outgoingEdges.put(v, new HashSet<E>()));
		checkRetNull(outgoingVertices.put(v, new HashSet<V>()));
		checkRetTrue(vertices.add(v));

		// Notify listeners
		for (GraphListener<V, E> listener : listeners) {
			listener.vertexAdded(v);
		}

		assert getOutgoingAdjacence(v).size() == 0;
		assert getIncomingAdjacence(v).size() == 0;
		assert getVertices().contains(v);

		return true;
	}

	/**
	 * Returns the edge between two vertices or null if it does not exist. In case of an undirected graph, both cases are
	 * considered.
	 * 
	 * @param from source vertex
	 * @param to destination vertex
	 * @return the edge contained in it or null if it does not exist
	 */
	public synchronized E getEdge(final V from, final V to) {
		if (!vertices.contains(from) || !vertices.contains(to)) {
			throw new IllegalArgumentException("Vertices must be in graph");
		}

		Set<E> outgoing = outgoingEdges.get(from);
		for (E e : outgoing) {
			V dest = destination.get(e);
			V source = sources.get(e);
			if (directed && dest.equals(to)) {
				return e;
			}
			else if (!directed
					&& ((dest.equals(to) && source.equals(from)) || (dest
							.equals(from) && source.equals(to)))) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Returns all edges in the graph. This set has to be considered read only.
	 * 
	 * @return all edges in the graph.
	 */
	public Set<E> getEdges() {
		return Collections.unmodifiableSet(edges);
	}

	/**
	 * Gets the endpoints of an edge. Position 0 is the source and position 1 the destination.
	 * @param e whose endpoints must be collected
	 * @return the endpoints
	 */
	public synchronized List<V> getEndpoints(final E e) {
		if (!edges.contains(e)) {
			throw new IllegalArgumentException("Edge must be in the graph");
		}

		ArrayList<V> list = new ArrayList<V>();
		list.add(sources.get(e));
		list.add(destination.get(e));

		assert list.size() == 2;
		assert getEdge(list.get(0), list.get(1)).equals(e);
		return list;
	}

	/**
	 * Gets all neighbors of an vertex, to which he has an incoming edge in case of a directed graph or is connected to it
	 * form an undirected graph. The set must be considered read only.
	 * @param v vertex
	 * @return a set of all neighbors
	 */
	public synchronized Set<V> getIncomingAdjacence(final V v) {
		if (!vertices.contains(v)) {
			throw new IllegalArgumentException("Vertex must be in graph");
		}
		return Collections.unmodifiableSet(incomingVertices.get(v));
	}

	public int getNumOfEdges() {
		return edges.size();
	}

	public int getNumOfVertices() {
		return vertices.size();
	}

	/**
	 * Gets all neighbors of a vertex to which he has an outgoing edge (directed graph)
	 * or is connected to (undirected graph).
	 * @param v vertex
	 * @return read-only set of all neighbor vertices
	 */
	public synchronized Set<V> getOutgoingAdjacence(final V v) {
		if (!vertices.contains(v)) {
			throw new IllegalArgumentException("Vertex must be in graph");
		}
		return Collections.unmodifiableSet(outgoingVertices.get(v));
	}

	/**
	 * Returns all vertices as a read-only set.
	 * 
	 * @return set containing all vertices.
	 */
	public Set<V> getVertices() {
		return Collections.unmodifiableSet(vertices);
	}

	public boolean isDirected() {
		return directed;
	}

	/**
	 * Removes an edge from the graph.
	 * 
	 * @param e edge to remove.
	 * @return true, if the edge was in the graph and false otherwise.
	 */
	public synchronized boolean removeEdge(final E e) {
		if (e == null) {
			throw new NullPointerException();
		}

		if (!edges.contains(e)) {
			return false;
		}

		V src = sources.get(e);
		V dst = destination.get(e);

		checkRetNotNull(incomingVertices.get(dst).remove(src));
		checkRetNotNull(outgoingVertices.get(src).remove(dst));
		checkRetNotNull(incomingEdges.get(dst).remove(e));
		checkRetNotNull(outgoingEdges.get(src).remove(e));

		if (!directed) {
			checkRetNotNull(incomingVertices.get(src).remove(dst));
			checkRetNotNull(outgoingVertices.get(dst).remove(src));
			checkRetNotNull(incomingEdges.get(src).remove(e));
			checkRetNotNull(outgoingEdges.get(dst).remove(e));
		}

		checkRetTrue(edges.remove(e));
		checkRetNotNull(sources.remove(e));
		checkRetNotNull(destination.remove(e));

		// Notify listeners
		for (GraphListener<V, E> listener : listeners) {
			listener.edgeAdded(e);
		}

		assert !getEdges().contains(e);

		return true;
	}

	/**
	 * Checks that the object is not null and throws an exception otherwise.
	 * 
	 * @param obj object to test
	 * @throws InternalError if the object is null
	 */
	private void checkRetNotNull(final Object obj) {
		checkRetTrue(obj != null);
	}

	/**
	 * Checks that a object is null and throws an exception otherwise.
	 * 
	 * @param obj object to test
	 * @throws InternalError if the object is not null
	 */
	private void checkRetNull(final Object obj) {
		checkRetTrue(obj == null);
	}

	/**
	 * Checks whether a bool is true or not. This method is called to verify return values.
	 * 
	 * @param bool boolean to check
	 * @throws InternalError if the boolean is not true
	 */
	private void checkRetTrue(final boolean bool) {
		if (!bool) {
			throw new InternalError();
		}
	}

	/**
	 * Removes a vertex from the graph.
	 * Removes all connected edges as well.
	 * 
	 * @param v vertex to remove
	 * @return true, if the vertex was in the graph and false otherwise.
	 */
	public synchronized boolean removeVertex(final V v) {
		if (v == null) {
			throw new NullPointerException();
		}

		if (!vertices.contains(v)) {
			return false;
		}

		// Cascade removal
		for (E e : new LinkedList<E>(incomingEdges.get(v))) {
			checkRetTrue(removeEdge(e));
		}
		for (E e : new LinkedList<E>(outgoingEdges.get(v))) {
			checkRetTrue(removeEdge(e));
		}

		// Actually remove vertex
		checkRetNotNull(incomingEdges.remove(v));
		checkRetNotNull(outgoingEdges.remove(v));
		checkRetTrue(vertices.remove(v));

		// Notify listeners
		for (GraphListener<V, E> listener : listeners) {
			listener.vertexRemoved(v);
		}

		assert !vertices.contains(v);

		return true;
	}

	/**
	 * Gets all incoming edges in case of a directed graph or all edges connected to v otherwise. The set must be
	 * considered read only.
	 * 
	 * @param v vertex to get the incoming edges from
	 * @return all incoming edges
	 */
	public synchronized Set<E> getIncomingEdges(final V v) {
		if (v == null) {
			throw new NullPointerException();
		}
		if (!vertices.contains(v)) {
			throw new IllegalArgumentException("Vertex must be in graph");
		}
		return Collections.unmodifiableSet(incomingEdges.get(v));
	}

	/**
	 * Directed graph: gets all outgoing edges of a vertex.
	 * Undirected graph: gets all edges connected to v.
	 * 
	 * @param v vertex to get the outgoing edges from
	 * @return read-only set of all outgoing edges of a vertex
	 */
	public synchronized Set<E> getOutgoingEdges(final V v) {
		if (v == null) {
			throw new NullPointerException();
		}
		if (!vertices.contains(v)) {
			throw new IllegalArgumentException("Vertex must be in graph");
		}
		return Collections.unmodifiableSet(outgoingEdges.get(v));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public synchronized Graph<V, E> clone() {
		Graph<V, E> ret = new Graph<V, E>(directed, edgeClass, vertexClass);

		ret.edges = new HashSet<E>(edges);
		ret.vertices = new HashSet<V>(vertices);

		ret.incomingEdges = new HashMap<V, Set<E>>(incomingEdges.size() * 2);
		for (Entry<V, Set<E>> e : incomingEdges.entrySet()) {
			ret.incomingEdges.put(e.getKey(), new HashSet<E>(e.getValue()));
		}
		ret.incomingVertices = new HashMap<V, Set<V>>(incomingVertices.size() * 2);
		for (Entry<V, Set<V>> e : incomingVertices.entrySet()) {
			ret.incomingVertices.put(e.getKey(), new HashSet<V>(e.getValue()));
		}
		ret.outgoingEdges = new HashMap<V, Set<E>>(outgoingEdges.size() * 2);
		for (Entry<V, Set<E>> e : outgoingEdges.entrySet()) {
			ret.outgoingEdges.put(e.getKey(), new HashSet<E>(e.getValue()));
		}
		ret.outgoingVertices = new HashMap<V, Set<V>>(outgoingVertices.size() * 2);
		for (Entry<V, Set<V>> e : outgoingVertices.entrySet()) {
			ret.outgoingVertices.put(e.getKey(), new HashSet<V>(e.getValue()));
		}

		ret.destination = new HashMap<E, V>(destination);
		ret.sources = new HashMap<E, V>(sources);

		ret.listeners = new HashSet<GraphListener<V, E>>(listeners);

		return ret;
	}

	public void addGraphListener(final GraphListener<V, E> listener) {
		if (listener == null) {
			throw new NullPointerException();
		}

		listeners.add(listener);
	}

	public void removeGraphListener(final GraphListener<V, E> listener) {
		if (listener == null) {
			throw new NullPointerException();
		}

		listeners.remove(listener);
	}

	/**
	 * Get all listeners as a read-only set.
	 * @return
	 */
	public Set<GraphListener<V, E>> getListeners() {
		return Collections.unmodifiableSet(listeners);
	}

	public Class<? extends Edge> edgeClass() {
		return edgeClass;
	}

	public Class<? extends Vertex> vertexClass() {
		return vertexClass;
	}
}
