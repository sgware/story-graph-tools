package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that sorts the {@link State states} of a {@link
 * StoryGraph story graph} based on the first {@link Node node} that uses the
 * state. In other words, state s will be ordered before state t if the first
 * node that {@link Node#getState() uses} state s has a lower {@link
 * Node#getID() ID number} than the first node that uses state t.
 * 
 * @author Stephen G. Ware
 */
public class SortStates implements Task {
	
	/** The story graph whose states will be sorted */
	protected final StoryGraph graph;
	
	/**
	 * Constructs a new story graph state sort task.
	 * 
	 * @param graph the story graph whose states will be sorted
	 */
	public SortStates(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		status.set("Finding the first appearance of each state", graph.nodes.size());
		BigNumberedMap<State, Long> first = new BigNumberedMap<>(graph.states);
		for(Node node : graph.nodes) {
			Long id = first.get(node.getState());
			if(id == null || node.getID() < id)
				first.put(node.getState(), node.getID());
			status.increment();
		}
		graph.states.sort((s1, s2) -> Utilities.compare(first.get(s1), first.get(s2)), status);
	}
}