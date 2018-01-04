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

    public Square(double centerX, double centerY, double size) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.size = size;
    }

    public void growByFactor(double factor) {
        this.size *= factor;
    }

    public void grow(double amount) {
        this.size += amount;
    }

    public void tilt(double degrees) {
        this.angle += degrees;
    }

    public List<Line2D> lines() {
        double x1 = centerX - size;
        double x3 = centerX + size;
        double x2 = centerX;
        double x4 = centerX;
        double y1 = centerY;
        double y3 = centerY;
        double y2 = centerY + size;
        double y4 = centerY - size;
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
}
