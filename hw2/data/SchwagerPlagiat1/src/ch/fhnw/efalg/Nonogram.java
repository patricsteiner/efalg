package Schwager.src.ch.fhnw.efalg;

/**
 * Describes the nonogram board.
 * @author Sandro
 *
 */
public class Nonogram {
	/**
	 * Describes the nonogram values in x direction
	 */
	private NonogramValues[] valuesX;
	
	/**
	 * Describes the nonogram values in y direction
	 */
	private NonogramValues[] valuesY;
	
	/**
	 * Describes the current field
	 * <ul>
	 * <li>-1 = Empty Field</li>
	 * <li> 0 = Undefined Field</li>
	 * <li> 1 = Filled Field</li>
	 * </ul>
	 */
	private int[][] currentField;
	
	public Nonogram(NonogramValues[] valuesX, NonogramValues[] valuesY) {
		super();
		this.valuesX = valuesX;
		this.valuesY = valuesY;
		this.currentField = new int[valuesY.length][valuesX.length];
	}

	public NonogramValues[] getValuesX() {
		return valuesX;
	}

	public NonogramValues[] getValuesY() {
		return valuesY;
	}

	public int[][] getField() {
		return currentField;
	}
}
