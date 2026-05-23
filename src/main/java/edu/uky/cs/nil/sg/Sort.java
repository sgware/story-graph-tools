package edu.uky.cs.nil.sg;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that sorts the symbols,
 * nodes, and edges of a {@link StoryGraph story graph}.
 * 
 * @author Stephen G. Ware
 */
public class Sort extends SimpleStoryGraphTool {
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new Sort(args).run();
	}
	
	/**
	 * Constructs a new story graph sorting tool.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public Sort(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new story graph sorting tool.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public Sort(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new story graph sorting tool with the default configuration.
	 */
	public Sort() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Sort Story Graph";
	}
	
	@Override
	public String getVersion() {
		return "0.9";
	}
	
	@Override
	public String getAuthors() {
		return "Stephen G. Ware";
	}
	
	@Override
	public String getDescription() {
		return "Sorts characters, fluents, values, and actions alphabetically. Sorts plans to save memory. Sorts nodes, states, and edges in breadth-first search order. The first argument must be a story graph file.";
	}
	
	@Override
	protected void run(StoryGraph graph, Status status) throws Exception {
		new SortSymbols(graph.characters).run(status);
		new SortSymbols(graph.fluents).run(status);
		new SortSymbols(graph.values).run(status);
		new SortSymbols(graph.actions).run(status);
		new SortPlans(graph).run(status);
		new SortNodes(graph).run(status);
		new SortStates(graph).run(status);
		new SortExplanations(graph).run(status);
		status.setMessage("Story graph sorted");
	}
}