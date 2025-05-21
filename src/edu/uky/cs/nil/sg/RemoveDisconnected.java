package edu.uky.cs.nil.sg;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that removes all {@link Node
 * nodes} from a {@link StoryGraph story graph} that cannot be reached via a
 * path of {@link TemporalEdge temporal} or {@link EpistemicEdge epistemic}
 * edges from node 0.
 * 
 * @author Stephen G. Ware
 */
public class RemoveDisconnected extends SimpleStoryGraphTool {
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new RemoveDisconnected(args).run();
	}
	
	/**
	 * Constructs a new disconnected node removal tool.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public RemoveDisconnected(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new disconnected node removal tool.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public RemoveDisconnected(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new disconnected node removal tool with the default
	 * configuration.
	 */
	public RemoveDisconnected() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Remove Disconnected Nodes";
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
		return "Removes nodes which cannot be reached via a temporal or epistemic path from node 0.";
	}
	
	@Override
	protected void run(StoryGraph graph, Status status) throws Exception {
		long before = graph.nodes.size();
		BreadthFirstSearch search = new BreadthFirstSearch(graph, false);
		search.run(status);
		graph.nodes.remove(node -> search.getOrder(node) == null);
		status.setMessage("Removed " + (before - graph.nodes.size()) + " disconnected nodes");
	}
}