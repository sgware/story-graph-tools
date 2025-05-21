package edu.uky.cs.nil.sg;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that replaces duplicate
 * elements in a {@link StoryGraph story graph} to save memory.
 * <p>
 * Specifically, after this tool has been run:
 * <ul>
 * <li>All {@link Node nodes} that have the same {@link Node#getState() state}
 * will use the same state object.</li>
 * <li>Any nodes which are duplicates have been replaced so that the graph no
 * longer contains any duplicate nodes. Nodes are considered duplicates if they
 * have all the same {@link Node#getValue(Fluent) fluent values}, all the same
 * {@link Node#getUtility(Character) utilities}, and if their outgoing epistemic
 * and temporal edges lead to equivalent nodes (see the {@link
 * ReplaceDuplicateNodes} task for more detail).</li>
 * <li>All {@link Explanation explanations} that have the same {@link
 * Explanation#getPlan() plan} will use the same plan object.</li>
 * </ul>
 * 
 * @author Stephen G. Ware
 */
public class RemoveDuplicates extends SimpleStoryGraphTool {
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new RemoveDuplicates(args).run();
	}
	
	/**
	 * Constructs a new duplicate elements removal tool.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public RemoveDuplicates(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new duplicate elements removal tool.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public RemoveDuplicates(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new duplicate elements removal tool with the default
	 * configuration.
	 */
	public RemoveDuplicates() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Remove Duplicate Elements";
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
		return "Removes duplicate states and plans. Replaces nodes whose states are equivalent unless they have temporal edges that lead to non-eqivalent nodes.";
	}
	
	@Override
	protected void run(StoryGraph graph, Status status) throws Exception {
		new ReplaceDuplicateStates(graph).run(status);
		new ReplaceDuplicateNodes(graph).run(status);
		new ReplaceDuplicatePlans(graph).run(status);
		status.setMessage("Duplicate elements replaced");
	}
}