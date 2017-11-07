package plagiatDetector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessor {
	
	StringBuilder stringBuilder;
	
	public String preprocess(Document document) {
		stringBuilder = new StringBuilder(document.getRawContent());
		removeComments();
		removeModifiers();
		return stringBuilder.toString();
	}
	
	private void removeComments() {
		Matcher matcher = Pattern.compile("#(//.*\n)#").matcher(stringBuilder);
		while (matcher.find()) {
			stringBuilder.replace(matcher.start(), matcher.end(), "");
		}
	}
	
	private void removeModifiers() {
		Matcher matcher = Pattern.compile("//.*\\n").matcher(stringBuilder);
		while (matcher.find()) {
			stringBuilder.replace(matcher.start(), matcher.end(), "");
		}
	}
}
