package ch.fhnw.edu.efalg.graph.impl;

import ch.fhnw.edu.efalg.graph.DirectedGraphTest;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.edges.IntegerEdge;
import ch.fhnw.edu.efalg.graph.vertices.IntegerVertex;

/**
 * Tests the implementation as directed graph.
 * 
 * @author Martin Schaub
 */
public final class GraphTestDirected extends DirectedGraphTest {

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efalg.graph.GraphTest#getGraph()
	 */
	@Override
	protected Graph<IntegerVertex, IntegerEdge> getGraph() {
		return new Graph<IntegerVertex, IntegerEdge>(true, IntegerEdge.class, IntegerVertex.class);
	}

}
