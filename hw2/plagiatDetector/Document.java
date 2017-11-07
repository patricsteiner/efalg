package plagiatDetector;

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
	private Preprocessor preprocessor;
	private String rawContent;
	private String processedContent;
	
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
	
	public String getProcessedContent() {
		return processedContent;
	}
	
	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	public void setShingler(Shingler shingler) {
		this.shingler = shingler;
	}
	
	public void setPreprocessor(Preprocessor preprocessor) {
		this.preprocessor = preprocessor;
	}
	
	public void prepare() {
		processedContent = preprocessor.preprocess(this);
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
