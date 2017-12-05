package ch.fhnw.edu.efalg.graph.algorithms;

import java.util.Comparator;
import java.util.Optional;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.GraphAlgorithmData;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.edges.IntegerEdge;
import ch.fhnw.edu.efalg.graph.edges.IntegerEdgeFactory;

/**
 * Base class for the approximation of the the TSP problem
 * using the MST heuristic.
 * 
 * @author Michel Pluess
 *
 * @param <V>
 * @param <E>
 */
public abstract class AbstractMSTHeuristicAlgorithm<V extends Vertex, E extends Edge> extends AbstractAlgorithm<V, E> {

	/**
	 *  Summand to be added to a new edge's weight during
	 */
	private static final int WEIGHT_SUMMAND = 100;
	
	public AbstractMSTHeuristicAlgorithm(final String name) {
		super(name, false);
	}
	
	@Override
	public String execute(GraphAlgorithmData<V, E> data) {
		// Select a start vertex
		V start = getStartNode(data);
		if (start == null) {
			return "Empty graph,\nnothing to do";
		}

		createCompleteGraph(data);
		int sumOfWeights = calculateAndShowPath(data, start);
		
		return "Finished\nPath length: " + sumOfWeights;
	}
	
	@Override
	public boolean worksWith(Graph<V, E> graph) {
		return graph.edgeClass().equals(IntegerEdge.class) && graph.isDirected();
	}
	
	/**
	 * Calculate the path of the travelling salesman according to
	 * the MST heuristic and show this path. Remove other edges.
	 * 
	 * YOUR IMPLEMENTATION OVERRIDES THIS METHOD
	 * 
	 * @param data
	 * @param start
	 * @return sum of weights of the calculated path --> path length
	 */
	abstract int calculateAndShowPath(GraphAlgorithmData<V, E> data, V start);
	
	/**
	 * Upgrades the graph in data to a complete graph.
	 * @param data to access the graph
	 */
	@SuppressWarnings("unchecked")
	private void createCompleteGraph(final GraphAlgorithmData<V, E> data) {
		Graph<V, E> g = data.getGraph();
		
		int hugeWeight = WEIGHT_SUMMAND;
		Optional<E> maxWeightEdge = g.getEdges().stream().max(Comparator.comparing(e -> ((IntegerEdge)e).getWeight()));
		if (maxWeightEdge.isPresent()) {
			hugeWeight += ((IntegerEdge)maxWeightEdge.get()).getWeight();
		}
		for (V src : data.getGraph().getVertices()) {
			for (V dst : data.getGraph().getVertices()) {
				if (!src.equals(dst)) {
					E edge = data.getGraph().getEdge(src, dst);
					E backEdge = data.getGraph().getEdge(dst, src);
					if (edge == null) {
						// Non-existing edge: add edge with huge weight. 
						if (backEdge == null) {
							Edge newEdge = ((IntegerEdgeFactory)data.getEdgeFactory()).newEdge(hugeWeight);
							g.addEdge(src, dst, (E)newEdge);
						}
						// Back edge exists: add edge with same weight as back edge.
						else {
							Edge newEdge = ((IntegerEdgeFactory)data.getEdgeFactory()).newEdge(((IntegerEdge)backEdge).getWeight());
							g.addEdge(src, dst, (E)newEdge);
						}
					}
					
				}
			}
		}
	}

}
