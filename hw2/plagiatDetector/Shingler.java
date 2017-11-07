package plagiatDetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Shingler {
	
	private final int tokensPerShingle;
	private ShingleRepository shingleRepository;
	
	public Shingler(int tokensPerShingle, ShingleRepository shingleRepository) {
		if (tokensPerShingle < 1) throw new IllegalArgumentException("tokensPerShingle must be > 0");
		this.tokensPerShingle = tokensPerShingle;
		this.shingleRepository = shingleRepository;
	}
	
	public HashSet<Integer> makeShinglesAndAddToRepository(Document document) {
		HashSet<Integer> shingleIds = new HashSet<>();
		for (int i = 0; i < document.getTokens().size() - tokensPerShingle; i++) {
			String tokens[] = new String[tokensPerShingle];
			for (int j = 0; j < tokensPerShingle; j++) {
				tokens[j] = document.getTokens().get(i + j);
			}
			shingleIds.add(shingleRepository.add(new Shingle(tokens)));
		}
		return shingleIds;
	}
}
