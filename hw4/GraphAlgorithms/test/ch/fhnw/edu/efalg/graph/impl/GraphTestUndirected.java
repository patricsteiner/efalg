package ch.fhnw.edu.efalg.graph.impl;

import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.UndirectedGraphTest;
import ch.fhnw.edu.efalg.graph.edges.IntegerEdge;
import ch.fhnw.edu.efalg.graph.vertices.IntegerVertex;

/**
 * Tests the implementation as a undirected graph.
 * 
 * @author Martin Schaub
 */
public final class GraphTestUndirected extends UndirectedGraphTest {

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphTest#getGraph()
	 */
	@Override
	protected Graph<IntegerVertex, IntegerEdge> getGraph() {
		return new Graph<IntegerVertex, IntegerEdge>(false, IntegerEdge.class, IntegerVertex.class);
	}

}
