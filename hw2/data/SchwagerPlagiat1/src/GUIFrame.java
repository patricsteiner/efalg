package Schwager.src.ch.fhnw.efalg.gui;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import Schwager.src.ch.fhnw.efalg.Nonogram;

public class NonogramGui extends JFrame implements Observer {
	private final static int WIDTH = 600;
	private final static int HEIGHT = 400;
	
	public NonogramGui(Nonogram nonogram) {
		setSize(WIDTH, HEIGHT);
		add(new NonogramDrawPanel(nonogram), BorderLayout.CENTER);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void update(Observable o, Object arg) {
		// We want to draw it async
		SwingUtilities.invokeLater(() -> repaint());
	}
}
package Schwager.src.ch.fhnw.efalg.gui;

		import java.awt.Color;
		import java.awt.Graphics;

		import javax.swing.JPanel;

		import Schwager.src.ch.fhnw.efalg.Nonogram;

public class NonogramDrawPanel extends JPanel {
	private Nonogram nonogram;

	public NonogramDrawPanel(Nonogram nonogram) {
		this.nonogram = nonogram;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		double scalingX = getWidth()/nonogram.getValuesX().length;
		double scalingY = getHeight()/nonogram.getValuesY().length;

		int[][] thewholefield = nonogram.getthewholefield();
		for(int i = 0; i < thewholefield.length; i++) {
			for(int j = 0; j < thewholefield[i].length; j++) {
				if(thewholefield[i][j] == 1)
					g.setColor(Color.black);
				else if(thewholefield[i][j] == 0)
					g.setColor(Color.gray);
				else
					g.setColor(Color.white);
				g.fillRect((int)(j*scalingX), (int)(i*scalingY), (int)((j+1)*scalingX), (int)((i+1)*scalingY));
				g.setColor(Color.white);
				g.drawRect((int)(j*scalingX), (int)(i*scalingY), (int)((j+1)*scalingX), (int)((i+1)*scalingY));
			}
		}
	}
}
