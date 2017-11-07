package plagiatDetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Shingler {
	
	public final int k;
	private ShingleRepository shingleRepository;
	
	public Shingler(int k, ShingleRepository shingleRepository) {
		if (k < 1) throw new IllegalArgumentException("k must be > 0");
		this.k = k;
		this.shingleRepository = shingleRepository;
	}
	
	public Collection<Integer> makeShinglesAndAddToRepository(Document document) {
		Collection<Integer> shingleIndices = new ArrayList<>();
		for (int i = 0; i < document.getTokens().size() - k; i++) {
			String tokens[] = new String[k];
			for (int j = 0; j < k; j++) {
				tokens[j] = document.getTokens().get(i + j);
			}
			shingleIndices.add(shingleRepository.add(new Shingle(tokens)));
		}
		return shingleIndices;
	}
}
