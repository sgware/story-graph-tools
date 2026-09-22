package edu.uky.cs.nil.sg;

import java.util.List;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that provides an interface to
 * the {@link RemoveDuplicates} task.
 * 
 * @author Stephen G. Ware
 */
public class RemoveDuplicatesTool extends SimpleStoryGraphTool {
	
	/** A flag that indicates all duplicate elements should be removed */
	protected static final Argument.Flag ALL = new Argument.Flag("all", "remove all duplicate elements (default if no other flags are used)");
	
	/** Whether duplicate {@link StoryGraph#states states} should be removed */
	protected static final Argument.Flag STATES = new Argument.Flag("states", "remove duplicate states", ALL);
	
	/** Whether duplicate {@link StoryGraph#plans plans} should be removed */
	protected static final Argument.Flag PLANS = new Argument.Flag("plans", "remove duplicate plans", ALL);
	
	/** Whether duplicate {@link StoryGraph#nodes nodes} should be removed */
	protected static final Argument.Flag NODES = new Argument.Flag("nodes", "remove duplicate nodes", ALL);
	
	private static final Argument.Flag[] FLAGS = new Argument.Flag[] {
		STATES, PLANS, NODES, ALL
	};
	
	/**
	 * The main entry point for the remove duplicates tool.
	 * 
	 * @param args the arguments passed to this tool from the terminal
	 * @throws Exception if a problem occurs while running this tool
	 */
	public static void main(String[] args) throws Exception {
		new RemoveDuplicatesTool().run(new Arguments(args));
	}
	
	/**
	 * Constructs a remove duplicates tool.
	 */
	public RemoveDuplicatesTool() {
		// default constructor
	}
	
	@Override
	public String getName() {
		return "Remove Duplicate Elements";
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
		return "Removes duplicate states and plans. Replaces equivalent nodes unless they have edges that lead to non-eqivalent nodes.";
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
		for(Argument.Flag flag : FLAGS)
			if(arguments.get(flag))
				none = false;
		new RemoveDuplicates(
			graph,
			arguments.get(STATES) || none,
			arguments.get(PLANS) || none,
			arguments.get(NODES) || none
		).run(status);
		return graph;
	}
}