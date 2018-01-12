import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A square is defined by a center point, a size and an angle.
 * Size is the length of the diagonal.
 * Anlge = 0: Square stands on the edge, like this <>
 * Angle = 45: Square stands on the border like this []
 */
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

    public double angle() {
        return angle;
    }

    public void angle(double angle) {
        this.angle = angle;
        lines = calculateLines(this.size, angle);
    }

    public void center(double x, double y) {
        centerX = x;
        centerY = y;
        lines = calculateLines(this.size, angle);
    }

    /**
     * Grow as much as possible while not changing the center point. Size and angle will be reset if square leaves the polygon.
     */
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

    /**
     * Calculate the effective lines regarding the position, size and angle of the Square.
     *
     * @param size
     * @param angle
     * @return lines that can be used to check for intersection with the polygon or drawn to the GUI.
     */
    private List<Line2D> calculateLines(double size, double angle) {
        double angleRad = angle * Math.PI / 180;
        Point2D p1 = new Point2D.Double(centerX - size/2, centerY);
        translate(p1, angleRad);
        Point2D p2 = new Point2D.Double(centerX, centerY + size/2);
        translate(p2, angleRad);
        Point2D p3 = new Point2D.Double(centerX + size/2, centerY);
        translate(p3, angleRad);
        Point2D p4 = new Point2D.Double(centerX, centerY - size/2);
        translate(p4, angleRad);
        return Arrays.asList(
                new Line2D.Double(p1, p2),
                new Line2D.Double(p2, p3),
                new Line2D.Double(p3, p4),
                new Line2D.Double(p4, p1)
        );
    }

    /**
     * Translate a point around the center of the square by the given angle in radians.
     *
     * @param point
     * @param angleRad
     */
    private void translate(Point2D point, double angleRad) {
        double tempX = point.getX() - centerX;
        double tempY = point.getY() - centerY;
        double x = centerX + tempX * Math.sin(angleRad) + tempY * Math.cos(angleRad);
        double y = centerY + tempX * Math.cos(angleRad) - tempY * Math.sin(angleRad);
        point.setLocation(x, y);
    }

    public String coordinates() {
        return lines.stream().map(line -> String.format("(%.2f, %.2f)", line.getX1(), line.getY1())).collect(Collectors.toList()).toString();
    }

}
