import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Square {

    private double centerX, centerY;
    private double size;
    private double angle;
    private double bestCenterX, bestCenterY;
    //double x1, x2, x3, x4, y1, y2, y3, y4;
    private boolean frozen;
    private double growthFactor = 1.001;
    private double tiltDegrees = 1;
    private Polygon polygon;

    public Square(Polygon polygon, double centerX, double centerY, double size) {
        if (polygon == null) throw new IllegalArgumentException("polygon must not be null");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        this.polygon = polygon;
        this.centerX = centerX;
        this.centerY = centerY;
        this.size = size;
    }

    public void grow() {
        this.size *= growthFactor;
    }

    public void shrink() {
        this.size /= growthFactor;
    }

    public void setGrowthFactor(double growthFactor) {
        if (growthFactor <= 0) throw new IllegalArgumentException("growthFactor must be > 0");
        this.growthFactor = growthFactor;
    }

    public boolean tilt() {
        while (angle < 90) {
            this.angle += tiltDegrees;
            if (canGrow()) return true;
        }
        return false;
    }

    public List<Line2D> lines() {
        double tempX, tempY;
        double angleRad = angle * Math.PI / 180;

        double x1 = centerX - size;
        double y1 = centerY;
        tempX = x1 - centerX;
        tempY = y1 - centerY;
        x1 = centerX + tempX*Math.cos(angleRad) - tempY*Math.sin(angleRad);
        y1 = centerY + tempX*Math.sin(angleRad) + tempY*Math.cos(angleRad);
        double x2 = centerX;
        double y2 = centerY + size;
        tempX = x2 - centerX;
        tempY = y2 - centerY;
        x2 = centerX + tempX*Math.cos(angleRad) - tempY*Math.sin(angleRad);
        y2 = centerY + tempX*Math.sin(angleRad) + tempY*Math.cos(angleRad);
        double x3 = centerX + size;
        double y3 = centerY;
        tempX = x3 - centerX;
        tempY = y3 - centerY;
        x3 = centerX + tempX*Math.cos(angleRad) - tempY*Math.sin(angleRad);
        y3 = centerY + tempX*Math.sin(angleRad) + tempY*Math.cos(angleRad);
        double x4 = centerX;
        double y4 = centerY - size;
        tempX = x4 - centerX;
        tempY = y4 - centerY;
        x4 = centerX + tempX*Math.cos(angleRad) - tempY*Math.sin(angleRad);
        y4 = centerY + tempX*Math.sin(angleRad) + tempY*Math.cos(angleRad);
        return Arrays.asList(
                new Line2D.Double(x1, y1, x2, y2),
                new Line2D.Double(x2, y2, x3, y3),
                new Line2D.Double(x3, y3, x4, y4),
                new Line2D.Double(x4, y4, x1, y1)
        );
    }

    public void freeze() {
        frozen = true;
    }

    public boolean frozen() {
        return frozen;
    }

    //public void savePosition() {
      //  bestCenterX = centerX;
      //  bestCenterY = centerY;
    //}

    public boolean intersects() {
        return polygon.lines().stream().anyMatch(polygonLine -> lines().stream().anyMatch(squareLine -> squareLine.intersectsLine(polygonLine)));
    }

    public boolean canGrow() {
        grow();
        boolean canGrow = !intersects();
        shrink();
        return canGrow;
    }

    public void setTiltDegrees(double tiltDegrees) {
        this.tiltDegrees = tiltDegrees;
    }
}
