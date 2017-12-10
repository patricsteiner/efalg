package ch.fhnw.edu.efalg.graph.algorithms;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;

import java.util.*;

/**
 * Implementation of the breadth-first-search (BFS) algorithm. Same as DFS, but uses a Queue instead of a Stack.
 * 
 * @author Patric Steiner
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public final class BFS<V extends Vertex, E extends Edge> extends AbstractAlgorithm<V, E> {

	/**
	 * Constructor
	 */
	public BFS() {
		super("Breadth First Search", true);
	}

	/**
	 * This algorithm works with all graph implementations, however a start vertex needs to be selected.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean worksWith(final Graph<V, E> graph) {
		return true;
	}

	/**
	 * Implementation of the BFS algorithm.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String execute(final GraphAlgorithmData<V, E> data) {
		// Queue to determine the order of nodes to visit.
		Queue<V> toVisit = new LinkedList<>();
		// Stores all already seen vertices
		Set<V> visited = new HashSet<>();
		// Stores the edges that led to a node
		Map<V, E> usedEdge = new HashMap<>();

		V start = getStartNode(data);
		if (start == null) {
			return "Empty graph,\nnothing to do";
		}
		toVisit.add(start);

		while (!toVisit.isEmpty()) {
			V cur = toVisit.poll();
			if (!visited.contains(cur)) {
				visited.add(cur);
				for (E e : data.getGraph().getOutgoingEdges(cur)) {
					V dst = otherEndpoint(data, e, cur);
					if (!visited.contains(dst)) {
						usedEdge.put(dst, e);
						toVisit.add(dst);
					}
				}
			}
		}

		// Stores the used edges
		List<E> edges = new LinkedList<>(usedEdge.values());

		highlightEdges(data, edges);
		darkenOtherEdges(data, edges);

		return "Finished";
	}
}
