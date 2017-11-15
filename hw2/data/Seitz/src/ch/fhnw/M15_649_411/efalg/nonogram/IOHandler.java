package ch.fhnw.M15_649_411.efalg.nonogram;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

//IO Handler, handles all IO operations
//Singleton class

/**
 * created on 26.09.2017
 *
 * @author Claudio
 * @version 1.0
 */
public class IOHandler {
    private static IOHandler instance;

    public static IOHandler getInstance() {
        if (instance == null) instance = new IOHandler();
        return instance;
    }

    /**
     * Generates a new Nonoboard dependent of the selected file
     *
     * @return new Nonoboard
     */
    public NonoBoard selectFile() {
        try {
            File file = chooseOpenFile();
            return loadFile(file);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Nnonogram initialization failed.\n" + e.getMessage());
            alert.showAndWait();
        }
        return null;
    }

    /**
     * Opens an OpenDialog for selecting a *.txt file
     * Selected file should be an readable format to initialize a Nonoboard
     *
     * @return the selected *.txt file
     */
    private File chooseOpenFile() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select text file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text file", "*.txt"));
        return fileChooser.showOpenDialog(null);
    }

    /**
     * Generates a new Nonoboard dependent of the selected file
     *
     * @param file input file, should be an readable format to initialize a Nonoboard
     * @return new Nonoboard
     * @throws Exception Nonoboard initialization error
     */
    public NonoBoard loadFile(File file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String[] rc = br.readLine().split("\\s+");
            int rows = Integer.parseInt(rc[0]);
            int columns = Integer.parseInt(rc[1]);
            int[][] xVecs = new int[rows][];
            int[][] yVecs = new int[columns][];
            initVecs(xVecs, br);        //read horizontal vectors, x dir
            initVecs(yVecs, br);        //read vertical vectors, y dir
            return new NonoBoard(xVecs, yVecs);
        }
    }

    /**
     * Initializes the vectors of a Nonoboard
     *
     * @param array array to store vector lengths
     * @param br    file reader
     * @throws Exception illegal nonogram format
     */
    private void initVecs(int[][] array, BufferedReader br) throws Exception {
        for (int i = 0; i < array.length; i++) {
            String[] data = br.readLine().split("\\s+");
            int length = data.length;
            int[] vecs = new int[length];
            for (int j = 0; j < length; j++) vecs[j] = Integer.parseInt(data[j]);
            array[i] = vecs;
        }
    }
}
