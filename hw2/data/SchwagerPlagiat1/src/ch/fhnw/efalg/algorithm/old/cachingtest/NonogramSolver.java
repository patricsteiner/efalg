package Schwager.src.ch.fhnw.efalg.algorithm.old.cachingtest;

import java.util.Observable;

/**
 * This class was a test if there is the possibility to safe already calculated permutations.
 * 3GB ram isn't enough for the big nonogram.
 * @author Sandro
 *
 */
public class NonogramSolver extends Observable {
//	private Nonogram nonogram;
//
//	public NonogramSolver(Nonogram nonogram) {
//		this.nonogram = nonogram;
//	}
//
//	/**
//	 * Solves the nonogram.
//	 * The following steps are done by this function: 
//	 * <ol>
//	 * <li>Find all possible permutation in X direction<br><small>We safe these because we don't want to find it every time again</small></li>
//	 * <li>Find fields that are the same in every permutation</li>
//	 * <li>Find all possible permutation in Y direction<br><small>We safe these because we don't want to find it every time again</small></li>
//	 * <li>If we had values that could be placed on the field, we try step 1 again, considering the already placed fields in the field</li>
//	 * </ol>
//	 */
//	public void startSolving() {
//		
//		fillAllFixedValues(nonogram.getField(), false);
//		// TODO do-while loop iterates 1 time too much through X&Y loop if X or Y loop found solution but other loop didn't had changes because of it.
//		// XXX in first iteration Y loop should still search for solution even if X loop didn't had changes. 
//		
//		// Sort the xValues so the biggest (sum(blocksize)+(no_blocks-1)) will be evaluated first. (Most likely to find correct position)
//		// We sort in this step because now we have definitely correct fields already placed and filtered out.
//		
//		PriorityQueue<NonogramValues> sortedXValues = new PriorityQueue<NonogramValues>();
//		for(NonogramValues v : nonogram.getValuesX())
//			if(v.getPermutations().size() != 1)
//				sortedXValues.offer(v);
//		
//		//trySolvePermutation(sortedXValues, nonogram.getCurrentField());
//
//		setChanged();
//		notifyObservers();
//	}
//
//	private boolean fillAllFixedValues(int[][] board, boolean caching) {
//		boolean foundSolution = false;
//		do {
//			foundSolution = false;
//			// Find permutations in X direction
//			// Add matching permutation to field
//			for (int i = 0; i < nonogram.getValuesX().length; i++) {
//				NonogramValues v = nonogram.getValuesX()[i];
//				
//				// Check if we are already finished with this column
//				if (v.getPermutations() == null || v.getPermutations().size() != 1) {
//					
//					// Get all permutations that are still possible
//					// With caching activated we keep them filtered (Not useful with backtracking)
//					Stream<int[]> s = v.filterPermutations(board, caching);
//					int[] fixedFields = findFixedFields(s);
//					if(fixedFields == null)
//						return false; // No solution for this board
//					foundSolution = placeFields(board, fixedFields, i, true) || foundSolution;
//				} else {
//					foundSolution = placeFields(board, v.getPermutations().get(0), i, true) || foundSolution;
//				}
//			}
//			if(NonogramStarter.UPDATE_CHANGES) {
//				setChanged();
//				notifyObservers();
//			}
//
//			// Find permutations in Y direction
//			// Add matching permutation to field
//			for (int i = 0; i < nonogram.getValuesY().length; i++) {
//				NonogramValues u = nonogram.getValuesY()[i];
//
//				// Check if we are already finished with this column
//				if (u.getPermutations() == null || u.getPermutations().size() != 1) {
//					
//					// Get all permutations that are still possible
//					// With caching activated we keep them filtered (Not useful with backtracking)
//					Stream<int[]> s = u.filterPermutations(board, caching);
//					int[] fixedFields = findFixedFields(s);
//					if(fixedFields == null)
//						return false; // No solution for this board
//					foundSolution = placeFields(board, fixedFields, i, false) || foundSolution;
//				} else {
//					foundSolution = placeFields(board, u.getPermutations().get(0), i, false) || foundSolution;
//				}
//			}
//			if(NonogramStarter.UPDATE_CHANGES) {
//				setChanged();
//				notifyObservers();
//			}
//		} while (foundSolution); // Do it as long as we found a solution in any direction
//		
//		// Placed everything that was possible
//		return true;
//	}
//
//	private int[] findFixedFields(Stream<int[]> s) {
//		return s.reduce(null, (a, e) -> {
//			if(a == null) {
//				a = Arrays.copyOf(e, e.length);
//				return a;
//			}
//			
//			for(int i = 0; i < a.length; i++)
//				if(a[i] != e[i])
//					a[i] = 0;
//			return a;
//		});
////		List<int[]> l = s.collect(Collectors.toList());
////		int[] res = null;
////		for(int i = 0; i < l.size(); i++) {
////			if(res == null)
////				res = new int[l.get(i).length];
////			for(int j = 0; j < res.length; j++)
////				if(l.get(i)[j] + res[j] != 0)
////					res[j] = l.get(i)[j];
////				else
////					res[j] = 0;
////		}
////		return res;
//	}
//
//	private boolean trySolvePermutation(PriorityQueue<NonogramValues> sortedXValues, int[][] board) {
//		int[][] boardCopy = new int[nonogram.getValuesY().length][nonogram.getValuesX().length];
//		copyBoard(board, boardCopy);
//		NonogramValues v = sortedXValues.poll();
//		for(int[] permutation : v.getPermutations()) {
//			placeFields(board, permutation, v.getIndex(), v.isXAxis());
//			// place on board
//			if(fillAllFixedValues(board, false)) {// current permutation has possible permutations for the others
//				if(solved()) // we did solve the nonogram by placing the fixed values
//					return true;
//				else // we place the next permutation
//					trySolvePermutation(sortedXValues, board);
//			}
//			copyBoard(boardCopy, board);
//		}
//		return false;
//	}
//
//	private boolean placeFields(int[][] board, int[] perm, int index, boolean xAxis) {
//		boolean hasChanged = false;
//		for(int i = 0; i < perm.length; i++) {
//			if(xAxis && board[i][index] != perm[i]) {
//				hasChanged = true;
//				board[i][index] = perm[i];
//			} else if(!xAxis && board[index][i] != perm[i]) {
//				hasChanged = true;
//				board[index][i] = perm[i];
//			}
//		}
//		return hasChanged;
//	}
//
//	private void copyBoard(int[][] from, int[][] to) {
//		for(int i = 0; i < from.length; i++)
//			to[i] = Arrays.copyOf(from[i], from[i].length);
//	}
//
//	/**
//	 * If all X Values have a single permutation left, the nonogram is solved.
//	 * <small>We could also check the Y direction or always the direction with less values but we don't safe that much time</small>
//	 * @return
//	 */
//	public boolean solved() {
//		for(NonogramValues v :  nonogram.getValuesX())
//			if(v.getPermutations().size() != 1)
//				return false;
//		return true;
//	}
}
