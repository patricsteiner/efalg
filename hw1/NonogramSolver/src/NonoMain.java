import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NonoMain extends Application {
	
	protected Nonogram nonogram;
	protected long start;
	
	public static void main(String[] args) throws FileNotFoundException {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		nonogram = new Nonogram(new FileInputStream(new File("25x25.txt")));
		NonoPane nonoPane = new NonoPane(nonogram);
		nonoPane.setPrefWidth(700);
		nonoPane.setPrefHeight(700);
		Scene s = new Scene(nonoPane);
		primaryStage.setScene(s);
		nonoPane.setVisible(true);
		primaryStage.show();
		Task<Boolean> solverTask = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				start = System.nanoTime();
				return nonogram.solve(0);
			}
		};
		solverTask.setOnSucceeded(e -> {
			long ns = System.nanoTime() - start;
			String time =  ns/1000000 + "ms";
			nonoPane.draw();
			if (solverTask.getValue()) {
				new Alert(AlertType.INFORMATION, "Yay, did it. Only took " + time).showAndWait();
			}
			else {
				new Alert(AlertType.ERROR, "Not gonna happen. You wasted " + time + " of your life.").showAndWait();
			}
		});
		solverTask.setOnFailed(e -> System.err.println("Solver Task failed!"));
		new Thread(solverTask).start();
		
		Timeline guiUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> nonoPane.draw()));
		guiUpdater.setCycleCount(Timeline.INDEFINITE);
		guiUpdater.play();
	}
	
	@Override
	public void stop() throws Exception {
		nonogram.stopSolving();
		System.out.println(nonogram);
		super.stop();
	}
}
