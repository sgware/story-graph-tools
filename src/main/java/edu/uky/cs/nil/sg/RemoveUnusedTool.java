package edu.uky.cs.nil.sg;

import java.util.List;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that provides an interface to
 * the {@link RemoveUnused} task.
 * 
 * @author Stephen G. Ware
 */
public class RemoveUnusedTool extends SimpleStoryGraphTool {
	
	/** A flag that indicates all unused elements should be removed */
	protected static final Argument.Flag ALL = new Argument.Flag("all", "remove all unused elements (default if no other flags are used)");
	
	/**
	 * Whether unused {@link StoryGraph#characters characters} should be removed
	 */
	protected static final Argument.Flag CHARACTERS = new Argument.Flag("characters", "remove unused characters", ALL);
	
	/**
	 * Whether {@link StoryGraph#fluents fluents} that do not change should be
	 * removed
	 */
	protected static final Argument.Flag FLUENTS = new Argument.Flag("fluents", "remove fluents whose values never change", ALL);
	
	/** Whether unused {@link StoryGraph#values values} should be removed */
	protected static final Argument.Flag VALUES = new Argument.Flag("values", "remove unused values", ALL);
	
	/** Whether unused {@link StoryGraph#states states} should be removed */
	protected static final Argument.Flag STATES = new Argument.Flag("states", "remove unused states", ALL);
	
	/** Whether unused {@link StoryGraph#actions actions} should be removed */
	protected static final Argument.Flag ACTIONS = new Argument.Flag("actions", "remove unused actions", ALL);
	
	/** Whether unused {@link StoryGraph#plans plans} should be removed */
	protected static final Argument.Flag PLANS = new Argument.Flag("plans", "remove unused plans", ALL);
	
	private static final Argument.Flag[] FLAGS = new Argument.Flag[] {
		CHARACTERS, FLUENTS, VALUES, STATES, ACTIONS, PLANS, ALL
	};
	
	/**
	 * The main entry point for the remove unused elements tool.
	 * 
	 * @param args the arguments passed to this tool from the terminal
	 * @throws Exception if a problem occurs while running this tool
	 */
	public static void main(String[] args) throws Exception {
		new RemoveUnusedTool().run(new Arguments(args));
	}
	
	/**
	 * Constructs a remove unused tool.
	 */
	public RemoveUnusedTool() {
		// default constructor
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
		return "Removes characters who never act or have beliefs. Removes fluents that never change. Removes values, actions, states, and plans that are never used.";
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
		new RemoveUnused(
			graph,
			arguments.get(CHARACTERS) || none,
			arguments.get(FLUENTS) || none,
			arguments.get(VALUES) || none,
			arguments.get(STATES) || none,
			arguments.get(ACTIONS) || none,
			arguments.get(PLANS) || none
		).run(status);
		return graph;
	}
}