package plagiatDetector.metrics;

public class FolderCountMetric extends AbstractMetric {

    public FolderCountMetric(int value) {
        super(value);
    }

    @Override
    public double getSimilarity(Metric other) {
        return 0;
    }
}
