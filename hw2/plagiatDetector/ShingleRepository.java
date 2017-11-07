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
		int currentIndex = shingles.size();
		shingles.add(shingle);
		shingleIndices.put(shingle, currentIndex);
		return currentIndex;
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
