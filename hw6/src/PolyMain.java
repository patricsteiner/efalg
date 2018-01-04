import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PolyMain extends Application {

    private final static int GUI_UPDATE_INTERVAL = 100; // in milliseconds
    private final static int DELAY = 10; // in milliseconds

    private static final String FILE_PATH = "hw6/Max-Square_in_Polygon.txt";
    private static Queue<Polygon> polygons;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(FILE_PATH));
        int nPolygons = scanner.nextInt();
        polygons = new LinkedList<>();
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
        PolyPane polyPane = new PolyPane();
        polyPane.setPrefWidth(700);
        polyPane.setPrefHeight(700);
        Scene scene = new Scene(polyPane);
        primaryStage.setScene(scene);
        primaryStage.show();
        Timeline guiUpdater = new Timeline(new KeyFrame(Duration.millis(GUI_UPDATE_INTERVAL), e -> polyPane.draw()));
        guiUpdater.setCycleCount(Timeline.INDEFINITE);
        guiUpdater.play();

        processNextPolygon(polyPane);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private void processNextPolygon(PolyPane polyPane) {
        if (polygons.isEmpty()) return;
        Polygon polygon = polygons.poll();
        final MaxSquareFinder maxSquareFinder = new MaxSquareFinder(polygon, DELAY);
        polyPane.setPolygon(polygon);
        polyPane.setSquares(maxSquareFinder.squares());
        Task<Void> maxSquareFinderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                maxSquareFinder.run();
                return null;
            }
        };
        maxSquareFinderTask.setOnSucceeded(e -> {
            new Alert(Alert.AlertType.INFORMATION, "Done").showAndWait();
            processNextPolygon(polyPane);
        });
        new Thread(maxSquareFinderTask).start();
    }
}
