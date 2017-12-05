package ch.fhnw.edu.efalg.graph.gui.impl.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState;

/**
 * Saves a program state into a file.
 * 
 * @author Martin Schaub
 * 
 * @param <V> vertex type
 * @param <E> edge type
 */
public final class SaveAction<V extends Vertex, E extends Edge> extends AbstractAction {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -6209615222457638429L;

	/**
	 * Program state to save.
	 */
	private final ProgramState<V, E> programState;

	/**
	 * Constructor
	 * 
	 * @param programState current program state to save
	 */
	public SaveAction(final ProgramState<V, E> programState) {
		if (programState == null) {
			throw new NullPointerException();
		}
		this.programState = programState;
	}

	/**
	 * Displays a file chooser dialog and guides the user trough the process.
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(programState.getFileChooserDirectory());
		chooser.setFileFilter(new FileNameExtensionFilter("graph files", "graph"));

		boolean saved = false;
		while (!saved && chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

			// Add the extension, if there is no one.
			File toWrite = chooser.getSelectedFile();
			if (!toWrite.getName().endsWith(".graph") && !toWrite.getName().matches(".+\\.[^.]+")) {
				toWrite = new File(toWrite.getAbsoluteFile() + ".graph");
			}

			// Overwrite the file?
			boolean write = false;
			if (toWrite.exists()) {
				int ans = JOptionPane.showConfirmDialog(null, "File already exists. Do you want to overwrite it?",
						"File already exists", JOptionPane.YES_NO_OPTION);
				if (ans == JOptionPane.YES_OPTION) {
					write = true;
				}
			}
			else {
				// If the files does not exist, write it!
				write = true;
			}

			if (write) {
				try {
					serializeToFile(toWrite);
					saved = true;
				}
				catch (final IOException ex) {
					JOptionPane.showMessageDialog(null, "Error saving file! " + ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		programState.setFileChooserDirectory(chooser.getCurrentDirectory());
	}

	/**
	 * Does the actual save process.
	 * 
	 * @param toWrite destination file
	 * @throws IOException IO error occurred
	 */
	private void serializeToFile(final File toWrite) throws IOException {

		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(toWrite));
		synchronized (programState) {
			out.writeObject(programState.getEdgeFactory());
			out.writeObject(programState.getVertexFactory());
			out.writeObject(programState.getEdgePanelHelper());
			out.writeObject(programState.getStartNode());
			out.writeObject(programState.getEndNode());

			out.writeBoolean(programState.getGraph().isDirected());
			out.writeObject(programState.getGraph().vertexClass());
			out.writeObject(programState.getGraph().edgeClass());

			for (final V v : programState.getGraph().getVertices()) {
				out.writeObject(v);
			}

			for (final E e : programState.getGraph().getEdges()) {
				out.writeObject(e);
				List<V> endpoints = programState.getGraph().getEndpoints(e);
				out.writeObject(endpoints.get(0));
				out.writeObject(endpoints.get(1));
			}
			
			// Necessary for saving / loading graphs without edges.
			out.writeObject(new FileDelimiterDummy());

			for (final V v : programState.getGraph().getVertices()) {
				out.writeObject(v);
				out.writeObject(programState.getColorMapper().getVertexColor(v));
				out.writeObject(programState.getLocationMapper().getLocation(v));
			}

			for (final E e : programState.getGraph().getEdges()) {
				out.writeObject(e);
				out.writeObject(programState.getColorMapper().getEdgeColor(e));
			}
		}
		out.close();

	}
}
