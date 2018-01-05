import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

public class Square {

    private double centerX, centerY;
    private double size, maxSize;
    private double angle, bestAngle;
    private double growthFactor = 1.001;
    private double tiltDegrees = 1;
    private Polygon polygon;
    private List<Line2D> lines;
    private boolean frozen;

    public Square(Polygon polygon, double centerX, double centerY, double size) {
        if (polygon == null) throw new IllegalArgumentException("polygon must not be null");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        this.polygon = polygon;
        this.centerX = centerX;
        this.centerY = centerY;
        this.size = size;
        calculateLines();
    }

    public void grow() {
        this.size *= growthFactor;
        calculateLines();
    }

    public void shrink() {
        this.size /= growthFactor;
        calculateLines();
    }

    public void growthFactor(double growthFactor) {
        if (growthFactor <= 0) throw new IllegalArgumentException("growthFactor must be > 0");
        this.growthFactor = growthFactor;
    }

    public void tilt() {
        this.angle += tiltDegrees;
        calculateLines();
    }

    public List<Line2D> lines() {
        return lines;
    }

    private void calculateLines() {
        double angleRad = angle * Math.PI / 180;
        Point2D p1 = new Point2D.Double(centerX - size, centerY);
        translate(p1, angleRad);
        Point2D p2 = new Point2D.Double(centerX, centerY + size);
        translate(p2, angleRad);
        Point2D p3 = new Point2D.Double(centerX + size, centerY);
        translate(p3, angleRad);
        Point2D p4 = new Point2D.Double(centerX, centerY - size);
        translate(p4, angleRad);
        lines = Arrays.asList(
                new Line2D.Double(p1, p2),
                new Line2D.Double(p2, p3),
                new Line2D.Double(p3, p4),
                new Line2D.Double(p4, p1)
        );
    }

    private void translate(Point2D point, double angleRad) {
        double tempX = point.getX() - centerX;
        double tempY = point.getY() - centerY;
        double x = centerX + tempX * Math.sin(angleRad) + tempY * Math.cos(angleRad);
        double y = centerY + tempX * Math.cos(angleRad) - tempY * Math.sin(angleRad);
        point.setLocation(x, y);
    }

    public boolean intersects() {
        return polygon.lines().stream().anyMatch(polygonLine -> lines().stream().anyMatch(squareLine -> squareLine.intersectsLine(polygonLine)));
    }

    public boolean canGrow() {
        grow();
        boolean canGrow = !intersects();
        shrink();
        return canGrow;
    }

    public void tiltDegrees(double tiltDegrees) {
        this.tiltDegrees = tiltDegrees;
    }

    public double angle() {
        return angle;
    }

    public double bestAngle() {
        return bestAngle;
    }

    public void bestAngle(double bestAngle) {
        this.bestAngle = bestAngle;
    }

    public double size() {
        return size;
    }

    public double maxSize() {
        return maxSize;
    }

    public void maxSize(double maxSize) {
        this.maxSize = maxSize;
    }

    public void freeze() {
        frozen = true;
        size = maxSize;
        angle = bestAngle;
    }

    public boolean frozen() {
        return frozen;
    }

}
