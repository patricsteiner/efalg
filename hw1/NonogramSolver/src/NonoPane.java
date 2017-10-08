import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * A simple, scalable GUI to display a Nonogram.
 * 
 * @author Patric Steiner
 */
public class NonoPane extends BorderPane {
	
	private Nonogram nonogram;
	private int gridWidth;
	private int gridHeight;
	private Canvas canvas;
	
	/**
	 * C-tor, sets up the Pane.
	 * @param nonogram the nonogram associated with this GUI.
	 */
	public NonoPane(Nonogram nonogram) {
		canvas = new Canvas();
		setCenter(canvas);
		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
		widthProperty().addListener(e -> draw());
		heightProperty().addListener(e -> draw());
		setNonogram(nonogram);
	}
	
	/**
	 * Sets the nonogram associated with this GUI, calculates the width and height of the Pane
	 * according to the width and height of the Nonogram and (re)draws the Pane.
	 * @param nonogram
	 */
	private void setNonogram(Nonogram nonogram) {
		this.nonogram = nonogram;
		// to get the grid dimensions, add the maximum amount of hints to the nonogram size
		// to make sure there is enough space to display everything.
		gridHeight = nonogram.getHeight() + nonogram.getColHints().stream().max((l1, l2) -> l1.size() - l2.size()).get().size();
		gridWidth = nonogram.getWidth() + nonogram.getRowHints().stream().max((l1, l2) -> l1.size() - l2.size()).get().size();
		draw();
	}
	
	/**
	 * Map an x value in the grid to an x value in the Pane.
	 * @param x
	 * @return the scaled value
	 */
	private double scaleX(double x) {
		return getWidth() / gridWidth * x;
	}
	
	/**
	 * Map an y value in the grid to an y value in the Pane.
	 * @param y
	 * @return the scaled value
	 */
	private double scaleY(double y) {
		return getHeight() / gridHeight * y;	
	}
	
	/**
	 * Draws the nonogram on the canvas.
	 */
	public void draw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, getWidth(), getHeight());
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		// make sure the fontsize is scaled properly
		gc.setFont(new Font("Arial", getWidth() >= getHeight() ? scaleY(1) : scaleX(.8)));
		// draw the hints for the columns
		for (int i = 0; i < nonogram.getColHints().size(); i++) {
			int hints = nonogram.getColHints().get(i).size();
			for (int j = 0; j < hints; j++) {
				double x = i + gridWidth - nonogram.getWidth() + .5; // use .5 as offset to make it look a bit better.
				double y = gridHeight - nonogram.getHeight() - j - .5;
				gc.fillText(nonogram.getColHints().get(i).get(hints - j - 1).toString(), scaleX(x), scaleY(y));
			}
		}
		// draw the hints for the rows
		for (int i = 0; i < nonogram.getRowHints().size(); i++) {
			int hints = nonogram.getRowHints().get(i).size();
			for (int j = 0; j < hints; j++) {
				double x = gridWidth - nonogram.getWidth() - j - 1 + .5;
				double y = i + gridHeight - nonogram.getHeight() + 1 - .5;
				gc.fillText(nonogram.getRowHints().get(i).get(hints - j - 1).toString(), scaleX(x), scaleY(y));
			}
		}
		// loop through the nonogram and just draw squares in a color based on the value in the nonogram.
		for (int i = 0; i < nonogram.getHeight(); i++) {
			for (int j = 0; j < nonogram.getWidth(); j++) {
				Color color = Color.LIGHTGRAY;
				if (nonogram.get(i, j) == Nonogram.FILLED) color = Color.BLACK;
				else if (nonogram.get(i, j) == Nonogram.EMPTY) color = Color.DARKORCHID;
				if (nonogram.getPrediction(i, j) == Nonogram.FILLED) color = NonoMain.DEBUG_COLORS ? Color.GRAY : Color.BLACK;
				else if (nonogram.getPrediction(i, j) == Nonogram.EMPTY) color = NonoMain.DEBUG_COLORS ? Color.ORCHID : Color.DARKORCHID;
				if (nonogram.getPreprocessed(i, j) == Nonogram.FILLED) color = NonoMain.DEBUG_COLORS ? Color.DARKRED : Color.BLACK;
				gc.setFill(color);
				double x = j + gridWidth - nonogram.getWidth();
				double y = i + gridHeight - nonogram.getHeight();
				gc.fillRect(scaleX(x), scaleY(y), scaleX(x+1), scaleY(y+1));
			}
		}
		// lastly, draw a grid to make it look pretty.
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		for (int i = gridHeight - nonogram.getHeight(); i < gridHeight; i++) {
			gc.strokeLine(0, scaleY(i), scaleX(gridWidth), scaleY(i));
		}
		for (int j = gridWidth - nonogram.getWidth(); j < gridWidth; j++) {
			gc.strokeLine(scaleX(j), 0, scaleX(j), scaleY(gridHeight));
		}
	}
}
