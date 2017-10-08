import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main class and starting point of the application. 
 * This application uses 2 seperate threads. One for updating the GUI and one for running the algorithm.
 * 
 * @author Patric Steiner
 */
public class NonoMain extends Application {
	
	// ######################################################
	// SET THESE VALUES NEFORE RUNNING 
	
	private final static String FILENAME_IN = "rabbit.txt";
	private final static String FILENAME_OUT = "out.txt";
	private final static int GUI_UPDATE_INTERVAL = 100; // in milliseconds
	public final static boolean DEBUG_COLORS = true; // if true, shows different colors in the GUI depending how the block was determined.
	private final static int DELAY = 0; // amount of ms to wait between every iteration (useful to see progress in GUI)
	
	// ######################################################
	
	/**
	 * The nonogram that is being processed by the algorithm.
	 */
	private Nonogram nonogram;
	
	/**
	 * Time when the algorithm was started, used to measure execution time.
	 */
	private long start;
	
	/**
	 * Entry point of the application. 
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Is automatically called, sets up the GUI and the algorithm thread and runs them both.
	 * Exceptions are not handled.
	 */
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
		
		// set up a task for solving the algorithm.
		Task<Boolean> solverTask = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				start = System.nanoTime();
				return nonogram.solve(DELAY);
			}
		};
		solverTask.setOnSucceeded(e -> {
			String elapsedTime =  (System.nanoTime() - start)/1000000 + "ms"; // calculating the elapsed time in ms.
			nonoPane.draw();
			// check if a solution was found and output a message as well as the elapsed time.
			if (solverTask.getValue()) {
				new Alert(AlertType.INFORMATION, "Yay, did it. Only took " + elapsedTime).showAndWait();
				
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
			else { // no solution was found
				new Alert(AlertType.ERROR, "Not gonna happen. You wasted " + elapsedTime + " of your life.").showAndWait();
			}
		});
		solverTask.setOnFailed(e -> System.err.println("Solver Task failed!")); // errors are not handled.
		new Thread(solverTask).start();
		
		// set the interval for updating the GUI
		Timeline guiUpdater = new Timeline(new KeyFrame(Duration.millis(GUI_UPDATE_INTERVAL), e -> nonoPane.draw()));
		guiUpdater.setCycleCount(Timeline.INDEFINITE);
		guiUpdater.play();
	}
	
	/**
	 * Makes sure the algorithm stops when the window is closed.
	 */
	@Override
	public void stop() throws Exception {
		nonogram.stopSolving();
		System.out.println(nonogram); // print the current state of the nonogram.
		super.stop();
	}
}
