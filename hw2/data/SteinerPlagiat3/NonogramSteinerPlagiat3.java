package testdata;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class NonogramSteinerPlagiat3 {
	
	private final int myWidth, myHeight;
	
    private int[][] myMatrix;
    
    private int[][] myPreprocessed;
    
    private int[][] myPredictions;
    
    private List<List<Integer>> myRowHints;

    private List<List<Integer>> myColHints;
    
    private int myDelay = 0;
    
    private boolean myStopped;
    
    private int myRowCounter = 0;

    public final static int UNKNOWN = 0;
    public final static int FILLED = 1;
    public  final static int EMPTY = -1;

    public int getHeight() {
		return myHeight;
	}

	public int getWidth() {
		return myWidth;
	}
	
    public List<List<Integer>> getRowHints() {
		return myRowHints;
	}

	public List<List<Integer>> getColHints() {
		return myColHints;
	}

	public int get(int i, int j) {
		return myMatrix[i][j];
	}
	
	public int getPrediction(int i, int j) {
		return myPredictions[i][j];
	}
	
	public int getPreprocessed(int i, int j) {
		return myPreprocessed[i][j];
	}
	
	public NonogramSteinerPlagiat3(InputStream in) {
        Scanner scanner = new Scanner(in);
        myHeight = scanner.nextInt();
        myWidth = scanner.nextInt();
        scanner.nextLine();
        myRowHints = new ArrayList<>(myHeight);
        myColHints = new ArrayList<>(myWidth);
        List<List<Integer>> listToAddThings = myRowHints;
        // read all the hints and store them
        for (int i = 0; i < myHeight + myWidth; i++) {
            if (i >= myHeight) listToAddThings = myColHints;
            listToAddThings.add(Arrays.stream(scanner.nextLine().split("\\s+")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList()));
        }
        scanner.close();
        // initialize all the arrays
        myMatrix = new int[myHeight][];
        myPredictions = new int[myHeight][];
        myPreprocessed = new int[myHeight][];
        for (int i = 0; i < myHeight; i++) {
        	myMatrix[i] = new int[myWidth];
        	myPredictions[i] = new int[myWidth];
        	myPreprocessed[i] = new int[myWidth];
        }
    }

	public boolean solve(int delay) throws InterruptedException {
		myStopped = false;
		this.myDelay = delay;
		preprocess();
		return findSolution(0); // start at row 0
	}

	public void stopSolving() {
		myStopped = true;
	}
	
	private void firstPermutation(int i) {
		int j = 0;
    	for (int hint : myRowHints.get(i)) {
    		for (int k = 0; k < hint; k++) {
    			myMatrix[i][j++] = FILLED;
    		}
    		if (j < myWidth) myMatrix[i][j++] = EMPTY;
    	}
    	while (j < myWidth) myMatrix[i][j++] = EMPTY;
	}
	
	private void firstPermutationWithPredictions(int row) {
		firstPermutation(row);
		int firstBlockSize = myRowHints.get(row).get(0);
		if (firstBlockSize == 0 || myRowHints.get(row).size() < 2) return;
		if (myPredictions[row][0] == FILLED) return;
		int sumOfAllBlocksButFirst = 0;
		for (int x = 1; x < myRowHints.get(row).size(); x++) {
			sumOfAllBlocksButFirst += myRowHints.get(row).get(x);
		}
		int filled = 0;
		//int unknown = 0;
		int p = myWidth - 1;
		while (filled <= sumOfAllBlocksButFirst) {
			if (myPredictions[row][p] == FILLED) filled++;
			//else if (predictions[row][p] == UNKNOWN) unknown++;
			if (--p < 0) return;
		}
		p++; // last cell of first block shall be placed on p
		int shift = p - firstBlockSize + 1;
		if (shift < 1) return;
		for (int j = myWidth - 1; j > p; j--) {
			myMatrix[row][j] = myMatrix[row][j - shift];
			myMatrix[row][j - shift] = EMPTY;
		}
	}
	
	private void resetRow(int i) {
		for (int j = 0; j < myWidth; j++) {
			myMatrix[i][j] = UNKNOWN;
			myPredictions[i][j] = UNKNOWN;
		}
	}

	private void preprocess() {
		for (int i = 0; i < myHeight; i++) {
			int sumOfHints = myRowHints.get(i).stream().mapToInt(Integer::valueOf).sum();
			int minimalOccupiedSpace = sumOfHints + myRowHints.get(i).size() - 1; // always 1 space between blocks
			int pos = 0;
			for (int k = 0; k < myRowHints.get(i).size(); k++) {
				pos = pos + myRowHints.get(i).get(k) - 1;
				int difference = myWidth - minimalOccupiedSpace - myRowHints.get(i).get(k);
				if (difference < 0) {
					for (int j = pos; j > pos + difference; j--) {
						myPreprocessed[i][j] = FILLED;
					}
				}
				pos += 2;
			}
		}
		for (int j = 0; j < myWidth; j++) {
			int sumOfHints = myColHints.get(j).stream().mapToInt(Integer::valueOf).sum();
			int minimalOccupiedSpace = sumOfHints + myColHints.get(j).size() - 1; // always 1 space between blocks
			int pos = 0;
			for (int k = 0; k < myColHints.get(j).size(); k++) {
				pos = pos + myColHints.get(j).get(k) - 1;
				int difference = myHeight - minimalOccupiedSpace - myColHints.get(j).get(k);
				if (difference < 0) {
					for (int i = pos; i > pos + difference; i--) {
						myPreprocessed[i][j] = FILLED;
					}
				}
				pos += 2;
			}
		}
	}

	private boolean findSolution(int row) throws InterruptedException {
    	if (myStopped) return true;
    	
        if (myRowCounter++ == myHeight) {
        	System.out.println("DONE :D");
        	System.out.println(this);
        	return true;
        }
    	makePrediction(row);
        while (nextPermutation(row)) {
        	if (myDelay > 0) Thread.sleep(myDelay);
        	if (matchesPrediction(row) && matchesPreprocessed(row)) {
        		if (findSolution(row + 1)) return true;
        	}
        }
        resetRow(row);
        myRowCounter--;
        return false;
    }
    
    private void makePrediction(int row) {
    	if (row == 0) return;
    	for (int j = 0; j < myWidth; j++) {
	    	Iterator<Integer> hints = myColHints.get(j).iterator();
	    	int blockSize = 0;
	    	int hint = hints.next();
	    	for (int i = 0; i < row; i++) {
	    		if (myMatrix[i][j] == FILLED || myPreprocessed[i][j] == FILLED) {
	    			blockSize++;
	    		}
	    		else if (myMatrix[i][j] == EMPTY || myPreprocessed[i][j] == EMPTY) {
	    			if (blockSize > 0) hint = hints.hasNext() ? hints.next() : 0;
	    			blockSize = 0;
	    		}
	    	}
    		if (blockSize > 0 && blockSize < hint) {
    			myPredictions[row][j] = FILLED;
    		}
    		else if (blockSize == hint) {
    			myPredictions[row][j] = EMPTY;
    		}
    		else {
    			myPredictions[row][j] = UNKNOWN;
    		}
    	}
    }
    
    private boolean matchesPrediction(int row) {
    	for (int j = 0; j < myWidth; j++) {
    		if (myPredictions[row][j] != UNKNOWN && myMatrix[row][j] != myPredictions[row][j]) {
    			return false;
    		}
    	}
    	return true;
    }

    private boolean matchesPreprocessed(int row) {
    	for (int j = 0; j < myWidth; j++) {
    		if (myPreprocessed[row][j] != UNKNOWN && myMatrix[row][j] != myPreprocessed[row][j]) {
    			return false;
    		}
    	}
    	return true;
    }
    
    private boolean nextPermutation(int row) {
    	int rightMostBlockSize = myRowHints.get(row).get(myRowHints.get(row).size() - 1);
    	if (rightMostBlockSize == 0) return false;
    	int[] vector = myMatrix[row];
    	if (vector[0] == UNKNOWN) {
    		firstPermutationWithPredictions(row);
    		return true;
    	}
    	// First step: find the starting position of the rightmost block
    	int rightMostBlockPosition = vector.length - 1;
    	while (vector[rightMostBlockPosition] == EMPTY && rightMostBlockPosition > 0) {
    		rightMostBlockPosition--;
    	}
    	rightMostBlockPosition -= rightMostBlockSize - 1;
    	// if there is empty space after rightmost block
    	if (rightMostBlockPosition + rightMostBlockSize < vector.length) { 
    		// move the last block to the right by 1 unit --> this is the next permutation
    		vector[rightMostBlockPosition] = EMPTY;
    		vector[rightMostBlockPosition + rightMostBlockSize] = FILLED;
    		return true;
    	}
    	int i = rightMostBlockPosition - 2; // it is at least 2 units further away (there is always a space between blocks)
    	if (i < 0) return false; // if there is no such block: no more permutations possible
    	int emptyCells = 1;
    	while(vector[i] == EMPTY || vector[i+1] != EMPTY || vector[i+2] != EMPTY) {
    		if (vector[i] == EMPTY) emptyCells++;
    		else emptyCells = 0;
    		i--;
    		if (i < 0) return false;
    	}
    	if (emptyCells < 1) return false; // if there is not at least 1 space: no more permutations
    	int filledCells = 1; // now count the size of the block we found (it's at least 1 unit long)
    	while (i > 0 && vector[i-1] == FILLED) {
    		filledCells++;
    		i--;
    	}
    	vector[i] = EMPTY; // "move" the found block 1 unit to right
    	vector[i + filledCells] = FILLED;
    	for (int j = i + filledCells + 2; j < vector.length - emptyCells + 2; j++) {
    		if (j != j + emptyCells - 2) {
	    		vector[j] = vector[j + emptyCells - 2];
	    		vector[j + emptyCells - 2] = EMPTY;
    		}
    	}
    	return true;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : myMatrix) {
            for (int i : row) sb.append(i == UNKNOWN ? "?" : i == EMPTY ? "0" : "1");
            sb.append("\n");
        }
        return sb.toString();
    }
}