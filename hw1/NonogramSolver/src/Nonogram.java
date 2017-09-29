import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Nonogram {
    final int height, width;
    int[][] matrix;
    List<List<Integer>> rowHints;
    List<List<Integer>> colHints;
    List<NonogramListener> listeners = new ArrayList<>();
    
    int rowCounter = 0; // used to indicate how many rows have been permuted already
    
    final static int UNKNOWN = 0;
    final static int FILLED = 1;
    final static int EMPTY = -1;
    
    
    public int backtrackCounter = 0;

    public void addListener(NonogramListener listener) {
    	listeners.add(listener);
    }
    
    public void removeListener(NonogramListener listener) {
    	//NonogramListener l;
    	//for (NonogramListener nl : listeners) if (nl.equals(listener)) l = nl;
    	listeners.remove(listener);
    }
    
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
        for (int i = 0; i < height; i++) {
        	matrix[i] = new int[width];
        }
        
       /* matrix = new int[][]{
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0}
                /*{0,1,1,0,1},
                {0,1,0,1,1},
                {1,0,0,0,1},
                {1,1,1,1,1},
                {0,0,1,1,0}
        };*/
    }

	int[] getRow(int n) {
		return matrix[n];
	}
	
	int[] getCol(int n) {
		/*int[] col = new int[width];
		for (int i = 0; i < height; i++) {
			col[i] = matrix[i][n];
		}
		return col;*/
		return IntStream.range(0, height).map(i -> matrix[i][n]).toArray();
	}
	
	int get(int i, int j) {
		return matrix[i][j];
	}
	
	void set(int i, int j, int val) {
		matrix[i][j] = val;
	}

	// make first possible permutation. later needs to consider current "fixed" cells.
	void firstPermutation(int i) {
		int j = 0;
    	for (int hint : rowHints.get(i)) {
    		for (int k = 0; k < hint; k++) {
    			matrix[i][j++] = FILLED;
    		}
    		if (j < width) matrix[i][j++] = EMPTY;
    	}
    	while (j < width) matrix[i][j++] = EMPTY;
	}
	
	// reset row to all 0s. later needs to reset to a state where all "fixed" cells are set.
	void resetRow(int i) {
		for (int j = 0; j < width; j++) matrix[i][j] = UNKNOWN;
	}
	
    boolean findSolution(int row) {
    	notifyListeners();
    	try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	backtrackCounter++;
    	if (backtrackCounter % 1000 == 0) {
    		System.out.println("-----------------------");
    		System.out.println(this);
        	System.out.println("-----------------------");
        	
    	}
    	
    	
    	//if (row >= height /*|| rowCounter > height*/) return false;
        
        if (rowCounter++ == height && isSolved()) {
        	System.out.println(this);
        	System.out.println("DONE");
        	notifyListeners();
        	return true;
        }
        
        while (nextPermutation(row)) {
        	if (checkCols()) {
        		if (findSolution(row + 1)) return true;
        	}
        }
        resetRow(row);
        rowCounter--;
        return false;
    }

    boolean isSolved() {
        /*for (int i = 0; i < height; i++) {
            List<Integer> blockLengths = getBlockLenghts(getRow(i));
            for (int j = 0; j < rowHints.get(i).size(); j++) {
                if (rowHints.get(i).get(j) != blockLengths.get(j)) return false;
            }
        }*/
        for (int i = 0; i < width; i++) {
            List<Integer> blockLengths = getBlockLenghts(getCol(i));
            for (int j = 0; j < colHints.get(i).size(); j++) {            	
                if (colHints.get(i).size() != blockLengths.size() || colHints.get(i).get(j) != blockLengths.get(j)) 
                	return false;
            }
        }
        return true;
    }

    // XXX Make a compareBlckLEngths fucntion isntead to save memory (no array return)
    List<Integer> getBlockLenghts(int[] vector) {
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
    }

    // only need to check cols, because rows will never be invalid due to permutation function
    // ein array mit allem möglichen (max) blocklängen machen. wenn sich das dann überschneidet mit den hints, ist ok. abbruch nur, wenn effektiv ein fehler schon erkannt wird.
    boolean checkCols() {
    	for (int j = 0; j < width; j++) {
	    	if (!checkCol(j)) return false;
    	}
    	return true;
    }
    
    boolean checkCol(int j) {
    	Iterator<Integer> hints = colHints.get(j).iterator();
    	int definiteBlockSize = 0;
    	//int possibleBlockSize = 0;
    	int hint = hints.next();
    	for (int i = 0; i < height; i++) {
    		//if (matrix[i][j] == UNKNOWN) possibleBlockSize++;
    		if (matrix[i][j] == FILLED) { 
    			definiteBlockSize++;
    			if (definiteBlockSize > hint) return false;
    		/*possibleBlockSize++;*/ 
    		}
    		else if (matrix[i][j] == EMPTY && definiteBlockSize > 0) {
    			if (definiteBlockSize != hint) return false;
    			definiteBlockSize = 0;
    			if (hints.hasNext()) hint = hints.next();
    			else hint = 0; // even if we already used all hints, still continue and make sure only EMPTY cells follow.
    			/*possibleBlockSize = 0;*/ 
    		}
    		else if (matrix[i][j] == UNKNOWN) return true; //for now just accept it and dont do further research
    	}
    	return true;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            for (int i : row) sb.append(i == UNKNOWN ? "_" : i == EMPTY ? "." : "X");
            sb.append("\n");
        }
        return sb.toString();
    }

    public void notifyListeners() {
    	listeners.forEach(l -> l.update());
    }
    
    /**
     * Computes the next permutation of the given vector. The vector must consist of blocks of 0 and 1.
     * Blocks will be maintained for all permutations, space between blocks is always at least 1.
     * To get all permutations, the vector must start with all the blocks left aligned with 1 space between each block.
     * @param row
     * @return true if another permutation was found, false otherwise
     */
    boolean nextPermutation(int row) {
    	int rightMostBlockSize = rowHints.get(row).get(rowHints.get(row).size() - 1);
    	if (rightMostBlockSize == 0) return false;
    	int[] vector = matrix[row];
    	if (vector[0] == UNKNOWN) {
    		firstPermutation(row);
    		return true;
    	}
    	/*// check if the vector was reset (indicated by first val -2). Done so the initial vector state is returned
    	// the first time the function is called.
    	if (vector[0] < 0) {
    		vector[0] += 2;
    		return true;
    	}*/
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
    	// move all the other blocks as far to the left as possible (zeros is used as offset basically)
    	// the +2 / -2 is there because starting from the rightmost side of the found block (i + ones), 
    	// we don't just need to go 1 unit to the right, but 2, since there is a space between each block.
    	for (int j = i + filledCells + 2; j < vector.length - emptyCells + 2; j++) {
    		if (j != j + emptyCells - 2) {
	    		vector[j] = vector[j + emptyCells - 2];
	    		vector[j + emptyCells - 2] = EMPTY;
    		}
    	}
    	return true;
    }
}