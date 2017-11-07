package plagiatDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class PlagiatDetectorPane extends BorderPane {
	
	private PlagiatDetector plagiatDetector;
	private double[][] similarityMatrix;
	private Canvas canvas;
	private int gridSize;
	
	public static final String DIR_DOCUMENTS = "hw2/testdata";
	
	public PlagiatDetectorPane() throws IOException {
		plagiatDetector = new PlagiatDetector();

		canvas = new Canvas();
		setCenter(canvas);
		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
		widthProperty().addListener(e -> draw());
		heightProperty().addListener(e -> draw());
		
		File testdata = new File(DIR_DOCUMENTS);

		List<Document> documents = new ArrayList<>();

		for (File file : testdata.listFiles()) {
			String rawContent = new String(Files.readAllBytes(file.toPath()));
			documents.add(new Document(file.getName(), rawContent));
		}
		
		documents.forEach(plagiatDetector::addDocument);
		
		Task detectorTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				similarityMatrix = new double[documents.size()][documents.size()];
				gridSize = documents.size() + 1; // + 1 because need space for document name
				for (int i = 0; i < documents.size(); i++) {
					for (int j = 0; j < documents.size(); j++) {
						Document d1 = documents.get(i);
						Document d2 = documents.get(j);
						double similarity = plagiatDetector.similarity(d1, d2);
						similarityMatrix[i][j] = similarity;
						System.out.println(d1.getName() + " vs " + d2.getName() + ": " + similarity);
					}
				}
				documents.forEach(d -> System.out.println(d.getName() + ": " + d.getShingleIndices().stream().sorted().map(String::valueOf).reduce((i, i2) -> i + " " + i2)));
				draw();
				return null;
			}
		};
		
		new Thread(detectorTask).start();	
	}
	
	/**
	 * Map an x value in the grid to an x value in the Pane.
	 * @param x
	 * @return the scaled value
	 */
	private double scaleX(double x) {
		return getWidth() / gridSize * x;
	}
	
	/**
	 * Map an y value in the grid to an y value in the Pane.
	 * @param y
	 * @return the scaled value
	 */
	private double scaleY(double y) {
		return getHeight() / gridSize * y;	
	}
	
	private void draw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, getWidth(), getHeight());
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		// make sure the fontsize is scaled properly
		gc.setFont(new Font("Arial", getWidth() >= getHeight() ? scaleY(.25) : scaleX(.2)));
		// draw similarities and fill rectangles
		for (int i = 0; i < similarityMatrix.length; i++) {
			for (int j = 0; j < similarityMatrix.length; j++) {
				System.out.print(similarityMatrix[i][j]);
				gc.setFill(Color.gray((1-similarityMatrix[i][j]/2)));
				gc.fillRect(scaleX(i+1), scaleY(j+1), scaleX(i+2), scaleY(j+2));
				gc.setFill(Color.BLACK);
				String similarity = String.format("%.0f%%", similarityMatrix[i][j]*100);
				gc.fillText(similarity, scaleX(i+1.5), scaleY(j+1.5));
			}
			System.out.println();
		}
		// draw grid
		for (int i = 0; i < gridSize; i++) {
			gc.strokeLine(0, scaleY(i), scaleX(gridSize), scaleY(i));
			gc.strokeLine(scaleX(i), 0, scaleX(i), scaleY(gridSize));
		}
		// draw document names
		gc.setFont(new Font("Arial", getWidth() >= getHeight() ? scaleY(.2) : scaleX(.15)));
		for (int i = 0; i < plagiatDetector.getAllDocuments().size(); i++) {
			String documentName = plagiatDetector.getAllDocuments().get(i).getName();
			gc.fillText(documentName, scaleX(.5), scaleY(i+1.5));
			gc.fillText(documentName, scaleX(i + 1.5), scaleY(.5));
		}
	}
	
}
