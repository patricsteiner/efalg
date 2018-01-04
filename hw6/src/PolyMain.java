import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PolyMain extends Application {

    private static final String FILE_PATH = "hw6/Max-Square_in_Polygon.txt";
    private static List<Polygon> polygons;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(FILE_PATH));
        int nPolygons = scanner.nextInt();
        polygons = new ArrayList<>(nPolygons);
        for (int i = 0; i < nPolygons; i++) {
            int nPoints = scanner.nextInt();
            List<Point2D> points = new ArrayList<>(nPoints);
            for (int j = 0; j < nPoints; j++) {
                points.add(new Point2D.Double(scanner.nextDouble(), scanner.nextDouble()));
            }
            Polygon polygon = new Polygon(points);
            polygons.add(polygon);
        }
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PolyPane polyPane = new PolyPane(polygons);
        polyPane.setPrefWidth(700);
        polyPane.setPrefHeight(700);
        Scene scene = new Scene(polyPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
