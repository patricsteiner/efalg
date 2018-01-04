import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

public class Polygon {

    private javafx.scene.shape.Polygon fxPolygon;
    private List<Point2D> points;
    private List<Line2D> lines;
    private double minX, maxX;
    private double minY, maxY;

    public Polygon(List<Point2D> points) {
        this.points = points;
        minX = points.stream().map(Point2D::getX).sorted(Double::compareTo).findFirst().get();
        minY = points.stream().map(Point2D::getY).sorted(Double::compareTo).findFirst().get();
        maxX = points.stream().map(Point2D::getX).sorted((a, b) -> -a.compareTo(b)).findFirst().get();
        maxY = points.stream().map(Point2D::getY).sorted((a, b) -> -a.compareTo(b)).findFirst().get();
        lines = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            lines.add(new Line2D.Double(points.get(i), points.get(i+1)));
        }
        lines.add(new Line2D.Double(points.get(points.size() - 1), points.get(0)));
        fxPolygon = new javafx.scene.shape.Polygon(points.stream().flatMapToDouble(p -> DoubleStream.of(p.getX(), p.getY())).toArray());
    }

    public javafx.scene.shape.Polygon fxPolygon() {
        return fxPolygon;
    }

    public List<Line2D> lines() {
        return lines;
    }

    public double minX() {
        return minX;
    }

    public double maxX() {
        return maxX;
    }

    public double maxY() {
        return maxY;
    }

    public double minY() {
        return minY;
    }

    public double width() {
        return maxX - minX;
    }

    public double height() {
        return maxY - minY;
    }

}
