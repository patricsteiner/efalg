import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.awt.geom.Line2D;
import java.util.List;
import java.util.function.Consumer;

/**
 * A scaling Pane that visually represents the ParticleSwarmMaxSquareAlgorithm.
 */
public class PolyPane extends BorderPane {

    private Polygon polygon;
    private List<Square> squares;
    private final Canvas canvas;
    private double ratio;

    public PolyPane() {
        canvas = new Canvas();
        setCenter(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        widthProperty().addListener(e -> calculateRatioAndDraw());
        heightProperty().addListener(e -> calculateRatioAndDraw());
        setOnMouseClicked(e -> System.out.println(e.getX() + " " + e.getY())); // secret function to easily create polygons for testing
    }

    private void calculateRatioAndDraw() {
        if (polygon == null || squares == null) return;
        ratio = polygon.width() > polygon.height() ? getWidth() / polygon.width() : getHeight() / polygon.height();
        draw();
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
        calculateRatioAndDraw();
    }

    public void setSquares(List<Square> squares) {
        this.squares = squares;
        calculateRatioAndDraw();
    }

    private double scaleX(double x) {
        return ratio * (x - polygon.minX()) * .97 + 10;
    }

    private double scaleY(double y) {
        return ratio * (y - polygon.minY()) * .97 + 10;
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setLineWidth(2);
        polygon.lines().forEach(drawLine(gc));
        gc.setLineWidth(1);
        if (squares.size() == 1) {
            gc.setLineWidth(2);
            gc.setStroke(Color.GREEN);
        }
        squares.stream().flatMap(square -> square.lines().stream()).forEach(drawLine(gc));
    }

    private Consumer<Line2D> drawLine(GraphicsContext gc) {
        return line -> gc.strokeLine(scaleX(line.getX1()), scaleY(line.getY1()), scaleX(line.getX2()), scaleY(line.getY2()));
    }

}
