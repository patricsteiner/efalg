package plagiatDetector.gui;

import javafx.concurrent.Task;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import plagiatDetector.factories.DocumentFactory;
import plagiatDetector.models.Document;
import plagiatDetector.services.PlagiatDetectorService;
import plagiatDetector.util.JavaSourceFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Graphical user interface that shows a similarity-matrix of all the Documents in the DocumentRepository.
 */
public class PlagiatDetectorPane extends BorderPane {

    private PlagiatDetectorService plagiatDetectorService;
    private double[][] similarityMatrix;
    private Canvas canvas;
    private int gridSize;

    public PlagiatDetectorPane(PlagiatDetectorService plagiatDetectorService) throws IOException {
        this.plagiatDetectorService = plagiatDetectorService;

        canvas = new Canvas();
        setCenter(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        TextInputDialog textInputDialog = new TextInputDialog("hw2/data");
        textInputDialog.setTitle("PlagiatDetector");
        textInputDialog.setHeaderText("Please specify the folder containing the java programs");
        String javaSourceFoldersRoot = textInputDialog.showAndWait()
                .orElseThrow(() -> new IllegalArgumentException("javaSourceFoldersRoot needs to be specified"));

        File javaSourceFolders = new File(javaSourceFoldersRoot);

        DocumentFactory documentFactory = plagiatDetectorService.getDocumentFactory();
        for (File javaSourceFolder : javaSourceFolders.listFiles()) {
            Document document = documentFactory.makeDocument(new JavaSourceFolder(javaSourceFolder));
            plagiatDetectorService.addDocument(document);
            // File documentAsFile = new File(Paths.get(javaSourceFolder.getPath(), document.getName().concat(".txt")).toString());
            // Files.write(documentAsFile.toPath(), document.getProcessedContent().getBytes());
        }

        List<Document> documents = plagiatDetectorService.getAllDocuments();

        Task detectorTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                similarityMatrix = new double[documents.size()][documents.size()];
                gridSize = documents.size() + 1; // + 1 because need space for document name
                for (int i = 0; i < documents.size(); i++) {
                    for (int j = 0; j < documents.size(); j++) {
                        Document d1 = documents.get(i);
                        Document d2 = documents.get(j);
                        double similarity = plagiatDetectorService.similarity(d1, d2);
                        similarityMatrix[i][j] = similarity;
                    }
                }
                return null;
            }
        };
        detectorTask.setOnSucceeded(event -> draw());
        new Thread(detectorTask).start();
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
        for (int i = 0; i < plagiatDetectorService.getAllDocuments().size(); i++) {
            String documentName = plagiatDetectorService.getAllDocuments().get(i).getName();
            documentName = wrapString(documentName, 16);
            gc.fillText(documentName, scaleX(.5), scaleY(i + 1.5));
            gc.fillText(documentName, scaleX(i + 1.5), scaleY(.5));
        }
    }

    private String wrapString(String string, int maxLineLength) {
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
