package ch.fhnw.edu.efalg.graph;

import java.io.Serializable;

import javax.swing.JPanel;

/**
 * Contains methods to
 * - create a JPanel so the
 *   user can specify the details of an edge he
 *   wants to create
 * - create an Edge from the data entered in the JPanel
 * @author Michel Pluess
 *
 * @param <E> edge type
 */
public interface EdgePanelHelper<E extends Edge> extends Serializable {
	static final String EDGE_DETAILS_PANEL_TITLE = "Edge Details";
	
	/**
	 * Edge panel grid layout horizontal gap.
	 */
	static final int HGAP = 5;
	
	/**
	 * Edge panel grid layout vertical gap.
	 */
	static final int VGAP = 5;
	
	/**
	 * Default value for parsed textfields.
	 */
	static final int DEFAULT_INT_VALUE = Integer.MIN_VALUE;
	
	/**
	 * Create an edge panel where a user can define the specific
	 * data for this edge type.
	 * @return
	 */
	public JPanel createEdgeDetailsPanel();
	
	/**
	 * Create an edge object from the data supplied by the user
	 * in the edge panel.
	 * @param panel
	 * @param edgeFactory
	 * @return edge
	 */
	public E createEdgeFromPanel(JPanel panel, EdgeFactory<E> edgeFactory);
}
