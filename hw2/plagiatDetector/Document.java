package plagiatDetector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Document {
	private List<String> tokens;
	private Collection<Integer> shingleIndices;
	
	public Document(String rawContent) {
		tokens = tokenize(rawContent);
	}
	
	private List<String> tokenize(String rawContent) {
		//rawContent = rawContent.replaceAll("", " ");
		return Arrays.asList(rawContent.split("[ \\n\\r\\n\\t]+"));
	}
	
	public List<String> getTokens() {
		return Collections.unmodifiableList(tokens);
	}
	
	public void setShingleIndices(Shingler shingler) {
		shingleIndices = shingler.makeShinglesAndAddToRepository(this);
	}
	
	public Collection<Integer> getShingleIndices() {
		return Collections.unmodifiableCollection(shingleIndices);
	}
}
