package plagiatDetector.metrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//TODO
public class ImportCountMetric implements Metric {

	@Override
	public double getSimilarity(Metric other) {
		double similarity = (double) getValue() / other.getValue();
		return similarity > 1 ? 1/similarity : similarity;
	}

	@Override
	public int getValue() {
		Matcher machter = Pattern.compile("import ").matcher("");
		int counter = 0;
		while (machter.find()) {
			counter++;
		}
		return counter;
	}
}
