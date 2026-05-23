package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that replaces duplicate {@link State states} in a {@link
 * StoryGraph story graph} to save memory--two states are considered the same
 * if they assign the same {@link State#getValue(Fluent) values} to all {@link
 * Fluent fluents} and the same utility to the {@link State#getUtility() author}
 * and to {@link State#getUtility(Character) each character}.
 * 
 * @author Stephen G. Ware
 */
public class ReplaceDuplicateStates implements Task {
	
	/** The story graph whose duplicate states will be replaced */
	protected final StoryGraph graph;
	
	/**
	 * Constructs a new duplicate state replacement task.
	 * 
	 * @param graph the graph whose duplicate states will be replaced
	 */
	public ReplaceDuplicateStates(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		status.set("Replacing duplicate states", graph.nodes.size());
		long before = graph.states.size();
		StateHashMap<State> replacements = new StateHashMap<>(graph);
		for(Node node : graph.nodes) {
			State replacement = replacements.get(node.getState());
			if(replacement == null)
				replacements.put(node.getState(), node.getState());
			else if(replacement != node.getState()) {
				node.getState().setID(Settings.PRUNED);
				node.setState(replacement);
			}
			status.increment();
		}
		graph.states.renumber(status);
		status.setMessage("Replaced " + (before - graph.states.size()) + " duplicate states");
	}
}