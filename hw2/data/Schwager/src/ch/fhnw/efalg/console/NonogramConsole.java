package Schwager.src.ch.fhnw.efalg.console;

import java.util.Observable;
import java.util.Observer;

import Schwager.src.ch.fhnw.efalg.Nonogram;

public class NonogramConsole implements Observer {
	private Nonogram nonogram;
	
	public NonogramConsole(Nonogram nonogram) {
		this.nonogram = nonogram;
		printNonogram();
	}

	@Override
	public void update(Observable o, Object arg) {
		printNonogram();
	}
	
	public void printNonogram() {
		// Make some space in the console
		System.out.println();
		System.out.println("Your current nonogram:");
		System.out.println();
	    // Draw field
		int[][] field = nonogram.getField();
		for(int j = 0; j < nonogram.getValuesY().length; j++) {
			for(int i = 0; i < nonogram.getValuesX().length; i++) {
				if(field[j][i] == 1)
//					System.out.print("\u25AE");
					System.out.print("X");
				else if(field[j][i] == 0)
					System.out.print("?");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}
}
