import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Nonogram {
	
	protected final int height, width;
    protected int[][] matrix;
    protected int[][] preprocessed;
    protected int[][] predictions;
    protected List<List<Integer>> rowHints;
    protected List<List<Integer>> colHints;
    
    //protected List<Integer> columnsToCheck = new ArrayList<>();
    
    protected int delay = 0;
    
    protected boolean stopped;
    
    protected int rowCounter = 0; // used to indicate how many rows have been permuted already
    
    public final static int UNKNOWN = 0;
    public final static int FILLED = 1;
    public final static int FIXED = 2;
    public  final static int EMPTY = -1;
    
    public int backtrackCounter = 0;
    public int permutationCounter = 0;
    public long timeSpentPermuting = 0;
    public long timeSpentChecking = 0;

    public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public Nonogram(InputStream in) {
        Scanner scanner = new Scanner(in);
        height = scanner.nextInt();
        width = scanner.nextInt();
        scanner.nextLine();
        rowHints = new ArrayList<>(height);
        colHints = new ArrayList<>(width);
        List<List<Integer>> listToAddThings = rowHints;
        for (int i = 0; i < height + width; i++) {
            if (i >= height) listToAddThings = colHints;
            listToAddThings.add(Arrays.stream(scanner.nextLine().split("\\s+")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList()));
        }
        scanner.close();
        matrix = new int[height][];
        predictions = new int[height][];
        preprocessed = new int[height][];
        for (int i = 0; i < height; i++) {
        	matrix[i] = new int[width];
        	predictions[i] = new int[width];
        	preprocessed[i] = new int[width];
        }
    }

	public boolean solve(int delay) throws InterruptedException {
		stopped = false;
		this.delay = delay;
		preprocess();
		return findSolution(0);
	}
	
	public void stopSolving() {
		stopped = true;
	}
	
	/*protected int[] getRow(int n) {
		return matrix[n];
	}
	
	protected int[] getCol(int n) {
		/*int[] col = new int[width];
		for (int i = 0; i < height; i++) {
			col[i] = matrix[i][n];
		}
		return col;
		return IntStream.range(0, height).map(i -> matrix[i][n]).toArray();
	}*/
	
	public int get(int i, int j) {
		return matrix[i][j];
	}
	
	/*public void set(int i, int j, int val) {
		matrix[i][j] = val;
	}*/

	// make first possible permutation. later needs to consider current "fixed" cells.
	protected void firstPermutation(int i) {
		int j = 0;
    	for (int hint : rowHints.get(i)) {
    		for (int k = 0; k < hint; k++) {
    			matrix[i][j++] = FILLED;
    		}
    		if (j < width) matrix[i][j++] = EMPTY;
    	}
    	while (j < width) matrix[i][j++] = EMPTY;
	}
	
	protected boolean shiftRight(int row) {
		if (matrix[row][width-1] == FILLED) return false;
		for (int j = width - 1; j > 0; j--) {
			matrix[row][j] = matrix[row][j-1];
			matrix[row][j-1] = EMPTY;
		}
		return true;
	}
	
	protected void firstPermutationWithPredictions(int row) {
		firstPermutation(row);
		int firstBlockSize = rowHints.get(row).get(0);
		if (firstBlockSize == 0 || rowHints.get(row).size() < 2) return;
		if (predictions[row][0] == FILLED) return;
		int sumOfAllBlocksButFirst = 0;
		for (int x = 1; x < rowHints.get(row).size(); x++) {
			sumOfAllBlocksButFirst += rowHints.get(row).get(x);
		}
		int filled = 0;
		int unknown = 0;
		int p = width - 1;
		while (filled <= sumOfAllBlocksButFirst) {
			if (predictions[row][p] == FILLED) filled++;
			else if (predictions[row][p] == UNKNOWN) unknown++;
			if (--p < 0) return;
		}
		p++; // last cell of first block shall be placed on p
		int shift = p - firstBlockSize + 1;
		if (shift < 1) return;
		for (int j = width - 1; j > p; j--) {
			matrix[row][j] = matrix[row][j - shift];
			matrix[row][j - shift] = EMPTY;
		}
	}
	
	// reset row to all 0s. later needs to reset to a state where all "fixed" cells are set.
	protected void resetRow(int i) {
		for (int j = 0; j < width; j++) {
			matrix[i][j] = UNKNOWN;
			predictions[i][j] = UNKNOWN;
		}
	}
	
	public void preprocess() {
//		for (List<Integer> hints : rowHints) Collections.reverse(hints);
//		Collections.reverse(colHints);
		
		// rows
		for (int i = 0; i < height; i++) {
			int sumOfHints = rowHints.get(i).stream().mapToInt(Integer::valueOf).sum();
			int minimalOccupiedSpace = sumOfHints + rowHints.get(i).size() - 1; // always 1 space between blocks
			int pos = 0;
			for (int k = 0; k < rowHints.get(i).size(); k++) {
				pos = pos + rowHints.get(i).get(k) - 1;
				int difference = width - minimalOccupiedSpace - rowHints.get(i).get(k);
				System.out.println(difference);
				if (difference < 0) {
					for (int j = pos; j > pos + difference; j--) {
						preprocessed[i][j] = FILLED;
					}
				}
				pos += 2;
			}
		}
		// same for cols
		for (int j = 0; j < width; j++) {
			int sumOfHints = colHints.get(j).stream().mapToInt(Integer::valueOf).sum();
			int minimalOccupiedSpace = sumOfHints + colHints.get(j).size() - 1; // always 1 space between blocks
			int pos = 0;
			for (int k = 0; k < colHints.get(j).size(); k++) {
				pos = pos + colHints.get(j).get(k) - 1;
				int difference = height - minimalOccupiedSpace - colHints.get(j).get(k);
				System.out.println(difference);
				if (difference < 0) {
					for (int i = pos; i > pos + difference; i--) {
						preprocessed[i][j] = FILLED;
					}
				}
				pos += 2;
			}
		}
	}
	
	protected boolean findSolution(int row) throws InterruptedException {
    	if (stopped) return true;
    	//backtrackCounter++;
    	//System.out.println(timeSpentPermuting/1000000 + " vs checking: " + timeSpentChecking/1000000);
    	
    	//if (row >= height /*|| rowCounter > height*/) return false;
        
        if (rowCounter++ == height/* && isSolved()*/) {
        	System.out.println(this);
        	System.out.println("DONE");
        	return true;
        }
        
    	//long time = System.nanoTime();
    	makePrediction(row);

    	boolean nextPermutation = nextPermutation(row);
//    	long permutationCounter = 1;
    	
    	//time = System.nanoTime() - time;
    	//timeSpentPermuting += time;

        while (nextPermutation) {
//        	permutationCounter++;
//        	System.out.println(permutationCounter);
//        	if (permutationCounter > 10000000000L) {
//        		firstPermutation(row);
//        		shiftRight(row);
//        		permutationCounter = 1;
//        	}
        	if (delay > 0) Thread.sleep(delay);
        	//time = System.nanoTime();
        	boolean allGood = matchesPrediction(row) && matchesPreprocessed(row);// && checkCols();
        	//time = System.nanoTime() - time;
        	//timeSpentChecking += time;
        	if (allGood) {
        		if (findSolution(row + 1)) return true;
        	}
        	//time = System.nanoTime();
        	nextPermutation = nextPermutation(row);
        	
        	//time = System.nanoTime() - time;
        	//timeSpentPermuting += time;
        }
        resetRow(row);
        rowCounter--;
        return false;
    }

    /*public boolean isSolved() {
        /*for (int i = 0; i < height; i++) {
            List<Integer> blockLengths = getBlockLenghts(getRow(i));
            for (int j = 0; j < rowHints.get(i).size(); j++) {
                if (rowHints.get(i).get(j) != blockLengths.get(j)) return false;
            }
        }
        for (int i = 0; i < width; i++) {
            List<Integer> blockLengths = getBlockLenghts(getCol(i));
            for (int j = 0; j < colHints.get(i).size(); j++) {            	
                if (colHints.get(i).size() != blockLengths.size() || colHints.get(i).get(j) != blockLengths.get(j)) 
                	return false;
            }
        }
        return true;
    }*/

    // XXX Make a compareBlckLEngths fucntion isntead to save memory (no array return)
    /*protected List<Integer> getBlockLenghts(int[] vector) {
        List<Integer> blockLengths = new ArrayList<>();
        int blockLength = 0;
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] == 1) blockLength++;
            else if (blockLength > 0){
                blockLengths.add(blockLength);
                blockLength = 0;
            }
        }
        if (blockLength > 0)
            blockLengths.add(blockLength);
        if (blockLengths.size() == 0) blockLengths.add(0);
        return blockLengths;
    }*/

    // only need to check cols, because rows will never be invalid due to permutation function
    // ein array mit allem möglichen (max) blocklängen machen. wenn sich das dann überschneidet mit den hints, ist ok. abbruch nur, wenn effektiv ein fehler schon erkannt wird.
    protected boolean checkCols() {
		for (int j = 0; j < width; j++) {
			if (!checkCol(j)) return false;
		}
    	return true;
    }
    
    protected boolean checkCol(int j) {
    	Iterator<Integer> hints = colHints.get(j).iterator();
    	int definiteBlockSize = 0;
    	int hint = hints.next();
    	for (int i = 0; i < height; i++) {
    		if (matrix[i][j] == FILLED) { 
    			definiteBlockSize++;
    			if (definiteBlockSize > hint) return false;
    		}
    		else if (matrix[i][j] == EMPTY && definiteBlockSize > 0) {
    			if (definiteBlockSize != hint) return false;
    			definiteBlockSize = 0;
    			if (hints.hasNext()) hint = hints.next();
    			else hint = 0; // even if we already used all hints, still continue and make sure only EMPTY cells follow.
    		}
    		else if (matrix[i][j] == UNKNOWN) return true;
    	}
    	return true;
    }
    
    // assumes everything is correct down to row, so nothing is actually checked for correctness.
    protected void makePrediction(int row) {
    	if (row == 0) return;
    	for (int j = 0; j < width; j++) {
	    	Iterator<Integer> hints = colHints.get(j).iterator();
	    	int blockSize = 0;
	    	int hint = hints.next();
	    	for (int i = 0; i < row; i++) {
	    		if (matrix[i][j] == FILLED) {
	    			blockSize++;
	    		}
	    		else if (matrix[i][j] == EMPTY) {
	    			if (blockSize > 0) hint = hints.hasNext() ? hints.next() : 0;
	    			blockSize = 0;
	    		}
	    	}
    		if (blockSize > 0 && blockSize < hint) {
    			predictions[row][j] = FILLED;
    		}
    		else if (blockSize == hint) {
    			predictions[row][j] = EMPTY;
    		}
    		else {
    			predictions[row][j] = UNKNOWN;
    		}
    	}
    }
    
    protected boolean matchesPrediction(int row) {
    	for (int j = 0; j < width; j++) {
    		if (predictions[row][j] != UNKNOWN && matrix[row][j] != predictions[row][j]) {
    			return false;
    		}
    	}
    	return true;
    }
    
    protected boolean matchesPreprocessed(int row) {
    	for (int j = 0; j < width; j++) {
    		if (preprocessed[row][j] != UNKNOWN && matrix[row][j] != preprocessed[row][j]) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /*protected void setColumnsToCheck(int... cols) {
    	columnsToCheck.clear();
    	for (int col : cols) columnsToCheck.add(col);
    }*/
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            for (int i : row) sb.append(i == UNKNOWN ? "?" : i == EMPTY ? "0" : "1");
            sb.append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Computes the next permutation of the given row of the matrix.
     * Blocks will be maintained for all permutations, space between blocks is always at least 1.
     * @param row row number of the matrix
     * @return true if another permutation was found, false otherwise
     */
    protected boolean nextPermutation(int row) {
    	int rightMostBlockSize = rowHints.get(row).get(rowHints.get(row).size() - 1);
    	if (rightMostBlockSize == 0) return false;
    	int[] vector = matrix[row];
    	if (vector[0] == UNKNOWN) {
    		//columnsToCheck.clear();
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
    		//setColumnsToCheck(rightMostBlockPosition, rightMostBlockPosition + rightMostBlockSize);
    		return true;
    	}
    	// If there is no space after the rightmost block: 
    	// try to find the next block from the right that can be moved to the right
    	int i = rightMostBlockPosition - 2; // it is at least 2 units further away (there is always a space between blocks)
    	if (i < 0) return false; // if there is no such block: no more permutations possible
    	int emptyCells = 1; // count the empty cells between blocks
    	// do until we found a block and there are at least 2 spaces right to it
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
    	//setColumnsToCheck(i, i + filledCells);
    	// move all the other blocks as far to the left as possible (zeros is used as offset basically)
    	// the +2 / -2 is there because starting from the rightmost side of the found block (i + ones), 
    	// we don't just need to go 1 unit to the right, but 2, since there is a space between each block.
    	for (int j = i + filledCells + 2; j < vector.length - emptyCells + 2; j++) {
    		if (j != j + emptyCells - 2) {
	    		vector[j] = vector[j + emptyCells - 2];
	    		vector[j + emptyCells - 2] = EMPTY;
	    		//columnsToCheck.add(j);
	    		//columnsToCheck.add(j + emptyCells - 2);
    		}
    	}
    	return true;
    }
}