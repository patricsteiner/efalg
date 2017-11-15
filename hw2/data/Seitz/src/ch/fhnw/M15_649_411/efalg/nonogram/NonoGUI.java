package ch.fhnw.M15_649_411.efalg.nonogram;

import ch.fhnw.M15_649_411.efalg.nonogram.algorithms.*;
import ch.fhnw.M15_649_411.efalg.nonogram.algorithms.zx_algorithm.ZxAlgorithm;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.File;

/**
 * created on 25.09.2017
 *
 * @author Claudio
 * @version 1.0
 */
public class NonoGUI extends BorderPane {
    private MenuBar menu;
    private Menu file;
    private MenuItem openFile;
    private Menu algorithms;
    private MenuItem startAlgorithm;
    private MenuItem stopAlgorithm;

    private NonoBoard board;
    private AbstractAlgorithm algorithm;
    private final double lineWidth = 1;
    private double dx, dy;
    private Canvas canvas;

    public NonoGUI() {
        initControls();
        layoutControls();
        addListener();

        // responsive UI
        widthProperty().addListener((observable, oldValue, newValue) -> drawGrid((double) newValue, getHeight() - menu.getHeight()));
        heightProperty().addListener((observable, oldValue, newValue) -> drawGrid(getWidth(), (double) newValue - menu.getHeight()));
        setPrefSize(800, 800);

        try {
            this.board = IOHandler.getInstance().loadFile(new File(this.getClass().getResource("/NonogramSmall.txt").toURI()));
            initGrid();
            drawGrid(getWidth(), getHeight() - menu.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initControls() {
        this.canvas = new Canvas();
        this.menu = new MenuBar();
        this.file = new Menu("File");
        this.openFile = new MenuItem("Open");
        this.algorithms = new Menu("Algorithm");
        this.startAlgorithm = new MenuItem("start");
        this.stopAlgorithm = new MenuItem("stop");
    }

    private void layoutControls() {
        menu.getMenus().addAll(file, algorithms);
        file.getItems().addAll(openFile);
        algorithms.getItems().addAll(startAlgorithm, stopAlgorithm);
        setTop(menu);
        setCenter(canvas);
    }

    private void addListener() {
        openFile.setOnAction(event -> {
            NonoBoard board = IOHandler.getInstance().selectFile();
            if (board != null) {
                this.board = board;
                initGrid();
                drawGrid(getWidth(), getHeight() - menu.getHeight());
            }
        });
        startAlgorithm.setOnAction(event -> {
            if (board != null) {
                board.resetBoard();
                if (algorithm != null && algorithm.isRunning())
                    algorithm.stop();
                algorithm = new ZxAlgorithm(board);
                algorithm.start();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No Nonogram is loaded");
                alert.showAndWait();
            }
        });
        stopAlgorithm.setOnAction(event -> {
            if (algorithm != null && algorithm.isRunning())
                algorithm.stop();
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No algorithm is running");
                alert.showAndWait();
            }
        });
    }

    /**
     * Initialises an new Nonogram UI
     */
    private void initGrid() {
        this.board.addListener((x, y, oldValue, newValue) -> {
            //update Grid visualisation
            Platform.runLater(() -> {
                if (oldValue != newValue) {
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    drawField(gc, x, y, newValue.getColor());
                }
            });

        });
    }

    /**
     * Draws the grid, used for resizing
     */
    private void drawGrid(double width, double height) {
        if (board == null) return;
        dx = width / (board.nxVecs + board.columns);
        dy = height / (board.nyVecs + board.rows);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setWidth(width);
        canvas.setHeight(height);
        gc.clearRect(0, 0, width, height);

        //draw lines
        gc.setLineWidth(lineWidth);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(dx * board.nxVecs, 0, dx * board.nxVecs, height);
        gc.strokeLine(0, dy * board.nyVecs, width, dy * board.nyVecs);

        gc.setStroke(Color.DARKGRAY);
        //draw vertical lines
        for (int i = 1; i < board.nxVecs; i++) {
            gc.strokeLine(i * dx, dy * board.nyVecs, i * dx, height);
        }
        for (int i = board.nxVecs + 1; i < board.nxVecs + board.columns; i++) {
            gc.strokeLine(i * dx, 0, i * dx, height);
        }

        //draw horizontal lines
        for (int i = 1; i < board.nyVecs; i++) {
            gc.strokeLine(dx * board.nxVecs, i * dy, width, i * dy);
        }
        for (int i = board.nyVecs + 1; i < board.nyVecs + board.rows; i++) {
            gc.strokeLine(0, i * dy, width, i * dy);
        }

        //draw numbers
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Helvetica", dy - lineWidth));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        //draw vertical numbers
        int[][] yVecs = board.getyVecs();
        for (int i = 0; i < yVecs.length; i++) {
            for (int j = 0; j < yVecs[i].length; j++) {
                String number = String.valueOf(yVecs[i][yVecs[i].length - 1 - j]);
                gc.fillText(number, dx * (board.nxVecs + 0.5 + i), dy * (board.nyVecs - 0.5 - j), dx - 2 * lineWidth);
            }
        }

        //draw horizontal numbers
        int[][] xVecs = board.getxVecs();
        for (int i = 0; i < xVecs.length; i++) {
            for (int j = 0; j < xVecs[i].length; j++) {
                String number = String.valueOf(xVecs[i][xVecs[i].length - 1 - j]);
                gc.fillText(number, dx * (board.nxVecs - 0.5 - j), dy * (board.nyVecs + 0.5 + i), dx - 2 * lineWidth);
            }
        }

        //draw fields
        NonoBoard.State[][] fields = board.getData();
        for (int y = 0; y < board.rows; y++) {
            for (int x = 0; x < board.columns; x++) {
                drawField(gc, x, y, fields[y][x].getColor());
            }
        }
    }

    /**
     * Fills the selected field
     *
     * @param gc graphic context of canvas
     * @param x  coordinate
     * @param y  coordinate
     */
    private void drawField(GraphicsContext gc, int x, int y, Color color) {
        gc.setFill(color);
        gc.fillRect((x + board.nxVecs) * dx + lineWidth / 2, (y + board.nyVecs) * dy + lineWidth / 2,
                dx - lineWidth, dy - lineWidth);
    }
}
