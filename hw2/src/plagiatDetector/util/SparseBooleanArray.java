package plagiatDetector.util;

import java.util.HashSet;
import java.util.Set;

/**
 * This is only a pseudo sparse array that just uses a HashSet.
 */
public class SparseBooleanArray {
	private Set<Integer> trueIndices;
	
	public SparseBooleanArray() {
		trueIndices = new HashSet<>();
	}
	
	public boolean get(int index) {
		return trueIndices.contains(index);
	}
	
	public void set(int index, boolean value) {
		if (value) trueIndices.add(index);
		else trueIndices.remove(index);
	}
}
