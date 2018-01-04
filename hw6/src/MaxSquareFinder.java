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
        for (int i = 0; i < 100; i++) {
            checkIntersection();
            growSquares();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void growSquares() {
        for (Square square : squares) {
            if (!square.frozen()) {
                square.growByFactor(1.1);
            }
        }
    }

    private void checkIntersection() {
        for (Square square : squares) {
            List<Line2D> lines = square.lines();
            for (Line2D squareLine : lines) {
                if (polygon.lines().stream().anyMatch(line -> line.intersectsLine(squareLine))) {
                    square.freeze();
                    break;
                }
            }
        }
    }

    private void initSquares() {
        squares = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            double x, y;
            do { // pick random point in polygon
                x = Math.random() * polygon.width() + polygon.minX();
                y = Math.random() * polygon.height() + polygon.minY();
            } while (!polygon.fxPolygon().contains(x, y));
            squares.add(new Square(x, y, polygon.width() / 100));
        }
    }

    public Polygon polygon() {
        return polygon;
    }

    public List<Square> squares() {
        return squares;
    }

}
