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

/**
 * This program calculates the square with the maximum possible size in a given polygon by using a particle swarm optimization.
 * It assumes that all polygons are closed and the polygon is not a perfect square (otherwise it does not make sense to call this algorithm...)
 */
public class PolyMain extends Application {

    /**
     * Configurable constants, should be adjusted depending on the input.
     */
    private final static int PARTICLES = 100; // amount of particles the algorithm uses. The more complex the polygon, the more particles should be used.
    private final static int EPOCHS = 100; // amount of iterations the algorithm does. Increase for a more accurate result.
    private final static int DELAY = 0; // in milliseconds, increase if you want to see what the algorithm does in the GUI.
    private final static int GUI_UPDATE_INTERVAL = 100; // in milliseconds
    private final static String FILE_PATH = "hw6/Max-Square_in_Polygon.txt";

    private static Queue<Polygon> polygons;

    /**
     * Reads the input file and creates polygons in a list that will be processed in a later stage.
     * @param args not needed
     * @throws FileNotFoundException
     */
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

    /**
     * Sets up the GUI and starts processing the polygons.
     * @param primaryStage
     * @throws Exception
     */
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

    /**
     * Sequentially runs the ParticleSwarmMaxSquareAlgorithm on all polygons and tells the GUI what to draw.
     * @param polyPane
     */
    private void processNextPolygon(PolyPane polyPane) {
        if (polygons.isEmpty()) return;
        Polygon polygon = polygons.poll();
        final ParticleSwarmMaxSquareAlgorithm particleSwarmMaxSquareAlgorithm = new ParticleSwarmMaxSquareAlgorithm(polygon, PARTICLES, EPOCHS, DELAY);
        polyPane.setPolygon(polygon);
        polyPane.setSquares(particleSwarmMaxSquareAlgorithm.squares());
        Task<Square> maxSquareFinderTask = new Task<Square>() {
            @Override
            protected Square call() throws Exception {
                return particleSwarmMaxSquareAlgorithm.run();
            }
        };
        maxSquareFinderTask.setOnSucceeded(e -> {
            Square maxSquare = maxSquareFinderTask.getValue();
            System.out.printf("Max Square found: size = %.2f, coordinates = %s\n", maxSquare.size(), maxSquare.coordinates());
            polyPane.setSquares(Collections.singletonList(maxSquare));
            new Alert(Alert.AlertType.INFORMATION, "Done").showAndWait();
            processNextPolygon(polyPane);
        });
        new Thread(maxSquareFinderTask).start();
    }

}