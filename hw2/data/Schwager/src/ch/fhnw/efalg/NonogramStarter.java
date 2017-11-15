package Schwager.src.ch.fhnw.efalg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import Schwager.src.ch.fhnw.efalg.algorithm.NonogramSolver;
import Schwager.src.ch.fhnw.efalg.console.NonogramConsole;
import Schwager.src.ch.fhnw.efalg.gui.NonogramGui;

public class NonogramStarter {
	/**
	 * If true - the GUI will be notified on changes
	 */
	public final static boolean UPDATE_CHANGES = true;
	/**
	 * Use GUI
	 */
	public final static boolean USE_GUI = true;

	private static Observer outputChannel;

	/**
	 * Reads in a file, creates a solver, creates a output channel, registers
	 * the output channel in the solver, starts solving.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Choose the nonogram file
			JFileChooser filechooser = new JFileChooser(new File("./"));
			filechooser.setDialogTitle("Select a Nonogram File.");
			filechooser.setMultiSelectionEnabled(false);
			if (filechooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
				return;

			// Read the nonogram file
			Nonogram nonogram = readNonogramFromFile(filechooser.getSelectedFile());

			NonogramSolver s = new NonogramSolver(nonogram);

			// Define output channel
			if (USE_GUI)
				SwingUtilities.invokeLater(() -> {
					outputChannel = new NonogramGui(nonogram);
					s.addObserver(outputChannel);
				});
			else {
				outputChannel = new NonogramConsole(nonogram);
				s.addObserver(outputChannel);
			}
			// Start solve nonogram
			s.startSolving();

		} catch (IOException | NumberFormatException ex) {
			// Couldnt read nonogram file - application will be closed
			return;
		}
	}

	/**
	 * Reads the nonogram from the File
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static Nonogram readNonogramFromFile(File f) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
			String[] lengths = br.readLine().split(" +");
			if (lengths.length < 2)
				throw new IOException("No nonogram file");
			int yLength = Integer.parseInt(lengths[0]);
			int xLength = Integer.parseInt(lengths[1]);
			NonogramValues[] xValues = new NonogramValues[xLength];
			NonogramValues[] yValues = new NonogramValues[yLength];

			int currentLine = 0;
			while (currentLine != yLength) {
				String[] values = br.readLine().trim().split(" +");
				yValues[currentLine] = getNonogramValuesFromString(values, xLength, currentLine, false);
				currentLine++;
			}
			currentLine = 0;
			while (currentLine != xLength) {
				String[] values = br.readLine().trim().split(" +");
				xValues[currentLine] = getNonogramValuesFromString(values, yLength, currentLine, true);
				currentLine++;
			}

			return new Nonogram(xValues, yValues);
		}
	}

	/**
	 * Creates nonogram values for the values in x or y direction.
	 * 
	 * @param values
	 * @param fieldSize
	 * @param index
	 * @param xAxis
	 * @return
	 */
	private static NonogramValues getNonogramValuesFromString(String[] values, int fieldSize, int index,
			boolean xAxis) {
		int[] nonogramValues = new int[values.length];
		if (values.length == 1 && "".equals(values[0]))
			nonogramValues = new int[0];
		for (int i = 0; i < nonogramValues.length; i++) {
			nonogramValues[i] = Integer.parseInt(values[i]);
		}
		return new NonogramValues(nonogramValues, fieldSize, index, xAxis);
	}
}
