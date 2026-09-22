package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that removes duplicate {@link State states} from a story
 * graph's list of {@link StoryGraph#states}. If two nodes are found that have
 * the same {@link Node#getValue(Fluent) values} for all fluents but use
 * different state objects, one state object is removed and the node that was
 * using the duplicate state will now use the preserved state.
 * <p>
 * This operation does not modify the content of a story graph but may save
 * memory by removing unnecessary duplicate objects.
 * 
 * @author Stephen G. Ware
 */
public class RemoveDuplicateStates implements Task {
	
	/** The graph whose duplicate states will be removed */
	public final StoryGraph graph;
	
	/**
	 * Constructs a remove duplicate states task for the given story graph.
	 * 
	 * @param graph the story graph whose duplicate nodes will be removed
	 */
	public RemoveDuplicateStates(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		status.set("Removing duplicate states", graph.nodes.size());
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
		status.setMessage("Removed " + (before - graph.states.size()) + " duplicate states");
	}
}