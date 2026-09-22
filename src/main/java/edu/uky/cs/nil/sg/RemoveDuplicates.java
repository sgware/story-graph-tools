package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that replaces duplicate elements in a {@link StoryGraph
 * story graph} to save memory.
 * <p>
 * Specifically, after this tool has been run:
 * <ul>
 * <li>All {@link Node nodes} that have the same {@link Node#getState() state}
 * will use the same state object.</li>
 * <li>All {@link Explanation explanations} that have the same {@link
 * Explanation#getPlan() plan} will use the same plan object.</li>
 * <li>The graph does not contains two nodes which are equivalent. Two nodes are
 * considered equivalent if they have the same {@link Node#getState() state},
 * all the same {@link Node#getUtility(Character) utilities}, all the same
 * {@link Node#edges edges}, and all of their edges lead to equivalent nodes.
 * See the {@link RemoveDuplicateNodes} task for more detail.</li>
 * </ul>
 * 
 * @author Stephen G. Ware
 */
public class RemoveDuplicates implements Task {
	
	/** The graph whose duplicate elements will be removed */
	public final StoryGraph graph;
	
	/** Whether duplicate states should be removed */
	public boolean states;
	
	/** Whether duplicate plans should be removed */
	public boolean plans;
	
	/** Whether duplicate nodes should be removed */
	public boolean nodes;
	
	/**
	 * Constructs a remove duplicate elements task for the given story graph.
	 * 
	 * @param graph the story graph whose duplicate elements will be removed
	 * @param states whether duplicate states should be removed
	 * @param plans whether duplicate plans should be removed
	 * @param nodes whether duplicate nodes should be removed
	 */
	public RemoveDuplicates(
		StoryGraph graph,
		boolean states,
		boolean plans,
		boolean nodes
	) {
		this.graph = graph;
		this.states = states;
		this.plans = plans;
		this.nodes = nodes;
	}
	
	/**
	 * Constructs a remove duplicate elements task for the given story graph
	 * that removes all duplicate elements.
	 * 
	 * @param graph the story graph whose duplicate elements will be removed
	 */
	public RemoveDuplicates(StoryGraph graph) {
		this(
			graph,
			true,
			true,
			true
		);
	}
	
	@Override
	public void run(Status status) throws Exception {
		long total = 0;
		if(states)
			total += run(new RemoveDuplicateStates(graph), graph.states, status);
		if(plans)
			total += run(new RemoveDuplicatePlans(graph), graph.plans, status);
		if(nodes)
			total += run(new RemoveDuplicateNodes(graph), graph.nodes, status);
		status.setMessage(total + " duplicate elements removed");
	}
	
	private final long run(Task task, NumberedList<?> list, Status status) throws Exception {
		long before = list.size();
		task.run(status);
		return before - list.size();
	}
}