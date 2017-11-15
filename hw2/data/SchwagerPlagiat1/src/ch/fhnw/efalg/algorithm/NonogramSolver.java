package Schwager.src.ch.fhnw.efalg.algorithm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Observable;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import Schwager.src.ch.fhnw.efalg.Nonogram;
import Schwager.src.ch.fhnw.efalg.NonogramStarter;
import Schwager.src.ch.fhnw.efalg.NonogramValues;

/**
 * Solver for the nonogram with no caching.
 * @author Sandro
 *
 */
public class NonogramSolver extends Observable {
	private Nonogram nonogram;
	private PriorityQueue<NonogramValues> sortedXValues;
	private PriorityQueue<NonogramValues> sortedYValues;

	public NonogramSolver(Nonogram nonogram) {
		this.nonogram = nonogram;
	}

	/**
	 * Solves the nonogram. The following steps are done by this function:
	 * <ol>
	 * <li>Fill in all completely empty rows</li>
	 * <li>Find fields that are the same in every permutation</li><br>
	 * <small>First we try it with the rows with the most fields defined, if
	 * there are no fixed fields left take the next rows with a smaller
	 * requirement<small>
	 * <li>Backtrack the rest of the field</li>
	 * </ol>
	 */
	public void startSolving() {
		// Sort so the one with the most fixed values are first
		// Important for backtracking
		sortedXValues = new PriorityQueue<NonogramValues>(nonogram.getValuesX().length, Collections.reverseOrder());
		for (NonogramValues u : nonogram.getValuesX())
			sortedXValues.add(u);
		sortedYValues = new PriorityQueue<NonogramValues>(nonogram.getValuesY().length, Collections.reverseOrder());
		for (NonogramValues v : nonogram.getValuesY())
			sortedYValues.add(v);
		
		fillEmptyValues(nonogram.getField(), sortedXValues);
		fillEmptyValues(nonogram.getField(), sortedYValues);

		fillFixedValues(nonogram.getField());

		if (!solved())
			trySolvePermutation(nonogram.getField());

		if(NonogramStarter.UPDATE_CHANGES) {
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Try to solve the nonogram by placing fixed values.
	 * <ol>
	 * <li>First we try all rows with less than 15 variable space</li>
	 * <li>After no additional field could be placed, try it with 25 variable
	 * space</li>
	 * <li>After that try every field that might not have to much variable
	 * space</li><br>
	 * <small>We filter it to only check rows with a solvable number of
	 * permutations</small>
	 * </ol>
	 * 
	 * @param board
	 */
	private void fillFixedValues(int[][] board) {
		// Search for fixed values in rows that have at most 15 variable spaces
		while (fillFixedValues(nonogram.getField(), sortedXValues, e -> sortedYValues.size() - e.getSize() < 15)
				| fillFixedValues(nonogram.getField(), sortedYValues, e -> sortedXValues.size() - e.getSize() < 15)) {
			if(NonogramStarter.UPDATE_CHANGES) {
				setChanged();
				notifyObservers();
			}
		}

		// Search for fixed values in rows that have at most 25 variable spaces
		while (fillFixedValues(nonogram.getField(), sortedXValues, e -> sortedYValues.size() - e.getSize() < 25)
				| fillFixedValues(nonogram.getField(), sortedYValues, e -> sortedYValues.size() - e.getSize() < 25)) {
			if(NonogramStarter.UPDATE_CHANGES) {
				setChanged();
				notifyObservers();
			}
		}

		// Searching for fixed values in any position as long as the space that is left isn't 5 times bigger than the field itself. 
		while (fillFixedValues(nonogram.getField(), sortedXValues,
				e -> getConnectedUndefinedFields(getRowFromBoard(board, e)) - e.getSize() < 5)
				| fillFixedValues(nonogram.getField(), sortedYValues,
						e -> getConnectedUndefinedFields(getRowFromBoard(board, e)) - e.getSize() < 5)) {
			if(NonogramStarter.UPDATE_CHANGES) {
				setChanged();
				notifyObservers();
			}
		}
	}

	/**
	 * Return the number of connected 0 fields on the board row.
	 * 
	 * @param rowFromBoard
	 * @return
	 */
	private int getConnectedUndefinedFields(int[] rowFromBoard) {
		int max = 0;
		int count = 0;
		for (int i = 0; i < rowFromBoard.length; i++) {
			if (rowFromBoard[i] == 0)
				count++;
			else {
				if (count > max)
					max = count;
				count = 0;
			}
		}
		return max;
	}

	/**
	 * Fills in all rows that have 0 values in it
	 * @param board
	 * @param values
	 */
	private void fillEmptyValues(int[][] board, Collection<NonogramValues> values) {
		values.parallelStream().filter(e -> e.getValues().length == 0).forEach(e -> {
			setRowFromBoard(board, e, -1);
		});
	}

	/**
	 * Fills all fixed values for the fields in the current row.
	 * 
	 * @param board
	 * @param values
	 * @param minimumSize
	 * @throws NullPointerException if no solution can be found.
	 * @return
	 */
	private boolean fillFixedValues(int[][] board, Collection<NonogramValues> values,
			Predicate<NonogramValues> filter) {
		return values.parallelStream().filter(filter).map(e -> {
			// Get the current row for the values
			int[] currentRow = getRowFromBoard(board, e);
			// Get the fixed values for the current row.
			int[] newRow = e.getFixedFields(currentRow);
			// Set the fixed values
			// Return true if there has been any changes.
			if (setRowFromBoard(board, e, newRow))
				return true;
			return false;
		}).reduce(false, (a, b) -> a || b);
	}

//	private boolean fillFixedValues(int[][] board, Collection<NonogramValues> values) {
//		return values.parallelStream().map(e -> {
//			int[] currentRow = getRowFromBoard(board, e);
//			int[] newRow = e.getFixedFields(currentRow);
//			if (setRowFromBoard(board, e, newRow))
//				return true;
//			return false;
//		}).reduce(false, (a, b) -> a || b);
//	}

	/**
	 * Returns the corresponding row for the given NonogramValues.
	 * 
	 * @param board
	 * @param e
	 * @return int[] with the row in the board.
	 */
	private int[] getRowFromBoard(int[][] board, NonogramValues e) {
		if (!e.isXAxis())
			return board[e.getIndex()];
		int[] copy = new int[board.length];
		for (int i = 0; i < copy.length; i++)
			copy[i] = board[i][e.getIndex()];
		return copy;
	}

	/**
	 * Sets the corresponding row for the given NonogramValues with the given value.
	 * 
	 * @param board
	 * @param e
	 * @param value
	 */
	private void setRowFromBoard(int[][] board, NonogramValues e, int value) {
		if (e.isXAxis()) {
			for (int i = 0; i < board.length; i++) {
				board[i][e.getIndex()] = value;
			}
		} else {
			for (int i = 0; i < board[e.getIndex()].length; i++) {
				board[e.getIndex()][i] = value;
			}
		}
	}

	/**
	 * Sets the corresponding row for the given NonogramValues with the given row.
	 * 
	 * @param board
	 * @param e
	 * @param row
	 */
	private boolean setRowFromBoard(int[][] board, NonogramValues e, int[] row) {
		boolean solutionFound = false;

		if (e.isXAxis()) {
			for (int i = 0; i < row.length; i++) {
				if (row[i] != board[i][e.getIndex()]) {
					solutionFound = true;
					board[i][e.getIndex()] = row[i];
				}
			}
		} else {
			for (int i = 0; i < row.length; i++) {
				if (row[i] != board[e.getIndex()][i]) {
					solutionFound = true;
					board[e.getIndex()][i] = row[i];
				}
			}
		}
		return solutionFound;
	}

	/**
	 * Try to solve the nonogram by testing out permutations.
	 * <ol>
	 * <li>Get the starting spaces between the values. (Used to calculate permutations)</li>
	 * <li>Try to create a valid permutation out of it</li>
	 * <li>- Set the valid permutation on the board</li>
	 * <li>- Try to fill in all fixed values</li>
	 * <li>- If board is not solved, try the next permutation</li>
	 * <li>Change the spaces between the values to create new permutation</li>
	 * </ol>
	 * @param board
	 * @return
	 */
	private boolean trySolvePermutation(int[][] board) {
		// Copy board to be able to reset it.
		int[][] boardCopy = new int[nonogram.getValuesY().length][nonogram.getValuesX().length];
		copyBoard(board, boardCopy);

		// Get the next permutation of the Queue
		NonogramValues v = sortedXValues.poll();

		int[] spacesBetween = Arrays.copyOf(v.getStartSpaces(), v.getStartSpaces().length);
		int[] permutation = new int[board.length];
		int[] boardRow = getRowFromBoard(board, v);

		do {
			if (v.createValidPermutation(boardRow, permutation, spacesBetween)) {
				// Place on board
				setRowFromBoard(board, v, permutation);

				if(NonogramStarter.UPDATE_CHANGES) {
					setChanged();
					notifyObservers();
				}

				try {
					// Fill in fixed values.
					fillFixedValues(board);

					if (solved())
						return true;
					else if (trySolvePermutation(board))
						return true;
				} catch (NullPointerException ex) {
					// While trying to fill the fixed field - no matching
					// permutation could be found.
					// Do nothing here as we change the permutation anyway.
				}
				// Reset board.
				copyBoard(boardCopy, board);
			}
		} while (v.nextSpacesBetween(board.length, spacesBetween));
		
		// Add it back to the Queue.
		sortedXValues.offer(v);
		// No solution found.
		return false;
	}

	/**
	 * No field has 0 on the board.
	 * @return
	 */
	private boolean solved() {
		for (int i = 0; i < nonogram.getField().length; i++)
			for (int j = 0; j < nonogram.getField()[i].length; j++)
				if (nonogram.getField()[i][j] == 0)
					return false;
		return true;
	}

	/**
	 * Copies the board. Uses {@link Arrays#copyOf(int[], int)} to copy efficiently.
	 * @param from
	 * @param to
	 */
	private void copyBoard(int[][] from, int[][] to) {
		for (int i = 0; i < from.length; i++)
			to[i] = Arrays.copyOf(from[i], from[i].length);
	}
}
