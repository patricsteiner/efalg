package ch.fhnw.M15_649_411.efalg.nonogram;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * created on 25.09.2017
 *
 * @author Claudio
 * @version 1.0
 */
public class NonoApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        NonoGUI gui = new NonoGUI();
        Scene scene = new Scene(gui);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Nonogramm");
        primaryStage.show();
    }
}
