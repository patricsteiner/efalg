package plagiatDetector;

public interface Metric {
	public int getValue();
	
	public double getSimilarity(Metric other);
}
