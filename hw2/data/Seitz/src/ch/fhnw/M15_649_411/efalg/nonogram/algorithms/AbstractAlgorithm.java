package ch.fhnw.M15_649_411.efalg.nonogram.algorithms;

import ch.fhnw.M15_649_411.efalg.nonogram.NonoBoard;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.Date;

/**
 * created on 01.10.2017
 *
 * @author Claudio
 * @version 1.0
 */
public abstract class AbstractAlgorithm {
    protected NonoBoard board;
    private Thread thread;

    public AbstractAlgorithm(NonoBoard board) {
        this.board = board;
        this.thread = new Thread(() -> {
            //algorithm runnable
            Date start = new Date(System.currentTimeMillis());
            System.out.println("Algorithm started at " + start);
            execute();
            Date end = new Date(System.currentTimeMillis());
            System.out.println("Algorithm has finished at " + end);
            if (!isSolution()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "No solution was found.\n");
                    alert.showAndWait();
                });
            }
            System.out.println("Algorithm running time: " + ((end.getTime() - start.getTime())) + "ms");
        });
    }

    /**
     * Starts the algorithm thread
     */
    public void start() {
        thread.start();
    }

    /**
     * Stops the algorithm thread
     */
    public void stop() {
        thread.stop();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Algorithm has been stopped.");
            alert.showAndWait();
        });
    }

    /**
     * Checks if the algorithm is running
     * @return true if the algorithm is running, otherwise false
     */
    public boolean isRunning() {
        return thread.isAlive();
    }

    /**
     * Concrete algorithm implementation
     */
    protected abstract void execute();

    /**
     * Checks if a solution was found
     * @return success
     */
    protected abstract boolean isSolution();
}
