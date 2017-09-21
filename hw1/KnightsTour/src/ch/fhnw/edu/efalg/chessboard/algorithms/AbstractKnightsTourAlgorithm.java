package ch.fhnw.edu.efalg.chessboard.algorithms;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import ch.fhnw.edu.efalg.chessboard.ChessBoard;
import ch.fhnw.edu.efalg.chessboard.ChessBoardAlgorithm;
import ch.fhnw.edu.efalg.chessboard.Position;
import ch.fhnw.edu.efalg.chessboard.impl.KnightsTourField;
import ch.fhnw.edu.efalg.chessboard.impl.PositionImpl;

/**
 * Abstract base class for knight's tour algorithms.
 * 
 * @author Martin Schaub
 */
public abstract class AbstractKnightsTourAlgorithm implements ChessBoardAlgorithm {

	/**
	 * Possible moves from the knight play figure.
	 * 
	 * MOVES[i][0] = x diff and MOVES[i][1] = y diff
	 */
	protected static final int[][] MOVES = { { 2, 1 }, { 1, 2 }, { -1, 2 }, { -2, 1 }, { -2, -1 }, { -1, -2 }, { 1, -2 },
			{ 2, -1 } };

	/**
	 * Has the algorithm been stopped.
	 */
	private boolean hasBeenStopped = false;

	/**
	 * Has the algorithm been requested to stop?
	 * @return true, if the algorithm needs to be stopped and false otherwise.
	 */
	protected boolean hasBeenStopped() {
		return hasBeenStopped;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.fhnw.edu.efficientalgorithms.knightstour.ChessBoardAlgorithm#execute(ch.fhnw.edu.efficientalgorithms.knightstour
	 * .ChessBoard, ch.fhnw.edu.efficientalgorithms.knightstour.Position)
	 */
	@Override
	public void execute(final ChessBoard board, final Position startPos) {
		// Initialize the field
		for (int i = 0; i < board.getNumOfRows(); ++i) {
			for (int j = 0; j < board.getNumOfColumns(); ++j) {
				board.setField(new PositionImpl(i, j), new KnightsTourField());
			}
		}

		// Execute the algorithm
		findKnightTour(board, startPos);

		// Test the result.
		if (!isSolution(board) && !hasBeenStopped) {
			JOptionPane.showMessageDialog(null, "The chessboard with this start position has no knight's tour.",
					"No Solution was found!", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Searches a knight's tour.
	 * 
	 * @param board chess board to search
	 * @param startPos start position
	 */
	protected abstract void findKnightTour(final ChessBoard board, final Position startPos);
	
	/**
	 * Checks if the solution is valid.
	 * 
	 * @param board chessboard to check
	 * @return true, if all numbers occur only once in the board and the chessboard is finished
	 */
	protected abstract boolean isSolution(final ChessBoard board); 

	/**
	 * Gets the selected field form the board. The only additional thing to board's method is, that its automatically
	 * casted.
	 * 
	 * @param board board to get the field from
	 * @param pos field's position.
	 * @return the field
	 */
	protected KnightsTourField getField(final ChessBoard board, final Position pos) {
		return (KnightsTourField) board.getField(pos);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.fhnw.edu.efficientalgorithms.knightstour.ChessBoardAlgorithm#stop()
	 */
	@Override
	public void stop() {
		hasBeenStopped = true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
