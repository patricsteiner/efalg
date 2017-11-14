package plagiatDetector.metrics;

public class FileCountMetric extends AbstractMetric {

    public FileCountMetric(int value) {
        super(value);
    }

    @Override
    public double getSimilarity(Metric other) {
        return 0;
    }
}
