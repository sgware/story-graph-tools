package edu.uky.cs.nil.sg;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that provides an interface to
 * the {@link RemoveDisconnected} task.
 * 
 * @author Stephen G. Ware
 */
public class RemoveDisconnectedTool extends SimpleStoryGraphTool {
	
	/**
	 * The main entry point for the remove disconnected nodes tool.
	 * 
	 * @param args the arguments passed to this tool from the terminal
	 * @throws Exception if a problem occurs while running this tool
	 */
	public static void main(String[] args) throws Exception {
		new RemoveDisconnectedTool().run(new Arguments(args));
	}
	
	/**
	 * Constructs a remove disconnected tool.
	 */
	public RemoveDisconnectedTool() {
		// default constructor
	}
	
	@Override
	public String getName() {
		return "Remove Disconnected Nodes";
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
		return "Removes nodes which cannot be reached via a temporal or epistemic path from node 0.";
	}
	
	@Override
	protected StoryGraph run(Arguments arguments, StoryGraph graph, Status status) throws Exception {
		new RemoveDisconnected(graph).run(status);
		return graph;
	}
}