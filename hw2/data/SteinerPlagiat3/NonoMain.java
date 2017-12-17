import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

public class NonoMain extends Application {

	private final static String FILENAME_IN = "rabbit.txt";
	private final static String FILENAME_OUT = "out.txt";
	private final static int GUI_UPDATE_INTERVAL = 100; // in milliseconds
	public final static boolean DEBUG_COLORS = true; // if true, shows different colors in the GUI depending how the block was determined.
	private final static int DELAY = 0; // amount of ms to wait between every iteration (useful to see progress in GUI)

	private Nonogram nonogram;

	private long start;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		nonogram = new Nonogram(new FileInputStream(new File(FILENAME_IN)));
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
				return nonogram.solve(DELAY);
			}
		};
		solverTask.setOnSucceeded(e -> {
			String elapsedTime =  (System.nanoTime() - start)/1000000 + "ms";
			nonoPane.draw();

			if (solverTask.getValue()) {
				new Alert(AlertType.INFORMATION, "SOLVED " + elapsedTime).showAndWait();
				
				// write the solution to a file.
				try {
					PrintWriter p;
					p = new PrintWriter(new File(FILENAME_OUT));
					p.print(nonogram.toString());
					p.flush();
					p.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else {
				new Alert(AlertType.ERROR, "NOT SOLVED " + elapsedTime + "").showAndWait();
			}
		});
		solverTask.setOnFailed(e -> System.err.println("Solver Task failed!")); // errors are not handled.
		new Thread(solverTask).start();
		
		// set the interval for updating the GUI
		Timeline guiUpdater = new Timeline(new KeyFrame(Duration.millis(GUI_UPDATE_INTERVAL), e -> nonoPane.draw()));
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
