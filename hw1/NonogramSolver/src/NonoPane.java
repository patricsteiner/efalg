import javafx.concurrent.Task;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class NonoPane extends BorderPane implements NonogramListener {
	
	private Nonogram nonogram;
	int gridWidth;
	int gridHeight;
	Canvas canvas;
	
	public NonoPane(Nonogram nonogram) {
		canvas = new Canvas();
		setCenter(canvas);
		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
		widthProperty().addListener(e -> update());
		heightProperty().addListener(e -> update());
		setNonogram(nonogram);
	}
	
	void setNonogram(Nonogram nonogram) {
		this.nonogram = nonogram;
		nonogram.addListener(this);
		gridHeight = nonogram.height + nonogram.colHints.stream().max((l1, l2) -> l1.size() - l2.size()).get().size();
		gridWidth = nonogram.width + nonogram.rowHints.stream().max((l1, l2) -> l1.size() - l2.size()).get().size();
		update();
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				nonogram.findSolution(0);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	double scaleX(double x) {
		return getWidth() / gridWidth * x;
	}
	
	double scaleY(double y) {
		return getHeight() / gridHeight * y;	
	}

	@Override
	public void update() {	
		draw();
	}
	
	void draw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, getWidth(), getHeight());
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(new Font("Arial", getWidth() >= getHeight() ? scaleY(1) : scaleX(.8)));
		for (int i = 0; i < nonogram.colHints.size(); i++) {
			int hints = nonogram.colHints.get(i).size();
			for (int j = 0; j < hints; j++) {
				double x = i + gridWidth - nonogram.width + .5;
				double y = gridHeight - nonogram.height - j - .5;
				gc.fillText(nonogram.colHints.get(i).get(hints - j - 1).toString(), scaleX(x), scaleY(y));
			}
		}
		for (int i = 0; i < nonogram.rowHints.size(); i++) {
			int hints = nonogram.rowHints.get(i).size();
			for (int j = 0; j < hints; j++) {
				double x = gridWidth - nonogram.width - j - 1 + .5;
				double y = i + gridHeight - nonogram.height + 1 - .5;
				gc.fillText(nonogram.rowHints.get(i).get(hints - j - 1).toString(), scaleX(x), scaleY(y));
			}
		}
		for (int i = 0; i < nonogram.height; i++) {
			for (int j = 0; j < nonogram.width; j++) {
				if (nonogram.get(i, j) == Nonogram.FILLED) gc.setFill(Color.BLACK);
				else if (nonogram.get(i, j) == Nonogram.EMPTY) gc.setFill(Color.DARKORCHID);
				else gc.setFill(Color.LIGHTGRAY);
				double x = j + gridWidth - nonogram.width;
				double y = i + gridHeight - nonogram.height;
				gc.fillRect(scaleX(x), scaleY(y), scaleX(x+1), scaleY(y+1));
			}
		}
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		for (int i = gridHeight - nonogram.height; i < gridHeight; i++) {
			gc.strokeLine(0, scaleY(i), scaleX(gridWidth), scaleY(i));
		}
		for (int j = gridWidth - nonogram.width; j < gridWidth; j++) {
			gc.strokeLine(scaleX(j), 0, scaleX(j), scaleY(gridHeight));
		}
	}
}
