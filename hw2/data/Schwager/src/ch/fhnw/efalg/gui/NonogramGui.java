package Schwager.src.ch.fhnw.efalg.gui;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
