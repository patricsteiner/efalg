package ch.fhnw.edu.efalg.graph.edges;

import javax.swing.JTextField;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.EdgePanelHelper;

/**
 * Abstract base class for EdgePanelHelpers.
 * 
 * @author Michel Pluess
 *
 * @param <E>
 */
public abstract class AbstractEdgePanelHelper<E extends Edge> implements EdgePanelHelper<E> {
	private static final long serialVersionUID = 9140751479041146176L;

	/**
	 * Parse int from textfield.
	 * Set to {@link EdgePanelHelper#DEFAULT_INT_VALUE} if parsing
	 * is not possible.
	 * @param textfield
	 * @return parsed int
	 */
	static int parseTextField(JTextField textfield) {
		int nr = DEFAULT_INT_VALUE;
		try {
			nr = Integer.parseInt(textfield.getText());
			if (nr < 0) {
				nr = DEFAULT_INT_VALUE;
			}
		} catch (Exception ex) {}
		
		return nr;
	}
}
