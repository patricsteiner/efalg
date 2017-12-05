package ch.fhnw.edu.efalg.graph;

import ch.fhnw.edu.efalg.graph.gui.impl.ColorMapper;
import ch.fhnw.edu.efalg.graph.gui.impl.LocationMapper;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState;

/**
 * Implementation which uses the ProgramState as backend.
 * 
 * @author Martin Schaub
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public class GraphAlgorithmData<V extends Vertex, E extends Edge> {

	/**
	 * Current program state.
	 */
	private final ProgramState<V, E> state;

	/**
	 * Constructor
	 * 
	 * @param state current program state
	 */
	public GraphAlgorithmData(final ProgramState<V, E> state) {
		if (state == null) {
			throw new NullPointerException();
		}
		this.state = state;
	}

	public LocationMapper<V, E> getLocationMapper() {
		return state.getLocationMapper();
	}

	public ColorMapper<V, E> getColorMapper() {
		return state.getColorMapper();
	}

	public EdgeFactory<E> getEdgeFactory() {
		return state.getEdgeFactory();
	}

	public Graph<V, E> getGraph() {
		return state.getGraph();
	}

	public V getStartNode() {
		return state.getStartNode();
	}

	public void setStartNode(V v) {
		state.setStartNode(v);
	}

	public V getEndNode() {
		return state.getEndNode();
	}

	public void setEndNode(V v) {
		state.setEndNode(v);
	}
	
}
