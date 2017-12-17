package plagiatDetector.util;

import plagiatDetector.repositories.ShingleRepository;

import java.util.HashSet;
import java.util.List;

/**
 * Util class that produces Shingles from given Tokens and stores the Shingles in the ShingleRepository.
 */
public class Shingler {
	
	private final int tokensPerShingle;
	private ShingleRepository shingleRepository;
	
	public Shingler(int tokensPerShingle, ShingleRepository shingleRepository) {
		if (tokensPerShingle < 1) throw new IllegalArgumentException("tokensPerShingle must be > 0");
		this.tokensPerShingle = tokensPerShingle;
		this.shingleRepository = shingleRepository;
	}
	
	public HashSet<Integer> makeShinglesAndAddToRepository(List<String> tokens) {
		HashSet<Integer> shingleIds = new HashSet<>();
		for (int i = 0; i < tokens.size() - tokensPerShingle; i++) {
			String tokensInShingle[] = new String[tokensPerShingle];
			for (int j = 0; j < tokensPerShingle; j++) {
				tokensInShingle[j] = tokens.get(i + j);
			}
			shingleIds.add(shingleRepository.add(new Shingle(tokensInShingle)));
		}
		return shingleIds;
	}
}
