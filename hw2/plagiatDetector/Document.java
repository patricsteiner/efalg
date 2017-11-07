package plagiatDetector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Document {
	
	private String name;
	private List<String> tokens;
	private HashSet<Integer> shingleIds; // using HashSet for O(1) read
	private Tokenizer tokenizer;
	private Shingler shingler;
	private String rawContent;
	
	public Document(String name, String rawContent) {
		this.name = name;
		this.rawContent = rawContent;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRawContent() {
		return rawContent;
	}
	
	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	public void setShingler(Shingler shingler) {
		this.shingler = shingler;
	}
	
	public void prepare() {
		tokens = tokenizer.tokenize(this);
		shingleIds = shingler.makeShinglesAndAddToRepository(this);
	}
	
	public List<String> getTokens() {
		return Collections.unmodifiableList(tokens);
	}
	
	public Set<Integer> getShingleIndices() {
		return Collections.unmodifiableSet(shingleIds);
	}
}
