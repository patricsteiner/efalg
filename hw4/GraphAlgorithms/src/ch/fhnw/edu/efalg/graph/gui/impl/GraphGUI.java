package ch.fhnw.edu.efalg.graph.gui.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ch.fhnw.edu.efalg.graph.Edge;
import ch.fhnw.edu.efalg.graph.EdgeFactory;
import ch.fhnw.edu.efalg.graph.EdgePanelHelper;
import ch.fhnw.edu.efalg.graph.Graph;
import ch.fhnw.edu.efalg.graph.Vertex;
import ch.fhnw.edu.efalg.graph.algorithms.AbstractAlgorithm;
import ch.fhnw.edu.efalg.graph.gui.ProgramStateListener;
import ch.fhnw.edu.efalg.graph.gui.impl.ProgramState.State;
import ch.fhnw.edu.efalg.graph.gui.impl.action.EmptyAction;
import ch.fhnw.edu.efalg.graph.gui.impl.action.ExecuteAlgorithmAction;
import ch.fhnw.edu.efalg.graph.gui.impl.action.ExitAction;
import ch.fhnw.edu.efalg.graph.gui.impl.action.LoadAction;
import ch.fhnw.edu.efalg.graph.gui.impl.action.NewAction;
import ch.fhnw.edu.efalg.graph.gui.impl.action.RandomGraphAction;
import ch.fhnw.edu.efalg.graph.gui.impl.action.ResetColorsAction;
import ch.fhnw.edu.efalg.graph.gui.impl.action.SaveAction;

/**
 * Assembles the graphical components together into a graphical user interface.
 * 
 * @author Martin Schaub
 * 
 * @param <V>
 *            vertex type
 * @param <E>
 *            edge type
 */
public class GraphGUI<V extends Vertex, E extends Edge> implements
		ProgramStateListener<V, E> {
	
	private final ProgramState<V, E> programState;
	private JFrame frame;
	private JPanel optionsPanel;

	/**
	 * Index of the edge details panel within the options panel.
	 */
	static final int EDGE_DETAILS_PANEL_INDEX = 1;
	
	/**
	 * Constructor
	 * 
	 * @param state
	 *            program state
	 */
	public GraphGUI(final ProgramState<V, E> state) {
		if (state == null) {
			throw new NullPointerException();
		}
		programState = state;
		programState.addStateListener(this);

		GraphPanel<V, E> graphPanel = new GraphPanel<V, E>(programState);
		programState.addStateListener(graphPanel);
		programState.getColorMapper().addColorMapperListener(graphPanel);
		programState.getLocationMapper().addLocationListener(graphPanel);
		graphPanel.setBackground(Color.white);

		MouseCatcher<V, E> mouseCatcher = new MouseCatcher<V, E>(this);
		graphPanel.addMouseListener(mouseCatcher);
		graphPanel.addMouseMotionListener(mouseCatcher);

		JLabel statusLabel = new JLabel("");

		frame = new JFrame("Graph Algorithm Visualization");
		frame.setLayout(new BorderLayout());
		frame.add(graphPanel, BorderLayout.CENTER);
		
		optionsPanel = createOptionsPanel(statusLabel);
		frame.add(optionsPanel, BorderLayout.EAST);

		frame.setSize(1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(createMenuBar(statusLabel));
		frame.setVisible(true);

		// for correct initialization a new graph is created
		programState.setGraph(new Graph<V, E>(true, programState.getEdgeFactory()
				.getEdgeClass(), programState.getVertexFactory().getVertexClass()));
	}

	/**
	 * Creates an option panel on the right side of the application.
	 * 
	 * @param statusLabel
	 *            statusLabel to add.
	 * @return the newly created option panel
	 */
	private JPanel createOptionsPanel(final JLabel statusLabel) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		statusLabel.setAlignmentX(0f);

		panel.add(createSelectionPanel());
		panel.add(Box.createVerticalGlue());
		panel.add(statusLabel);

		return panel;
	}

	/**
	 * Creates a selection panel.
	 * 
	 * @return the newly created selection panel
	 */
	private JPanel createSelectionPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Mode"));
		panel.setLayout(new GridLayout(7, 1));

		ButtonGroup group = new ButtonGroup();

		JRadioButton moveNodes = new JRadioButton("Move Nodes");
		moveNodes.setSelected(true);
		moveNodes.addActionListener(new ProgramStateChanger<V, E>(programState,
				State.MOVE_NODES));
		group.add(moveNodes);
		panel.add(moveNodes);

		JRadioButton createNode = new JRadioButton("Create Node");
		createNode.addActionListener(new ProgramStateChanger<V, E>(programState,
				State.NODE_INSERT));
		group.add(createNode);
		panel.add(createNode);

		JRadioButton deleteNode = new JRadioButton("Delete Node");
		deleteNode.addActionListener(new ProgramStateChanger<V, E>(programState,
				State.NODE_DELETE));
		group.add(deleteNode);
		panel.add(deleteNode);

		JRadioButton createEdge = new JRadioButton("Create Edge");
		createEdge.addActionListener(new ProgramStateChanger<V, E>(programState,
				State.EDGE_INSERT));
		group.add(createEdge);
		panel.add(createEdge);

		JRadioButton deleteEdge = new JRadioButton("Delete Edge");
		deleteEdge.addActionListener(new ProgramStateChanger<V, E>(programState,
				State.EDGE_DELETE));
		group.add(deleteEdge);
		panel.add(deleteEdge);

		JRadioButton selectStartNode = new JRadioButton("Start Node Selection");
		selectStartNode.setSelected(true);
		selectStartNode.addActionListener(new ProgramStateChanger<V, E>(programState,
				State.START_NODE_SELECTION));
		group.add(selectStartNode);
		panel.add(selectStartNode);

		JRadioButton selectEndNode = new JRadioButton("End Node Selection");
		selectEndNode.addActionListener(new ProgramStateChanger<V, E>(programState,
				State.END_NODE_SELECTION));
		group.add(selectEndNode);
		panel.add(selectEndNode);

		panel.setMaximumSize(panel.getPreferredSize());
		panel.setAlignmentX(0f);
		
		return panel;
	}

	/**
	 * Create panel where the user can edit edge details.
	 * Panel is specific to the selected edge type.
	 * @return
	 */
	private JPanel createEdgeDetailsPanel() {
		JPanel panel = programState.getEdgePanelHelper().createEdgeDetailsPanel();
		panel.setMaximumSize(panel.getPreferredSize());
		panel.setAlignmentX(0f);

		return panel;
	}

	/**
	 * Creates a new JMenuItem with this characteristics and adds it to the
	 * parent menu bar.
	 * 
	 * @param name
	 *            name of the new entry
	 * @param action
	 *            action to execute
	 * @param mnemonic
	 *            keyboard shortcut
	 * @param parent
	 *            parent component to add
	 */
	private void addMenuItem(final String name, final Action action,
			final int mnemonic, final JMenu parent) {
		JMenuItem item = new JMenuItem(action);
		item.setText(name);
		item.setMnemonic(mnemonic);
		item.setEnabled(true);
		parent.add(item);
	}

	/**
	 * Creates the menu bar of the jframe.
	 * 
	 * @param statusLabel
	 *            statusLabel for the algorithm actions
	 * @return created menu bar.
	 */
	private JMenuBar createMenuBar(final JLabel statusLabel) {
		JMenuBar menuBar = new JMenuBar();

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		menuBar.add(file);

		file.add(createNew());

		file.addSeparator();
		
		file.add(createRandomGraphMenu());
		
		file.addSeparator();
		
		addMenuItem("Reset colors", new ResetColorsAction<V, E>(programState), KeyEvent.VK_R,
				file);
		addMenuItem("Empty Graph", new EmptyAction<V, E>(programState), KeyEvent.VK_E,
				file);

		file.addSeparator();

		addMenuItem("Save", new SaveAction<V, E>(programState), KeyEvent.VK_S, file);
		addMenuItem("Load", new LoadAction<V, E>(programState), KeyEvent.VK_L, file);

		file.addSeparator();

		addMenuItem("Exit", new ExitAction(), KeyEvent.VK_X, file);

		JMenu algorithms = new JMenu("Algorithms");
		algorithms.setMnemonic(KeyEvent.VK_A);
		menuBar.add(algorithms);

		addAlgorithms(algorithms, statusLabel);

		return menuBar;
	}

	/**
	 * Creates the random graph menu.
	 * 
	 * @return jmenu that stores the menu items.
	 */
	private JMenu createRandomGraphMenu() {
		JMenu menu = new JMenu("Random Graph");
		menu.setMnemonic(KeyEvent.VK_A);

		JMenu sparse = new JMenu("Sparse");
		menu.add(sparse);

		JMenu dense = new JMenu("Dense");
		menu.add(dense);

		for (int i = 5; i < 50; i = i + 5) {
			addMenuItem(Integer.toString(i), new RandomGraphAction<V, E>(programState,
					i, 2), 0, sparse);
			addMenuItem(Integer.toString(i), new RandomGraphAction<V, E>(programState,
					i, i / 3), 0, dense);
		}
		return menu;
	}

	/**
	 * Creates the new menu.
	 * 
	 * @return created menu
	 */
	@SuppressWarnings("unchecked")
	private JMenu createNew() {
		JMenu menu = new JMenu("New");
		menu.setMnemonic(KeyEvent.VK_N);

		JMenu directedMenu = new JMenu("directed");
		menu.add(directedMenu);
		JMenu undirectedMenu = new JMenu("undirected");
		menu.add(undirectedMenu);

		// Get all edge classes. If there is a factory and a panel helper starting
		// with the edge class name: create NewAction.
		
		for (Class<?> edgeClass : getAllClasses(
				"ch.fhnw.edu.efalg.graph.edges",
				Edge.class)) {
			
			Class<?> factoryClass = null;
			for (Class<?> clazz : getAllClasses(
					"ch.fhnw.edu.efalg.graph.edges",
					EdgeFactory.class)) {
				if (clazz.getSimpleName().startsWith(edgeClass.getSimpleName())) {
					factoryClass = clazz;
					break;
				}
			}
			
			Class<?> panelHelperClass = null;
			for (Class<?> clazz : getAllClasses(
					"ch.fhnw.edu.efalg.graph.edges",
					EdgePanelHelper.class)) {
				if (clazz.getSimpleName().startsWith(edgeClass.getSimpleName())) {
					panelHelperClass = clazz;
					break;
				}
			}
			
			if (factoryClass != null && panelHelperClass != null) {
				try {
					EdgeFactory<E> factory = (EdgeFactory<E>) factoryClass.newInstance();
	
					JMenuItem directed = new JMenuItem(new NewAction<V, E>(programState,
							factoryClass, panelHelperClass, true));
					JMenuItem undirected = new JMenuItem(new NewAction<V, E>(programState,
							factoryClass, panelHelperClass, false));
	
					directed.setText(factory.getName());
					undirected.setText(factory.getName());
	
					directedMenu.add(directed);
					undirectedMenu.add(undirected);
				} catch (final InstantiationException e) {
					System.err
							.println("Instantiation error creating graph with edge type " + edgeClass.getName());
				} catch (final IllegalAccessException e) {
					System.err
							.println("Illegal access error creating graph with edge type " + edgeClass.getName());
				}
			}
		}

		return menu;
	}

	/**
	 * Search for algorithms and adds them to the menu
	 * 
	 * @param menu
	 *            menu to add the found algorithms
	 * @param statusLabel
	 *            label to update from the algorithm.
	 */
	@SuppressWarnings("unchecked")
	private void addAlgorithms(final JMenu menu, final JLabel statusLabel) {
		for (Class<?> clazz : getAllClasses(
				"ch.fhnw.edu.efalg.graph.algorithms",
				AbstractAlgorithm.class)) {
			try {
				AbstractAlgorithm<V, E> algorithm = (AbstractAlgorithm<V, E>) clazz
						.newInstance();

				JMenuItem item = new JMenuItem();
				ExecuteAlgorithmAction<V, E> algorithmAction = new ExecuteAlgorithmAction<V, E>(
						algorithm, programState, item, statusLabel);
				programState.addStateListener(algorithmAction);

				item.setAction(algorithmAction);
				item.setText(algorithm.getName());
				menu.add(item);
			} catch (final InstantiationException e) {
				System.err
						.println("Error loading algorithm " + clazz.getName());
			} catch (final IllegalAccessException e) {
				System.err
						.println("Error loading algorithm " + clazz.getName());
			}
		}
	}

	/**
	 * Gets all classes in the package which aren't abstract and implement the
	 * interface or base class.
	 * 
	 * @param packageName
	 *            package to search
	 * @param type
	 *            super type of the class, or object if this is not needed
	 * @return list of classes that satisfies this properties.
	 */
	private List<Class<?>> getAllClasses(final String packageName,
			final Class<?> type) {
		List<Class<?>> foundClasses = new LinkedList<Class<?>>();

		URL packageURL = getClass().getResource(
				"/" + packageName.replaceAll("\\.", "/"));
		File packageDir;
		try {
			packageDir = new File(URLDecoder.decode(packageURL.getPath(),
					"UTF-8"));
			if (packageDir.isDirectory()) {
				for (File f : packageDir.listFiles()) {
					if (f.isFile() && f.getName().endsWith(".class")) {
						try {
							String className = packageName + "."
									+ f.getName().replaceAll(".class$", "");
							Class<?> clazz = Class.forName(className);
							if ((clazz.getModifiers() & Modifier.ABSTRACT) == 0
									&& type.isAssignableFrom(clazz)) {
								foundClasses.add(clazz);
							}
						} catch (final ClassNotFoundException e) {
							// do nothing, because then the class just cannot be
							// loaded.
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("Error decoding package path "
					+ packageURL.getPath());
		}

		return foundClasses;
	}
	
	public ProgramState<V, E> getProgramState() {
		return programState;
	}

	public JPanel getOptionsPanel() {
		return optionsPanel;
	}

	/**
	 * @see ProgramStateListener#newGraphLoaded(Graph, Graph)
	 */
	@Override
	public void newGraphLoaded(Graph<V, E> currentGraph, Graph<V, E> oldGraph) {
		if (programState.getState().equals(State.EDGE_INSERT)) {
			optionsPanel.remove(EDGE_DETAILS_PANEL_INDEX);
			optionsPanel.add(createEdgeDetailsPanel(), EDGE_DETAILS_PANEL_INDEX);
			frame.validate();
			frame.repaint();
		}
	}

	/**
	 * @see ProgramStateListener#graphChanged()
	 */
	@Override
	public void graphChanged() {
		// nothing to do
	}

	/**
	 * @see ProgramStateListener#stateChanged(State, State)
	 */
	@Override
	public void stateChanged(State oldState, State newState) {
		if (oldState.equals(State.EDGE_INSERT)) {
			optionsPanel.remove(EDGE_DETAILS_PANEL_INDEX);
			frame.validate();
			frame.repaint();
		}
		else if (newState.equals(State.EDGE_INSERT)) {
			optionsPanel.add(createEdgeDetailsPanel(), EDGE_DETAILS_PANEL_INDEX);
			frame.validate();
			frame.repaint();
		}
	}
}
