package plagiatDetector;

import java.util.Arrays;
import java.util.List;

public class Tokenizer {
	public List<String> tokenize(Document document) {
		return Arrays.asList(document.getRawContent().split("[ \\n\\r\\n\\t]+"));
	}
}
