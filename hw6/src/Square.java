import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

public class Square {

    private double centerX, centerY;
    private double size;
    private double angle;
    private Polygon polygon;
    private List<Line2D> lines;

    public Square(Polygon polygon, double centerX, double centerY, double size) {
        if (polygon == null) throw new IllegalArgumentException("polygon must not be null");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        this.polygon = polygon;
        this.size = size;
        center(centerX, centerY);
    }

    public List<Line2D> lines() {
        return lines;
    }

    public double size() {
        return size;
    }

    public void center(double x, double y) {
        centerX = x;
        centerY = y;
        lines = calculateLines(this.size, angle);
    }

    public void rotateAndGrow() {
        if (!polygon.contains(centerX, centerY)) {
            size = 0.0001;
            angle = 0;
            return;
        }
        double tmpSize = size;
        double tmpAngle = 0;
        while (tmpAngle < 90) {
            List<Line2D> tmpLines = calculateLines(tmpSize, tmpAngle);
            while (!polygon.intersects(tmpLines)) {
                size = tmpSize;
                angle = tmpAngle;
                lines = tmpLines;
                tmpSize *= 1.001;
                tmpLines = calculateLines(tmpSize, tmpAngle);
            }
            tmpAngle++;
        }
    }

    private List<Line2D> calculateLines(double size, double angle) {
        double angleRad = angle * Math.PI / 180;
        Point2D p1 = new Point2D.Double(centerX - size, centerY);
        translate(p1, angleRad);
        Point2D p2 = new Point2D.Double(centerX, centerY + size);
        translate(p2, angleRad);
        Point2D p3 = new Point2D.Double(centerX + size, centerY);
        translate(p3, angleRad);
        Point2D p4 = new Point2D.Double(centerX, centerY - size);
        translate(p4, angleRad);
        return Arrays.asList(
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

}
