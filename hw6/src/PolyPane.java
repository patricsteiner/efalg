import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Stream;

public class PolyPane extends BorderPane {

    private Polygon polygon;
    private List<Square> squares;
    private Canvas canvas;

    public PolyPane() {
        canvas = new Canvas();
        setCenter(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public void setSquares(List<Square> squares) {
        this.squares = squares;
    }

    private double scaleX(double x) {
        return getWidth() / polygon.width() * (x - polygon.minX()) * .97 + 10;
    }

    private double scaleY(double y) {
        return getWidth() / polygon.width() * (y - polygon.minY()) * .97 + 10;
    }

    public void draw() {
        if (polygon == null) return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setLineWidth(2);
        polygon.lines().stream().forEach(line -> gc.strokeLine(scaleX(line.getX1()), scaleY(line.getY1()), scaleX(line.getX2()), scaleY(line.getY2())));
        gc.setLineWidth(1);
        squares.stream().flatMap(square -> square.lines().stream()).forEach(line -> gc.strokeLine(scaleX(line.getX1()), scaleY(line.getY1()), scaleX(line.getX2()), scaleY(line.getY2())));
    }
}
