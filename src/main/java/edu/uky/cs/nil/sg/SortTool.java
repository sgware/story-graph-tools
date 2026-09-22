package edu.uky.cs.nil.sg;

import java.util.List;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that provides an interface to
 * the {@link Sort} task.
 * 
 * @author Stephen G. Ware
 */
public class SortTool extends SimpleStoryGraphTool {
	
	/**
	 * A flag that indicates all elements of the graph should be sorted
	 */
	protected static final Argument.Flag ALL = new Argument.Flag("all", "sort all elements of the graph (default if no other flags are used)");
	
	/**
	 * A flag that sets the {@link #CHARACTERS}, {@link #FLUENTS}, {@link
	 * VALUES}, and {@link ACTIONS} flags
	 */
	protected static final Argument.Flag SYMBOLS = new Argument.Flag("symbols", "sort characters, fluents, values, and actions", ALL);
	
	/**
	 * Whether the graph's {@link StoryGraph#characters characters} should be
	 * sorted
	 */
	protected static final Argument.Flag CHARACTERS = new Argument.Flag("characters", "sort characters alphabetically", SYMBOLS, ALL);
	
	/**
	 * Whether the graph's {@link StoryGraph#fluents fluents} should be sorted
	 */
	protected static final Argument.Flag FLUENTS = new Argument.Flag("fluents", "sort fluents alphabetically", SYMBOLS, ALL);
	
	/**
	 * Whether the graph's {@link StoryGraph#values values} should be sorted
	 */
	protected static final Argument.Flag VALUES = new Argument.Flag("values", "sort values alphabetically", SYMBOLS, ALL);
	
	/**
	 * Whether the graph's {@link StoryGraph#states states} should be sorted
	 */
	protected static final Argument.Flag STATES = new Argument.Flag("states", "sort states in node order", ALL);
	
	/**
	 * Whether the graph's {@link StoryGraph#actions actions} should be sorted
	 */
	protected static final Argument.Flag ACTIONS = new Argument.Flag("actions", "sort actions alphabetically", SYMBOLS, ALL);
	
	/**
	 * Whether the graph's {@link StoryGraph#plans plans} should be sorted
	 */
	protected static final Argument.Flag PLANS = new Argument.Flag("plans", "sort plans by action in reverse and to save memory", ALL);
	
	/**
	 * Whether the graph's {@link StoryGraph#nodes nodes} should be sorted
	 */
	protected static final Argument.Flag NODES = new Argument.Flag("nodes", "sort nodes in breadth-first search order", ALL);
	
	/**
	 * A flag that sets the {@link #TEMPORAL} and {@link #EPISTEMIC} flags
	 */
	protected static final Argument.Flag EDGES = new Argument.Flag("edges", "sort temporal and epistemic edges", ALL);
	
	/**
	 * Whether the graph's {@link EdgeCollection#temporal temporal edges} should
	 * be sorted
	 */
	protected static final Argument.Flag TEMPORAL = new Argument.Flag("temporal", "sort temporal edges in action order", EDGES, ALL);
	
	/**
	 * Whether the graph's {@link EdgeCollection#epistemic epistemic edges}
	 * should be sorted
	 */
	protected static final Argument.Flag EPISTEMIC = new Argument.Flag("epistemic", "sort epistemic edges in character order", EDGES, ALL);
	
	/**
	 * Whether the graph's {@link StoryGraph#explanations explanations} should
	 * be sorted
	 */
	protected static final Argument.Flag EXPLANATIONS = new Argument.Flag("explanations", "sort explanations in node, then character, then plan order", ALL);
	
	private static final Argument.Flag[] FLAGS = new Argument.Flag[] {
		CHARACTERS, FLUENTS, VALUES, ACTIONS, SYMBOLS, STATES, PLANS, NODES, TEMPORAL, EPISTEMIC, EDGES, EXPLANATIONS, ALL
	};
		
	/**
	 * The main entry point for the story graph sorting tool.
	 * 
	 * @param args the arguments passed to this tool from the terminal
	 * @throws Exception if a problem occurs while running this tool
	 */
	public static void main(String[] args) throws Exception {
		new SortTool().run(new Arguments(args));
	}
	
	/**
	 * Constructs a story graph sort tool.
	 */
	public SortTool() {
		// default constructor
	}
	
	@Override
	public String getName() {
		return "Sort Story Graph";
	}
	
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String getAuthors() {
		return "Stephen G. Ware";
	}
	
	@Override
	public String getDescription() {
		return "Sorts characters, fluents, values, and actions alphabetically. Sorts plans to save memory. Sorts nodes, states, and edges in breadth-first search order.";
	}
	
	@Override
	protected List<Argument<?>> getOptionalArguments() {
		List<Argument<?>> arguments = super.getOptionalArguments();
		for(Argument<?> flag : FLAGS)
			arguments.add(flag);
		return arguments;
	}
	
	@Override
	protected StoryGraph run(Arguments arguments, StoryGraph graph, Status status) throws Exception {
		boolean none = true;
		for(Argument.Flag argument : FLAGS)
			if(arguments.get(argument))
				none = false;
		new Sort(
			graph,
			arguments.get(CHARACTERS) || none,
			arguments.get(FLUENTS) || none,
			arguments.get(VALUES) || none,
			arguments.get(STATES) || none,
			arguments.get(ACTIONS) || none,
			arguments.get(PLANS) || none,
			arguments.get(NODES) || none,
			arguments.get(TEMPORAL) || none,
			arguments.get(EPISTEMIC) || none,
			arguments.get(EXPLANATIONS) || none
		).run(status);
		return graph;
	}
}