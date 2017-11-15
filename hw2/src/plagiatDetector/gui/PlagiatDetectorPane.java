package plagiatDetector.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javafx.concurrent.Task;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import plagiatDetector.PlagiatDetector;
import plagiatDetector.factories.DocumentFactory;
import plagiatDetector.models.Document;
import plagiatDetector.util.JavaSourceFolder;

public class PlagiatDetectorPane extends BorderPane {

    private PlagiatDetector plagiatDetector;
    private double[][] similarityMatrix;
    private Canvas canvas;
    private int gridSize;

    public static final String DIR_JAVASOURCEFOLDERS = "hw2/data";

    public PlagiatDetectorPane() throws IOException {
        plagiatDetector = new PlagiatDetector();

        canvas = new Canvas();
        setCenter(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        File javaSourceFolders = new File(DIR_JAVASOURCEFOLDERS);

        DocumentFactory documentFactory = plagiatDetector.getDocumentFactory();
        for (File javaSourceFolder : javaSourceFolders.listFiles()) {
            Document document = documentFactory.makeDocument(new JavaSourceFolder(javaSourceFolder));
            plagiatDetector.addDocument(document);
            File documentAsFile = new File(Paths.get(javaSourceFolder.getPath(), document.getName().concat(".txt")).toString());
            Files.write(documentAsFile.toPath(), document.getProcessedContent().getBytes());
        }

        List<Document> documents = plagiatDetector.getAllDocuments();

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
                    }
                }
                draw();
                return null;
            }
        };
        new Thread(detectorTask).start();

        //documents.forEach(d -> System.out.println(d.getName() + ": " + d.getShingleIndices().stream().sorted().map(String::valueOf).reduce((i, i2) -> i + " " + i2)));
         documents.forEach(d -> System.out.println(d.getProcessedContent()));
    }

    /**
     * Map an x value in the grid to an x value in the Pane.
     *
     * @param x
     * @return the scaled value
     */
    private double scaleX(double x) {
        return getWidth() / gridSize * x;
    }

    /**
     * Map an y value in the grid to an y value in the Pane.
     *
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
                Color color = Color.gray((1 - similarityMatrix[i][j] / 2));
                //if (similarityMatrix[i][j] > 1 / 2d) color = Color.YELLOW;
                //if (similarityMatrix[i][j] > 3 / 4d) color = Color.RED;
                //if (similarityMatrix[i][j] < 1 / 5d) color = Color.GREEN;
                gc.setFill(color);
                gc.fillRect(scaleX(i + 1), scaleY(j + 1), scaleX(i + 2), scaleY(j + 2));
                gc.setFill(Color.BLACK);
                String similarity = String.format("%.0f%%", similarityMatrix[i][j] * 100);
                gc.fillText(similarity, scaleX(i + 1.5), scaleY(j + 1.5));
            }
        }
        // draw grid
        for (int i = 0; i < gridSize; i++) {
            gc.strokeLine(0, scaleY(i), scaleX(gridSize), scaleY(i));
            gc.strokeLine(scaleX(i), 0, scaleX(i), scaleY(gridSize));
        }
        // draw document names
        gc.setFont(new Font("Arial", getWidth() >= getHeight() ? scaleY(.15) : scaleX(.1)));
        for (int i = 0; i < plagiatDetector.getAllDocuments().size(); i++) {
            String documentName = plagiatDetector.getAllDocuments().get(i).getName();
            documentName = makeStringMultiline(documentName, 15);
            gc.fillText(documentName, scaleX(.5), scaleY(i + 1.5));
            gc.fillText(documentName, scaleX(i + 1.5), scaleY(.5));
        }
    }

    private String makeStringMultiline(String string, int maxLineLength) {
        if (string.length() <= maxLineLength) return string;
        StringBuilder stringBuilder = new StringBuilder(string);
        int index = maxLineLength;
        while (index < stringBuilder.length()) {
            stringBuilder.insert(index, "\n");
            index += maxLineLength;
        }
        return stringBuilder.toString();
    }

}
