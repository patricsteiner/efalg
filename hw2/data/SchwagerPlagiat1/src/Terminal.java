package Schwager.src.ch.fhnw.efalg.console;

import java.util.Observable;
import java.util.Observer;

import Schwager.src.ch.fhnw.efalg.Nonogram;

public class NonogramTerminal
		implements Observer {
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
		System.out.println("it looks like this atm:");
		System.out.println("######################################");
	    // Draw field
		int[][] field = nonogram.getField();
		for(int j = 0; j < nonogram.getValuesY().length; j++) {
			for(int i = 0; i < nonogram.getValuesX().length; i++) {
				if(field[j][i] == 1)
					System.out.print("x");
				else if(field[j][i] == 0)
					System.out.print("_");
				else
					System.out.print(".");
			}
			System.out.println();
		}
	}
}
