package ch.fhnw.edu.efalg.graph.edges;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.EdgeFactory;

/**
 * EdgePanelHelper for CostCapacityFlowEdges.
 * 
 * @author Michel Pluess
 *
 * @param <E>
 */
public class CostCapacityFlowEdgePanelHelper<E extends Edge> extends AbstractEdgePanelHelper<E> {

	private static final long serialVersionUID = -7337141112979211143L;

	/**
	 * @see AbstractEdgePanelHelper#createEdgeDetailsPanel()
	 */
	@Override
	public JPanel createEdgeDetailsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(EDGE_DETAILS_PANEL_TITLE));
		panel.setLayout(new GridLayout(2, 2, HGAP, VGAP));

		JLabel labelCapacity = new JLabel("Capacity");
		JTextField textfieldCapacity = new JTextField();
		panel.add(labelCapacity);
		panel.add(textfieldCapacity);

		JLabel labelCost = new JLabel("Cost");
		JTextField textfieldCost = new JTextField();
		panel.add(labelCost);
		panel.add(textfieldCost);
		return panel;
	}

	/**
	 * @see AbstractEdgePanelHelper#createEdgeFromPanel(JPanel, EdgeFactory)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E createEdgeFromPanel(JPanel panel, EdgeFactory<E> edgeFactory) {
		int capacity = parseTextField((JTextField)panel.getComponent(1));
		int cost = parseTextField((JTextField)panel.getComponent(3));
		CostCapacityFlowEdgeFactory factory = (CostCapacityFlowEdgeFactory)edgeFactory;
		
		if (capacity != DEFAULT_INT_VALUE && cost == DEFAULT_INT_VALUE) {
			return (E)factory.newEdgeWithCapacity(capacity);
		}
		else if (capacity != DEFAULT_INT_VALUE && cost != DEFAULT_INT_VALUE) {
			return (E)factory.newEdgeWithCapacityAndCost(capacity, cost);
		}
		else if (capacity == DEFAULT_INT_VALUE && cost != DEFAULT_INT_VALUE) {
			return (E)factory.newEdgeWithCost(cost);
		}
		else {
			return (E)factory.newEdge();
		}
	}

}
