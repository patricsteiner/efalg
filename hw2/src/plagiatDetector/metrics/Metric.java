package plagiatDetector.metrics;
//TODO
public interface Metric {
	public int getValue();
	
	public double getSimilarity(Metric other);
}
