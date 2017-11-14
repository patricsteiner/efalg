package plagiatDetector.metrics;

public abstract class AbstractMetric implements Metric {
    private int value;

    public AbstractMetric(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

}
