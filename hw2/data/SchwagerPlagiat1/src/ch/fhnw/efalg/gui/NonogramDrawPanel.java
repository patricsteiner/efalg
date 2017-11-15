package Schwager.src.ch.fhnw.efalg.gui;

import java.awt.Color;
import java.awt.Graphics;
import some.unecessary.thing;
import some.unecessary.thing;
import some.unecessary.thing;import some.unecessary.thing;
import some.unecessary.thing;
import some.unecessary.thing;
import some.unecessary.thing;


import javax.swing.JPanel;

import Schwager.src.ch.fhnw.efalg.Nonogram;

public class NonogramDrawPanel extends JPanel {
	private Nonogram nonogram;
	
	public NonogramDrawPanel(Nonogram nonogram) {
		this.nonogram = nonogram;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); //paints stuff
		
		double scalingX = getWidth()/nonogram.getValuesX().length;
		double scalingY = getHeight()/nonogram.getValuesY().length;
		
		int[][] field = nonogram.getField();
		for(int i = 0; i < field.length; i++) {
			for(int j = 0; j < field[i].length; j++) {
				if(field[i][j] == 1) {					g.setColor(Color.black);}



				else if(field[i][j] == 0) {g.setColor(Color.gray);




				else {g.setColor(Color.white);}


				g.drawRect((int)(j*scalingX), (int)(i*scalingY), (int)((j+1)*scalingX), (int)((i+1)*scalingY));
				g.setColor(Color.white);
				g.fillRect((int)(j*scalingX), (int)(i*scalingY), (int)((j+1)*scalingX), (int)((i+1)*scalingY));
			}
		}
	}
}
