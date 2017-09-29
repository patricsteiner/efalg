package ch.fhnw.edu.efalg.chessboard.algorithms;

import ch.fhnw.edu.efalg.chessboard.ChessBoard;
import ch.fhnw.edu.efalg.chessboard.Position;
import ch.fhnw.edu.efalg.chessboard.impl.KnightsTourField;
import ch.fhnw.edu.efalg.chessboard.impl.PositionImpl;

import java.util.*;

/**
 * Naive implementation of an algorithm to find a knight's tour using backtracking.
 * No Heuristics are used.
 * 
 * IMPORTANT: Java VM Stack size may need to be increased to avoid a stackoverflow!
 *
 * @author Patric Steiner
 */
public class KnightsTourNoHeuristic extends AbstractKnightsTourAlgorithm {

	/**
	 * Use class variable to store board, so we can avoid passing it as argument in each call.	
	 */
	protected ChessBoard board;
	
	/**
	 * Variable to keep track of the amount of fields that have been visited.
	 */
	protected int visitedFields;

	/**
	 * Searches a knight's tour.
	 *
	 * @param board chess board to search
	 * @param startPos start position
	 */
	@Override
	protected void findKnightTour(final ChessBoard board, final Position startPos) {
		// set the first field on the board according to startPos and then run the backtracking-algorithm.
		visitedFields = 0;
		this.board = board;
		board.setField(startPos, new KnightsTourField(visitedFields++));
		findSolution(startPos);
	}

	/**
	 * Checks if the current state of the board is a solution. To make the process more performant, instead of iterating
	 * through the whole board, this method	simply checks if the variable 'visitedFields' equals the amount of fields on
	 * the board.
	 *
	 * @param board chessboard to check
	 * @return true if all fields of the board have been visited, false otherwise.
	 */
	@Override
	protected boolean isSolution(final ChessBoard board) {
		return visitedFields == board.getNumOfColumns() * board.getNumOfRows();
	}

	/**
	 * Helper method that tries to find a solution by using backtracking.
	 *
	 * @param pos current position
	 * @return true if a solution was found or the algorithm has been stopped by the user, false otherwise.
	 */
	protected boolean findSolution(final Position pos) {
		// if the user has stopped the algorithm, return true just to make sure the algorithm immediately stops.
		if (hasBeenStopped() || isSolution(board)) return true;
		// from the current position, find all possible moves and try every one of them
		Stack<Position> possibleMoves = findPossibleMoves(pos);
		while (possibleMoves.size() > 0) {
			Position nextPos = possibleMoves.pop();
			// get the first move out of the possibilities and adjust the board and counter accordingly
			board.setField(nextPos, new KnightsTourField(visitedFields++));
			// recursively call this function again with one step advanced. If a solution is found, we are done and return true.
			if (findSolution(nextPos)) return true;
			// if this move does not lead to a solution, undo the adjustments and try the next move.
			visitedFields--;
			board.setField(nextPos, new KnightsTourField());
		}
		// if none of the possible moves lead to a solution, return false and thus backtrack.
		return false;
	}

	/**
	 * Finds all possible moves from the current situation given by board and pos.
	 *
	 * @param pos current position
	 * @return a Stack<Position> containing all possible moves in no particular order.
	 */
	protected Stack<Position> findPossibleMoves(final Position pos) {
		Stack<Position> possibleMoves = new Stack<>();
		// try all the moves a knight can do
		for (int i = 0; i < MOVES.length; i++) {
			int row = MOVES[i][0] + pos.getRow();
			int col = MOVES[i][1] + pos.getColumn();
			// if the move does not result outside of the board
			if (col >= 0 && row >= 0 && col < board.getNumOfColumns() && row < board.getNumOfRows()) {
				Position newPos = new PositionImpl(row, col);
				// if the move does not end on a field that was already visited
				if (!getField(board, newPos).isVisited() && !newPos.equals(pos)) {
					// add this move to the stack
					possibleMoves.push(newPos);
				}
			}
		}
		return possibleMoves;
	}
}
