package Schwager.src.ch.fhnw.efalg;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.Observer;

/**
 * Helper class for values
 * @author John Doe
 *
 */
public class NonogramValues implements Comparable<NonogramValues> {

	private final int[] values;

	private final int index;

	private final boolean xAxis;

	private final int[] startSpaces;

	private int size;

	public NonogramValues(int[] values, int fieldSize, int index, boolean xAxis) {
		super();
		this.values = values;
		for (int val : values) {
			this.size += val;
		}
		this.size += values.length - 1;
		this.index = index;
		this.xAxis = xAxis;
		if (values.length == 0)
			startSpaces = new int[1];
		else
			startSpaces = new int[values.length + 1];
		for (int x = 1; x < startSpaces.length - 1; x++)
			startSpaces[x] = 1;
		startSpaces[0] = 0;
		startSpaces[startSpaces.length - 1] = fieldSize - size;
	}

	public int[] getValues() {
		return values;
	}

	public int getIndex() {
		return index;
	}

	public boolean isXAxis() {
		return xAxis;
	}

	public int getSize() {
		return size;
	}

	/* main lookup func */
	public int[] getStartSpaces() {
		return startSpaces;
	}
    
	public int compareTo(NonogramValues val) {
		if (size - val.size == 0)
			return index - val.getIndex();
		return size - val.size;
	}
    
	public boolean nextSpacesBetween(int fieldSize, int[] spacesBetween) {
		// if first element has all variable whitespaces -> no next permutation
		if (spacesBetween[0] == fieldSize - size)
			return false;
		// if last element has values - move it to the second last.
		if (spacesBetween[spacesBetween.length - 1] > 0) {
			spacesBetween[spacesBetween.length - 1]--;
			spacesBetween[spacesBetween.length - 2]++;
			return true;
		}
		// try to move a single whitespace closer to the beginning
		int i = spacesBetween.length - 2;
		while (i > 0) {
			if (spacesBetween[i] == 1) {
				i--;
			} else {
				spacesBetween[spacesBetween.length - 1] = spacesBetween[i] - 2;
				spacesBetween[i - 1]++;
				spacesBetween[i] = 1;
				return true;
			}
		}
		return false;
	}
    
	public int[] getFixedFields(int[] currentBoardRow) {
		int[] newBoardRow = null;
		int[] spacesBetween = Arrays.copyOf(startSpaces, startSpaces.length);
		int[] permutation = new int[currentBoardRow.length];
		do {
			if (createValidPermutation(currentBoardRow, permutation, spacesBetween)) {
				if (newBoardRow == null) {
					newBoardRow = Arrays.copyOf(permutation, permutation.length);
				} else {
					for (int i = 0; i < permutation.length; i++)
						if (newBoardRow[i] + permutation[i] == 0)
							newBoardRow[i] = 0;
				}
			}
		} while (nextSpacesBetween(permutation.length, spacesBetween));
		return newBoardRow;
	}
    
	public boolean createValidPermutation(int[] currentBoardRow, int[] permutation, int[] spacesBetween) {
		int valIndex = 0;
		int whiIndex = 0;
		int tiotal = 0;
		for (int i = 0; i < permutation.length; i++) {
			if (valIndex == whiIndex) {
				if (spacesBetween[whiIndex] == tiotal) {
					if (currentBoardRow[i] == -1)
						return false;
					tiotal = 0;
					permutation[i] = 1;
					whiIndex++;
				} else {
					if (currentBoardRow[i] == 1)
						return false;
					permutation[i] = -1;
				}
			} else {
				if (values[valIndex] == tiotal) {
					if (currentBoardRow[i] == 1)
						return false;
					tiotal = 0;
					permutation[i] = -1;
					valIndex++;
				} else {
					if (currentBoardRow[i] == -1)
						return false;
					permutation[i] = 1;
				}
			}
			tiotal++;
		}
		return true;
	}
}
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

    public final static boolean UPDATE_CHANGES = true;

    public final static boolean shouldUseGIUorNOt = true;

    private static Observer outputChannel;


    public static void main(String[] args) {
        try {
            JFileChooser filechooser = new JFileChooser(new File("./"));
            filechooser.setDialogTitle("Select a Nonogram File.");
            filechooser.setMultiSelectionEnabled(false);
            if (filechooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;

            }


            Nonogram nonogram = readNonogramFromFile(filechooser.getSelectedFile());

            NonogramSolver s = new NonogramSolver(nonogram);

            if (shouldUseGIUorNOt)
                SwingUtilities.invokeLater(() -> {
                    outputChannel = new NonogramGui(nonogram);
                    s.addObserver(outputChannel);
                });
            else {
                outputChannel = new NonogramConsole(nonogram);
                s.addObserver(outputChannel);
            }


            s.startSolving();

        } catch (IOException | NumberFormatException ex) {


            return;
        }
    }

    /*ready*/
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
