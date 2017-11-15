package Schwager.src.ch.fhnw.efalg.algorithm.old.cachingtest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @see NonogramSolver
 * @author Sandro
 *
 */
public class NonogramValues implements Comparable<NonogramValues> {
	private int[] values;
	private int size;
	private int index;
	private boolean xAxis;

	// The cache is used in case that there might be a reset - we don't wan't to calculate the permutations newly
	// but just check if the existing ones are possible in this situation
	private List<int[]> permutationCache;

	public NonogramValues(int[] values, int index, boolean xAxis) {
		super();
		this.values = values;
		for(int val : values) {
			this.size += val;
		}
		this.size += values.length-1;
		this.index = index;
		this.xAxis = xAxis;
	}

	public int[] getValues() {
		return values;
	}

	@Override
	public int compareTo(NonogramValues val) {
		return size - val.size;
	}

	public List<int[]> getPermutations() {
		return permutationCache;
	}
	
	public List<int[]> createPermutations(int fieldSize) {
		List<int[]> permutations = new ArrayList<int[]>();
		// Take number of whitespaces
		int[] whitespaces = new int[values.length+1];
		for(int x = 1; x < whitespaces.length-1; x++)
			whitespaces[x] = 1;
		whitespaces[0] = 0;
		whitespaces[whitespaces.length-1] = fieldSize-size;
		
		permutations.add(whitespacesToPermutation(fieldSize, whitespaces));
		// Change number of whitespaces to next permutation - O(n)
		
		while(nextWhitespace(fieldSize, whitespaces)) {
			permutations.add(whitespacesToPermutation(fieldSize, whitespaces));
		}
		return permutations;
	}
	
	private boolean nextWhitespace(int fieldSize, int[] whitespaces) {
		// if first element has all variable whitespaces -> no next permutation
		if(whitespaces[0] == fieldSize - size)
			return false;
		if(whitespaces[whitespaces.length-1] > 0) {
			whitespaces[whitespaces.length-1]--;
			whitespaces[whitespaces.length-2]++;
			return true;
		}
		// try to move a single whitespace closer to the beginning
		int i = whitespaces.length-2;
		while(i > 0) {
			if(whitespaces[i] == 1) {
				i--;
			} else {
				whitespaces[whitespaces.length-1] = whitespaces[i] - 2;
				whitespaces[i-1]++;
				whitespaces[i] = 1;
				return true;
			}
		}
		return false;
	}

	private int[] whitespacesToPermutation(int fieldSize, int[] whitespaces) {
		int[] permutation = new int[fieldSize];
		int valIndex = 0;
		int whiIndex = 0;
		int count = 0;
		for(int x = 0; x < fieldSize; x++) {
			if(valIndex == whiIndex) {
				if(whitespaces[whiIndex] == count) {
					count = 0;
					permutation[x] = 1;
					whiIndex++;
				} else {
					permutation[x] = -1;
				}
			} else {
				if(values[valIndex] == count) {
					count = 0;
					permutation[x] = -1;
					valIndex++;
				} else {
					permutation[x] = 1;
				}
			}
			count++;
		}
		return permutation;
	}

	public Stream<int[]> filterPermutations(int[][] board, boolean caching) {
		List<int[]> permutations;
		if(permutationCache != null) {
			permutations = permutationCache;
		} else {
			if(xAxis)
				permutations = createPermutations(board.length);
			else
				permutations = createPermutations(board[0].length);
		}
		Stream<int[]> stillPossiblePermutations = permutations.stream().filter(e -> {
			for(int i = 0; i < e.length; i++)
				if(xAxis && board[i][index] + e[i] == 0)
					return false;
				else if(!xAxis && board[index][i] + e[i] == 0)
					return false;
			return true;
		});
		if(!caching)
			return stillPossiblePermutations;
		
		permutationCache = stillPossiblePermutations.collect(Collectors.toList());
		return permutationCache.stream();
	}

	public int getIndex() {
		return index;
	}

	public boolean isXAxis() {
		return xAxis;
	}
}
