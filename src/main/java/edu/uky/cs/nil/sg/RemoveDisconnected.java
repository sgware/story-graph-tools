package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that removes all {@link Node nodes} from a {@link
 * StoryGraph story graph} that cannot be reached via a path of {@link
 * TemporalEdge temporal} or {@link EpistemicEdge epistemic} edges from node 0.
 * 
 * @author Stephen G. Ware
 */
public class RemoveDisconnected implements Task {
	
	/** The graph whose disconnected nodes will be removed */
	public final StoryGraph graph;
	
	/**
	 * Constructs a remove disconnected nodes task for the given story graph.
	 * 
	 * @param graph the story graph whose disconnected nodes will be removed
	 */
	public RemoveDisconnected(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		long before = graph.nodes.size();
		BreadthFirstSearch search = new BreadthFirstSearch(graph, false);
		search.run(status);
		graph.nodes.remove(node -> search.getOrder(node) == null);
		status.setMessage("Removed " + (before - graph.nodes.size()) + " disconnected nodes");
	}
}