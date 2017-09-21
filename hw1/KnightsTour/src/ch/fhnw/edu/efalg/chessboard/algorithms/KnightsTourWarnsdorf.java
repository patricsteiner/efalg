package ch.fhnw.edu.efalg.chessboard.algorithms;

import ch.fhnw.edu.efalg.chessboard.ChessBoard;
import ch.fhnw.edu.efalg.chessboard.Position;

import java.util.*;

/**
 * More sophisticated implementation of the algorithm to find a knight's tour using backtracking.
 * This algorithm uses the heuristics given by Warnsdorf. Instead of just choosing any possible move, this algorithm
 * always chooses the move with the least possible next moves first.
 *
 * @author Patric Steiner
 */
public final class KnightsTourWarnsdorf extends KnightsTourNoHeuristic {

    /**
     * Finds all possible moves from the current situation given by board and pos and makes sure to order the returned
     * Collection according to Wandorf's rule.
     *
     * @param board chess board to search
     * @param pos current position
     * @return a Stack<Position> containing all possible moves ordered by the least next possible moves.
     */
    protected Stack<Position> findPossibleMoves(final ChessBoard board, final Position pos) {
        // First just find all the moves in no particular order
        Stack<Position> possibleMoves = super.findPossibleMoves(board, pos);
        // Then use a Queue that stores the moves according to their number of possible next moves. Map.Entry is used
        // just to avoid programming a specific Pair class and allows to easily put to items together.
        Queue<Map.Entry<Position, Integer>> possibleMovesRanked = new PriorityQueue<>((e1, e2) -> e2.getValue() - e1.getValue());
        while (!possibleMoves.empty()) {
            Position p = possibleMoves.pop();
            // for each possible move, use the super.findPossibleMoves method to calculate the amount of respective next
            // possible moves. Offer this to the Queue, which will automatically position it on the correct spot.
            possibleMovesRanked.offer(new AbstractMap.SimpleEntry<Position, Integer>(p, super.findPossibleMoves(board, p).size()));
        }
        // When all the moves are in in the Queue, just push them back in the Stack one by one, which then will be
        // ordered according to Wandorfs rule.
        while (!possibleMovesRanked.isEmpty())
            possibleMoves.push(possibleMovesRanked.poll().getKey());

        return possibleMoves;
	}
}
