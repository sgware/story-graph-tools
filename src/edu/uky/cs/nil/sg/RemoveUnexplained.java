package edu.uky.cs.nil.sg;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that removes {@link
 * TemporalEdge temporal edges} which are not {@link
 * TemporalEdge#isExplained(Character) explained} for their {@link
 * Action#consenting consenting} {@link Action#isNPC() non-player characters}.
 * In other words, actions taken only by non-player characters that are not
 * explained will always be removed; actions taken only by player characters
 * will never be removed; actions taken jointly by both player and non-player
 * characters will be removed if they are not explained for the non-player
 * characters.
 * 
 * @author Stephen G. Ware
 */
public class RemoveUnexplained extends SimpleStoryGraphTool {
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new RemoveUnexplained(args).run();
	}
	
	/**
	 * Constructs a new unexplained action removal tool.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public RemoveUnexplained(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new unexplained action removal tool.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public RemoveUnexplained(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new unexplained action removal tool with the default
	 * configuration.
	 */
	public RemoveUnexplained() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Remove Unexplained Actions";
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
		return "Removes temporal edges that are not explained for a consenting non-player character.";
	}
	
	@Override
	protected void run(StoryGraph graph, Status status) throws Exception {
		long before = graph.edges.temporal.size();
		graph.edges.temporal.remove(edge -> {
			for(Character consenting : edge.label.consenting)
				if(!consenting.isPlayer() && !edge.isExplained(consenting))
					return true;
			return false;
		}, status);
		status.setMessage("Removed " + (before - graph.edges.temporal.size()) + " unexplained temporal edges");
	}
}