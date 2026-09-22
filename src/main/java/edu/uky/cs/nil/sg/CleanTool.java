package edu.uky.cs.nil.sg;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that provides an convenient
 * interface to the combined {@link RemoveDisconnected}, {@link RemoveUnused},
 * {@link RemoveDuplicates}, and {@link Sort} tasks.
 * 
 * @author Stephen G. Ware
 */
public class CleanTool extends SimpleStoryGraphTool {
	
	/**
	 * Constructs a story graph clean tool.
	 */
	public CleanTool() {
		// default constructor
	}
	
	@Override
	public String getName() {
		return "Clean Story Graph";
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
		return "Removes disconnected nodes and unused elements. Replaces duplicate elements. Sorts all elements.";
	}
	
	@Override
	protected StoryGraph run(Arguments arguments, StoryGraph graph, Status status) throws Exception {
		new RemoveDisconnected(graph).run(status);
		new RemoveUnused(graph).run(status);
		new RemoveDuplicates(graph).run(status);
		new Sort(graph).run(status);
		status.setMessage("Story graph cleaned");
		return graph;
	}
}