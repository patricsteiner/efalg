package plagiatDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShingleRepository {
	
	private ArrayList<Shingle> shingles;
	private Map<Shingle, Integer> shingleIndices;
	
	public ShingleRepository() {
		shingles = new ArrayList<>();
		shingleIndices = new HashMap<>();
	}
	
	public int add(Shingle shingle) {
		if (shingleIndices.containsKey(shingle)) return shingleIndices.get(shingle);	
		shingles.add(shingle);
		return shingleIndices.put(shingle, shingles.size() - 1); // -1 because we want to start at index 0
	}
	
	public boolean contains(Shingle shingle) {
		return shingleIndices.containsKey(shingle);
	}
	
	public Shingle findByIndex(int index) {
		return shingles.get(index);
	}
	
	public int getSize() {
		return shingles.size();
	}
}
