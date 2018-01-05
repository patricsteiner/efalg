import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class MaxSquareFinder {

    private int delay;
    private Polygon polygon;
    private List<Square> squares;

    public MaxSquareFinder(Polygon polygon, int delay) {
        this.polygon = polygon;
        this.delay = delay;
        initSquares();
    }

    public void run() {
        for (int i = 0; i < 5000; i++) {
            for (Square square : squares) {
                if (square.canGrow()) square.grow(); // every square to max size with start-angle
                else if (square.tilt()) square.grow(); // when it can't grow anymore: tilt it
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initSquares() {
        squares = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            double x, y;
            do { // pick random point in polygon
                x = Math.random() * polygon.width() + polygon.minX();
                y = Math.random() * polygon.height() + polygon.minY();
            } while (!polygon.fxPolygon().contains(x, y));
            squares.add(new Square(polygon, x, y, polygon.width() / 100));
        }
    }

    public Polygon polygon() {
        return polygon;
    }

    public List<Square> squares() {
        return squares;
    }

}
