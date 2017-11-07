package plagiatDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlagiatDetectorMain extends Application {
	
	public static void main(String[] args) {
		launch(args);	
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		PlagiatDetectorPane plagiatDetectorPane = new PlagiatDetectorPane();
		Scene scene = new Scene(plagiatDetectorPane);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> Platform.exit());
	}
	
	@Override
	public void stop() throws Exception {
		
		super.stop();
	}
}
