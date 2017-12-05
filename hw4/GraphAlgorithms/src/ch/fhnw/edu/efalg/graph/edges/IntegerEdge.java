package ch.fhnw.edu.efalg.graph.edges;

/**
 * Weighted edge.
 * 
 * @author Martin Schaub
 */
public final class IntegerEdge extends StandardEdge {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -1980154294799216009L;

	private final int weight;

	public IntegerEdge(final int weight) {
		super(Integer.valueOf(weight).toString());
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

}
