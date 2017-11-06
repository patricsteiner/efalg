package plagiatDetector;

import java.util.Arrays;
import java.util.List;

public class Shingle {

	private List<String> tokens;
	
	public Shingle(String... tokens) {
		this.tokens = Arrays.asList(tokens); 
	}
}
