package plagiatDetector.models;

import plagiatDetector.metrics.Metric;
import plagiatDetector.util.Preprocessor;
import plagiatDetector.util.Shingler;
import plagiatDetector.util.Tokenizer;

import java.util.*;

public class Document {
	
	private String name;
	private List<String> tokens;
	private HashSet<Integer> shingleIds; // using HashSet for O(1) read
	private Preprocessor preprocessor;
	private Tokenizer tokenizer;
	private Shingler shingler;
	private String rawContent;
	private String processedContent;
	private List<Metric> metrics;

	public Document(String name, String rawContent, Preprocessor preprocessor, Tokenizer tokenizer, Shingler shingler) {
		this.name = name;
		this.preprocessor = preprocessor;
		this.tokenizer = tokenizer;
		this.shingler = shingler;
		this.rawContent = rawContent;
		this.processedContent = preprocessor.preprocess(rawContent);
		this.tokens = tokenizer.tokenize(processedContent);
		this.shingleIds = shingler.makeShinglesAndAddToRepository(tokens);
		this.metrics = new ArrayList<>();
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
	
	public List<String> getTokens() {
		return Collections.unmodifiableList(tokens);
	}
	
	public Set<Integer> getShingleIndices() {
		return Collections.unmodifiableSet(shingleIds);
	}

	public void addMetric(Metric metric) {
		metrics.add(metric);
	}
}
