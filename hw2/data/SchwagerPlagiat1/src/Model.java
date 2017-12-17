package Schwager.src.ch.fhnw.efalg;

/** MAIN CLASS, author John Doe */
public class Nonogram {

	private NonogramValues[] valuesX;
	

	private NonogramValues[] valuesY;

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
