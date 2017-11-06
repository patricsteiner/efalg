package plagiatDetector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Document {
	private List<String> tokens;
	private Collection<Integer> shinlgeIndices;
	
	public Document(String rawContent) {
		tokens = tokenize(rawContent);
	}
	
	private List<String> tokenize(String rawContent) {
		return Arrays.asList(rawContent.split(" "));
	}
	
	public List<String> getTokens() {
		return (List<String>) Collections.unmodifiableCollection(tokens);
	}
	
	public void setShingleIndices(Collection<Integer> shingleIndices) {
		//TODO shinlgeIndices = shingler.makeShingles(this);
	}
	
	public Collection<Integer> getShinlgeIndices() {
		return Collections.unmodifiableCollection(shinlgeIndices);
	}
}
