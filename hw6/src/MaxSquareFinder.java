import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
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
        for (int i = 0; i < 10000; i++) {
            for (Square square : squares) {
                if (square.canGrow()) square.grow();
                else if (square.angle() < 90 && !square.frozen()) {
                    if (square.size() > square.maxSize()) {
                        square.maxSize(square.size());
                        square.bestAngle(square.angle());
                    }
                    square.tilt();
                } else {
                    square.freeze();
                    // start finetuning?
                }
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
            squares.add(new Square(polygon, x, y, polygon.width() / 1000));
        }
    }

    public Polygon polygon() {
        return polygon;
    }

    public List<Square> squares() {
        return squares;
    }

}
