package ch.fhnw.edu.efalg.graph.edges;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.EdgeFactory;

/**
 * EdgePanelHelper for CapacityFlowEdges.
 * 
 * @author Michel Pluess
 *
 * @param <E>
 */
public class CapacityFlowEdgePanelHelper<E extends Edge> extends AbstractEdgePanelHelper<E> {

	private static final long serialVersionUID = -1293733770469874424L;

	/**
	 * @see AbstractEdgePanelHelper#createEdgeDetailsPanel()
	 */
	@Override
	public JPanel createEdgeDetailsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(EDGE_DETAILS_PANEL_TITLE));
		panel.setLayout(new GridLayout(1, 2, HGAP, VGAP));

		JLabel label = new JLabel("Capacity");
		JTextField textfield = new JTextField();
		panel.add(label);
		panel.add(textfield);
		return panel;
	}

	/**
	 * @see AbstractEdgePanelHelper#createEdgeFromPanel(JPanel, EdgeFactory)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E createEdgeFromPanel(JPanel panel, EdgeFactory<E> edgeFactory) {
		int capacity = parseTextField((JTextField)panel.getComponent(1));
		CapacityFlowEdgeFactory factory = (CapacityFlowEdgeFactory)edgeFactory;
		
		if (capacity == DEFAULT_INT_VALUE) {
			return (E)factory.newEdge();
		}
		else {
			return (E)factory.newEdge(capacity);
		}
	}

}
