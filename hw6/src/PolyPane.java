
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PolyPane extends BorderPane {

    private Polygon polygon;
    private List<Square> squares;
    private Canvas canvas;

    public PolyPane(List<Polygon> polygons) {
        canvas = new Canvas();
        setCenter(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
        polygons.forEach(this::findMaxSquare);
        //findMaxSquare(polygons.get(0));
    }

    void findMaxSquare(Polygon polygon) {
        this.polygon = polygon;
        squares = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            double x, y;
            do { // pick random point in polygon
                x = Math.random() * polygon.width() + polygon.minX();
                y = Math.random() * polygon.height() + polygon.minY();
            } while (!polygon.fxPolygon().contains(x, y));
            squares.add(new Square(x, y));
        }
        draw();
    }

    private double scaleX(double x) {
        return getWidth()/ polygon.width() * (x - polygon.minX()) * .97 + 10;
    }

    private double scaleY(double y) {
        return getWidth() / polygon.width() * (y - polygon.minY()) * .97 + 10;
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);
        polygon.lines().forEach(line -> gc.strokeLine(scaleX(line.getX1()), scaleY(line.getY1()), scaleX(line.getX2()), scaleY(line.getY2())));
        squares.forEach(s -> gc.strokeOval(scaleX(s.centerX), scaleY(s.centerY), 2, 2));
    }
}
