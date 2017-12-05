package ch.fhnw.edu.efalg.graph.edges;

import javax.swing.JPanel;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.EdgeFactory;

/**
 * EdgePanelHelper for StandardEdges.
 * 
 * @author Michel Pluess
 *
 * @param <E>
 */
public class StandardEdgePanelHelper<E extends Edge> extends AbstractEdgePanelHelper<E> {

	private static final long serialVersionUID = 8610517448721374355L;

	/**
	 * @see AbstractEdgePanelHelper#createEdgeDetailsPanel()
	 */
	@Override
	public JPanel createEdgeDetailsPanel() {
		return new JPanel();
	}

	/**
	 * @see AbstractEdgePanelHelper#createEdgeFromPanel(JPanel, EdgeFactory)
	 */
	@Override
	public E createEdgeFromPanel(JPanel panel, EdgeFactory<E> edgeFactory) {
		return edgeFactory.newEdge();
	}

}
