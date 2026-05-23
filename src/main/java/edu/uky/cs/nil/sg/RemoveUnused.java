package edu.uky.cs.nil.sg;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that removes unused elements
 * from a {@link StoryGraph story graph}.
 * <p>
 * Specifically, this tool removes:
 * <ul>
 * <li>{@link Character Characters} who never appear as {@link Action#consenting
 * consenting characters} in any {@link TemporalEdge temporal edges} and which
 * never have {@link EpistemicEdge epistemic edges} from any {@link Node nodes}.
 * </li>
 * <li>{@link Fluent Fluents} which always have the same {@link
 * Node#getValue(Fluent) value} in all nodes.</li>
 * <li>{@link NominalValue Nominal values} that are never assigned as the {@link
 * Node#getValue(Fluent) value of a fluent} in any node.</li>
 * <li>{@link State State objects} which are never used as the {@link
 * Node#getState() state} of any nodes.</li>
 * <li>{@link Action Actions} which are never used as the {@link
 * TemporalEdge#label label} of any {@link TemporalEdge temporal edges}.</li>
 * <li>{@link Plan Plan objects} which are never used as the {@link
 * Explanation#getPlan() plan} of any {@link Explanation explanation}.</li>
 * </ul>
 * 
 * @author Stephen G. Ware
 */
public class RemoveUnused extends SimpleStoryGraphTool {
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new RemoveUnused(args).run();
	}
	
	/**
	 * Constructs a new unused element removal tool.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public RemoveUnused(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new unused element removal tool.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public RemoveUnused(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new unused element removal tool with the default
	 * configuration.
	 */
	public RemoveUnused() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Remove Unused Elements";
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
		return "Removes characters who never consent and never have beliefs. Removes fluents whose values never change. Removes values, actions, states, and plans that are never used. The first argument must be a story graph file.";
	}
	
	@Override
	protected void run(StoryGraph graph, Status status) throws Exception {
		UnusedElementSearch unused = new UnusedElementSearch(graph);
		unused.run(status);
		graph.prune(object -> unused.test(object), status);
		status.setMessage("Unused elements removed");
	}
}