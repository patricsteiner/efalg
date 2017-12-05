package ch.fhnw.edu.efalg.graph.algorithms;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;

/**
 * Abstract base class for the GraphAlgorithms hierarchy.
 * 
 * @author Martin Schaub
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public abstract class AbstractAlgorithm<V extends Vertex, E extends Edge> {

	/**
	 * Algorithms name.
	 */
	private final String name;
	/**
	 * True if this algorithm can run in a separate thread.
	 */
	private final boolean runInThread;

	/**
	 * Constructor
	 * 
	 * @param name algorithm's name
	 * @param runInThread Can this algorithm run in a separate thread
	 */
	public AbstractAlgorithm(final String name, final boolean runInThread) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.runInThread = runInThread;
	}

	/**
	 * Executes the algorithm. The input graph needs to be considered read only.
	 * 
	 * @param data needed data for the algorithms
	 * @return status message or result.
	 */
	public abstract String execute(GraphAlgorithmData<V, E> data);

	/**
	 * Does it work with this graph implementation.
	 * 
	 * @param graph to check
	 * @return true, when this algorithm works on this graph.
	 */
	public abstract boolean worksWith(Graph<V, E> graph);

	
	/**
	 * If this algorithm does not change the graph, it can be run in a separate thread. Hence if the algorithm is started
	 * over a GUI the AWT repaint process can continue its work. If this behavior is requested but the algorithm changes
	 * the graph, synchronizing on the program state does work.
	 * 
	 * @return true if this thread is ready for running in a separate thread.
	 */
	public final boolean isThreadReady() {
		return runInThread;
	}

	/**
	 * Returns the name of the algorithm.
	 * @return Algorithm's name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Start node for the algorithm.
	 * It uses the currently selected start node or just a random one,
	 * if none is selected.
	 * 
	 * @param data information to select the data from.
	 * @return start node or null if the graph has no vertices
	 */
	protected V getStartNode(final GraphAlgorithmData<V, E> data) {
		if (data == null) {
			throw new NullPointerException();
		}

		// Get the selected vertex
		if (data.getStartNode() != null) {
			return data.getStartNode();
		}
		else {
			V startNode = getRandomVertex(data);
			data.setStartNode(startNode);
			return startNode;
		}
	}

	/**
	 * Sometimes an end node is needed (e.g. flow problems).
	 * It uses the currently selected end node or just a random one,
	 * if no end node is selected.
	 * 
	 * @param data information to select the data from.
	 * @return end node or null if the graph has no vertices
	 */
	protected V getEndNode(final GraphAlgorithmData<V, E> data) {
		if (data == null) {
			throw new NullPointerException();
		}

		// Get the selected vertex
		if (data.getEndNode() != null) {
			return data.getEndNode();
		}
		else {
			V endNode = getRandomVertex(data);
			data.setEndNode(endNode);
			return endNode;
		}
	}

	/**
	 * Selects a random vertex of the graph.
	 * 
	 * @param data graph to select the vertex from.
	 * @return a randomly selected vertex or null, if the graph does not contain any vertex.
	 */
	protected V getRandomVertex(final GraphAlgorithmData<V, E> data) {
		if (data == null) {
			throw new NullPointerException();
		}

		if (data.getGraph().getNumOfVertices() == 0) {
			return null;
		}

		// num is element of [0,numOfVertices-1]
		int num = (int) (Math.random() * data.getGraph().getNumOfVertices());
		Iterator<V> it = data.getGraph().getVertices().iterator();
		for (int i = 0; i < num; ++i) {
			it.next();
		}

		return it.next();
	}

	/**
	 * Selects a vertex different from the one passed to this method.
	 * 
	 * @param data to get the graph
	 * @param other the other vertex to compare to
	 * @return null, if the graph is empty or contains only one vertex and a randomly selected vertex otherwise.
	 */
	protected V getDifferentVertex(final GraphAlgorithmData<V, E> data, final Vertex other) {
		if (other == null || data == null) {
			throw new NullPointerException();
		}

		if (data.getGraph().getNumOfVertices() <= 2) {
			return null;
		}

		V v = getRandomVertex(data);
		while (v.equals(other)) {
			v = getRandomVertex(data);
		}
		return v;
	}

	/**
	 * Sets the color of a selected edge list to RED.
	 * 
	 * @param list list of edges to color
	 * @param data algorithm data (colormapper for changing the color)
	 */
	protected void highlightEdges(final GraphAlgorithmData<V, E> data, final List<E> list) {
		highlightEdges(data, list, Color.RED);
	}

	/**
	 * Sets the color of a selected edge list to a custom color.
	 * 
	 * @param list list of edges to color
	 * @param color color to highlight
	 * @param data algorithm data (colormapper for changing the color)
	 */
	protected void highlightEdges(final GraphAlgorithmData<V, E> data, final List<E> list, final Color color) {
		if (list == null || data == null) {
			throw new NullPointerException();
		}

		for (E e : list) {
			data.getColorMapper().setEdgeColor(e, color);
		}
	}

	/**
	 * Highlights a list of vertices.
	 * 
	 * @param data algorithm data (need the colormapper)
	 * @param list list of nodes to color
	 * @param color colored nodes.
	 */
	protected void highlightVertices(final GraphAlgorithmData<V, E> data, final List<V> list, final Color color) {
		for (V v : list) {
			data.getColorMapper().setVertexColor(v, color);
		}
	}

	/**
	 * Darkens the edges which aren't in the list
	 * 
	 * @param data needed data (graph and colormapper)
	 * @param list list of edges that aren't highlighted.
	 */
	protected void darkenOtherEdges(final GraphAlgorithmData<V, E> data, final List<E> list) {
		if (list == null || data == null) {
			throw new NullPointerException();
		}

		Set<E> set = new HashSet<E>(list);

		for (E e : data.getGraph().getEdges()) {
			if (!set.contains(e)) {
				data.getColorMapper().setEdgeColor(e, Color.LIGHT_GRAY);
			}
		}
	}

	/**
	 * Just a wrapper so you can pass a data object like in the other methods.
	 * @see AbstractAlgorithm#otherEndpoint(Graph, Edge, Vertex)
	 */
	protected static <V extends Vertex, E extends Edge> V otherEndpoint(final GraphAlgorithmData<V, E> data, final E e,
			final V v) {
		return otherEndpoint(data.getGraph(), e, v);
	}
	
	/**
	 * Gets the other end point of a edge. This method is static, because it might be accessed in inner classes.
	 * 
	 * @param <V> vertex type
	 * @param <E> edge type
	 * 
	 * @param graph
	 * @param e edge of whose the endpoints are of interest.
	 * @param v one endpoint of the edge.
	 * 
	 * @return the other endpoint
	 */
	protected static <V extends Vertex, E extends Edge> V otherEndpoint(final Graph<V, E> graph, final E e,
			final V v) {
		if (graph == null || e == null || v == null) {
			throw new NullPointerException("graph=" + graph + " e=" + e + " v=" + v);
		}

		List<V> endpoints = graph.getEndpoints(e);
		if (endpoints.get(0).equals(v)) {
			return endpoints.get(1);
		}

		if (!endpoints.get(1).equals(v)) {
			throw new IllegalArgumentException("Is not an endpoint at all!");
		}

		return endpoints.get(0);
	}
}
