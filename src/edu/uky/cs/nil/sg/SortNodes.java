package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that sorts the {@link Node nodes} of a {@link StoryGraph
 * story graph} based on the order they were visited during a {@link
 * BreadthFirstSearch breadth-first search} starting at node 0.
 * 
 * @author Stephen G. Ware
 */
public class SortNodes implements Task {
	
	/** The story graph whose nodes will be sorted */
	protected final StoryGraph graph;
	
	/**
	 * Constructs a new story graph node sort task.
	 * 
	 * @param graph the story graph whose nodes will be sorted
	 */
	public SortNodes(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		BreadthFirstSearch search = new BreadthFirstSearch(graph, true);
		search.run(status);
		graph.nodes.sort((n1, n2) -> search.getOrder(n1).compareTo(search.getOrder(n2)), status);
	}
}