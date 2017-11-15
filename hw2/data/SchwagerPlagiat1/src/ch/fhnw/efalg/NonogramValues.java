package Schwager.src.ch.fhnw.efalg;

import java.util.Arrays;

/**
 * Describes the values of a row or col. (Defined by xAxis) This class is also
 * used to generate valid permutations for a board.
 * 
 * @author Sandro
 *
 */
public class NonogramValues implements Comparable<NonogramValues> {
	/**
	 * The values given by the input file
	 */
	private final int[] values;
	/**
	 * The index of the col or row on the board
	 */
	private final int index;
	/**
	 * Defines if it is a row or a col
	 */
	private final boolean xAxis;
	/**
	 * Defines the starting spaces between the values have to be.<br>
	 * Used to generate next permutation.
	 * 
	 * @see #nextSpacesBetween(int, int[])
	 */
	private final int[] startSpaces;
	/**
	 * Describes how many fields are "fixed".<br>
	 * This is the sum of all values + the length of the values - 1
	 */
	private int size;

	public NonogramValues(int[] values, int fieldSize, int index, boolean xAxis) {
		super();
		this.values = values;
		for (int val : values) {
			this.size += val;
		}
		this.size += values.length - 1;
		this.index = index;
		this.xAxis = xAxis;
		if (values.length == 0)
			startSpaces = new int[1];
		else
			startSpaces = new int[values.length + 1];
		for (int x = 1; x < startSpaces.length - 1; x++)
			startSpaces[x] = 1;
		startSpaces[0] = 0;
		startSpaces[startSpaces.length - 1] = fieldSize - size;
	}

	/**
	 * Returns the values.
	 * 
	 * @see #values
	 * @return
	 */
	public int[] getValues() {
		return values;
	}
	/**
	 * Returns the index.
	 * @see #index
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns if its on the xAxis.
	 * @see #xAxis
	 * @return
	 */
	public boolean isXAxis() {
		return xAxis;
	}

	/**
	 * Returns the size.
	 * @see #size
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the startSpaces between values.
	 * @see #startSpaces
	 * @return
	 */
	public int[] getStartSpaces() {
		return startSpaces;
	}

	/**
	 * Sorts the {@link NonogramValues} according to their {@link #size}.
	 */
	@Override
	public int compareTo(NonogramValues val) {
		if (size - val.size == 0)
			return index - val.getIndex();
		return size - val.size;
	}

	/**
	 * Calculates the next permutation of the spacesBetween array.<br>
	 * Starting value for spacesBetween is {@link #startSpaces}.
	 * 
	 * @param fieldSize
	 * @param spacesBetween
	 * @return
	 */
	public boolean nextSpacesBetween(int fieldSize, int[] spacesBetween) {
		// if first element has all variable whitespaces -> no next permutation
		if (spacesBetween[0] == fieldSize - size)
			return false;
		// if last element has values - move it to the second last.
		if (spacesBetween[spacesBetween.length - 1] > 0) {
			spacesBetween[spacesBetween.length - 1]--;
			spacesBetween[spacesBetween.length - 2]++;
			return true;
		}
		// try to move a single whitespace closer to the beginning
		int i = spacesBetween.length - 2;
		while (i > 0) {
			if (spacesBetween[i] == 1) {
				i--;
			} else {
				spacesBetween[spacesBetween.length - 1] = spacesBetween[i] - 2;
				spacesBetween[i - 1]++;
				spacesBetween[i] = 1;
				return true;
			}
		}
		return false;
	}

	/**
	 * Tries to create a valid permutation out of the spacesBetween by calling
	 * {@link #createValidPermutation(int[], int[], int[])} and safes the values
	 * that keep beeing the same. After that creates the next permutation by
	 * calling {@link #nextSpacesBetween(int, int[])} and
	 * {@link #createValidPermutation(int[], int[], int[])} again.
	 * 
	 * @param currentBoardRow
	 * @return
	 */
	public int[] getFixedFields(int[] currentBoardRow) {
		int[] newBoardRow = null;
		int[] spacesBetween = Arrays.copyOf(startSpaces, startSpaces.length);
		int[] permutation = new int[currentBoardRow.length];
		do {
			if (createValidPermutation(currentBoardRow, permutation, spacesBetween)) {
				if (newBoardRow == null) {
					newBoardRow = Arrays.copyOf(permutation, permutation.length);
				} else {
					for (int i = 0; i < permutation.length; i++)
						if (newBoardRow[i] + permutation[i] == 0)
							newBoardRow[i] = 0;
				}
			}
		} while (nextSpacesBetween(permutation.length, spacesBetween));
		return newBoardRow;
	}

	/**
	 * Creates a row out of the spacesBetween. If the permutation can't be
	 * correct, the method returns false and doesn't finish calculating the
	 * permutation.
	 * 
	 * @param currentBoardRow
	 * @param permutation
	 * @param spacesBetween
	 * @return
	 */
	public boolean createValidPermutation(int[] currentBoardRow, int[] permutation, int[] spacesBetween) {
		int valIndex = 0;
		int whiIndex = 0;
		int count = 0;
		for (int i = 0; i < permutation.length; i++) {
			if (valIndex == whiIndex) {
				if (spacesBetween[whiIndex] == count) {
					if (currentBoardRow[i] == -1)
						return false;
					count = 0;
					permutation[i] = 1;
					whiIndex++;
				} else {
					if (currentBoardRow[i] == 1)
						return false;
					permutation[i] = -1;
				}
			} else {
				if (values[valIndex] == count) {
					if (currentBoardRow[i] == 1)
						return false;
					count = 0;
					permutation[i] = -1;
					valIndex++;
				} else {
					if (currentBoardRow[i] == -1)
						return false;
					permutation[i] = 1;
				}
			}
			count++;
		}
		return true;
	}
}
