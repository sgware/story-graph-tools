package edu.uky.cs.nil.sg;

import java.util.List;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that provides an interface to
 * the {@link RemoveUnexplained} task.
 * 
 * @author Stephen G. Ware
 */
public class RemoveUnexplainedTool extends SimpleStoryGraphTool {
	
	/** A flag that indicates all unexplained actions should be removed */
	protected static final Argument.Flag ALL = new Argument.Flag("all", "remove unexplained edges for all characters");
	
	/** Whether unexplained non-player character actions should be removed */
	protected static final Argument.Flag NPC = new Argument.Flag("npc", "remove uneplained actions at least one non-player character consents to (default if no other flags are used)", ALL);
	
	/** Whether unexplained player actions should be removed */
	protected static final Argument.Flag PLAYER = new Argument.Flag("player", "remove unexplained actions the player consents to", ALL);
	
	/**
	 * The main entry point for the remove unexplained actions tool.
	 * 
	 * @param args the arguments passed to this tool from the terminal
	 * @throws Exception if a problem occurs while running this tool
	 */
	public static void main(String[] args) throws Exception {
		new RemoveUnexplainedTool().run(new Arguments(args));
	}
	
	/**
	 * Constructs a new remove unexplained actions tool.
	 */
	public RemoveUnexplainedTool() {
		// default constructor
	}
	
	@Override
	public String getName() {
		return "Remove Unexplained Actions";
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
		return "Removes temporal edges that are not explained for their consenting characters.";
	}
	
	@Override
	protected List<Argument<?>> getOptionalArguments() {
		List<Argument<?>> arguments = super.getOptionalArguments();
		arguments.add(NPC);
		arguments.add(PLAYER);
		arguments.add(ALL);
		return arguments;
	}
	
	@Override
	protected StoryGraph run(Arguments arguments, StoryGraph graph, Status status) throws Exception {
		boolean none = !arguments.get(NPC) && !arguments.get(PLAYER);
		new RemoveUnexplained(
			graph,
			arguments.get(NPC) || none,
			arguments.get(PLAYER)
		).run(status);
		return graph;
	}
}