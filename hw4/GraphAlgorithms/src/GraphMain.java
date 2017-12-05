import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.VertexFactory;
import ch.fhnw.edu.efalg.graph.gui.impl.ColorMapper;
import ch.fhnw.edu.efalg.graph.gui.impl.GraphGUI;
import ch.fhnw.edu.efalg.graph.gui.impl.LocationMapper;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState;
import ch.fhnw.edu.efalg.graph.vertices.IntegerVertexFactory;

/**
 * Main class.
 * 
 * @author Martin Schaub
 */
public class GraphMain {

	/**
	 * Main method.
	 * 
	 * @param args command line arguments
	 */
	public static void main(final String[] args) {
		LocationMapper<Vertex, Edge> locationMapper = new LocationMapper<Vertex, Edge>();
		ColorMapper<Vertex, Edge> colorMapper = new ColorMapper<Vertex, Edge>();
		VertexFactory<Vertex> vertexFactory = new IntegerVertexFactory();

		ProgramState<Vertex, Edge> state = new ProgramState<Vertex, Edge>(locationMapper, colorMapper,
				vertexFactory);

		state.getGraph().addGraphListener(locationMapper);
		state.getGraph().addGraphListener(colorMapper);

		new GraphGUI<Vertex, Edge>(state);
	}

}
