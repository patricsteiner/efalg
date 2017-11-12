package plagiatDetector;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import plagiatDetector.gui.PlagiatDetectorPane;

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
