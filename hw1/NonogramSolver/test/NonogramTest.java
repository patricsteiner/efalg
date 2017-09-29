import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class NonogramTest {

	@Test
	public void testNextPermutation() {
		int[] vector = {0,0,0,0,0};
		
		
		printVector(vector);
		int i = 1;
		while (Nonogram.nextPermutation(vector, 0)) {
			i++;
			 printVector(vector);
		}
		System.out.println("TOTAL: " + i);

	}
	
	void printVector(int[] vector) {
		for (int i : vector)
			if (i == 0 ) System.out.print("_");
			else System.out.print("X");
		System.out.println();
	}

}
