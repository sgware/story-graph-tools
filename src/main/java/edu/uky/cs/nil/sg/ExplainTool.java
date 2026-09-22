package edu.uky.cs.nil.sg;

import java.util.List;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that provides an interface to
 * the {@link Explain} task.
 * 
 * @author Stephen G. Ware
 */
public class ExplainTool extends SimpleStoryGraphTool {
	
	/** Maximum explanation plan length that will be considered */
	protected static final Argument.Key<Integer> DEPTH_LIMIT = new Argument.Key<>("depth", new Argument.Integer(0), "max plan length to generate (defaults to " + Explain.UNLIMITED_DEPTH + " for no limit)");
	
	/**
	 * The main entry point for the find explanations tool.
	 * 
	 * @param args the arguments passed to this tool from the terminal
	 * @throws Exception if a problem occurs while running this tool
	 */
	public static void main(String[] args) throws Exception {
		new ExplainTool().run(new Arguments(args));
	}
	
	/**
	 * Constructs a new find explanations tool.
	 */
	public ExplainTool() {
		// default constructor
	}
	
	@Override
	public String getName() {
		return "Find Explanations";
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
		return "Finds explanations that improve author utility and that characters believe will improve their utility. Missing epistemic edges are treated as loops.";
	}
	
	@Override
	protected List<Argument<?>> getOptionalArguments() {
		List<Argument<?>> arguments = super.getOptionalArguments();
		arguments.add(DEPTH_LIMIT);
		return arguments;
	}
	
	@Override
	protected StoryGraph run(Arguments arguments, StoryGraph graph, Status status) throws Exception {
		int depth = Explain.UNLIMITED_DEPTH;
		if(arguments.get(DEPTH_LIMIT) != null)
			depth = arguments.get(DEPTH_LIMIT);
		new Explain(graph, depth).run(status);
		return graph;
	}
}