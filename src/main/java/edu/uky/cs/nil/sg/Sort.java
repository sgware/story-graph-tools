package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that sorts the various elements of a {@link StoryGraph
 * story graph}.
 * <ul>
 * <li>The graph's {@link Symbol symbols} (its {@link StoryGraph#characters},
 * {@link StoryGraph#fluents}, {@link StoryGraph#values}, and {@link
 * StoryGraph#actions}) are sorted alphabetically.</li>
 * <li>The graph's {@link StoryGraph#plans plans} are sorted based on the order
 * of the graph's actions, where the last action in the plan is considered
 * first, then the second to last, etc. This increases the chances that a plan
 * can be stored as a {@link TailPlan} to save memory.</li>
 * <li>The graph's {@link StoryGraph#nodes nodes}, {@link StoryGraph#states
 * states}, and {@link StoryGraph#edges edges} are sorted ascending based on
 * their distance from the root node according to breadth-first search.</li>
 * </ul>
 * 
 * @author Stephen G. Ware
 */
public class Sort implements Task {
	
	/** The graph whose elements will be sorted */
	public final StoryGraph graph;
	
	/**
	 * Whether the graph's {@link StoryGraph#characters characters} will be
	 * sorted
	 */
	public final boolean characters;
	
	/**
	 * Whether the graph's {@link StoryGraph#fluents fluents} will be sorted
	 */
	public final boolean fluents;
	
	/**
	 * Whether the graph's {@link StoryGraph#values values} will be sorted
	 */
	public final boolean values;
	
	/**
	 * Whether the graph's {@link StoryGraph#states states} will be sorted
	 */
	public final boolean states;
	
	/**
	 * Whether the graph's {@link StoryGraph#actions actions} will be sorted
	 */
	public final boolean actions;
	
	/**
	 * Whether the graph's {@link StoryGraph#plans plans} will be sorted
	 */
	public final boolean plans;
	
	/**
	 * Whether the graph's {@link StoryGraph#nodes nodes} will be sorted
	 */
	public final boolean nodes;
	
	/**
	 * Whether the graph's {@link EdgeCollection#temporal temporal edges} will
	 * be sorted
	 */
	public final boolean temporal;
	
	/**
	 * Whether the graph's {@link EdgeCollection#epistemic epistemic edges} will
	 * be sorted
	 */
	public final boolean epistemic;
	
	/**
	 * Whether the graph's {@link StoryGraph#explanations explanations} will be
	 * sorted
	 */
	public final boolean explanations;
	
	/**
	 * Constructs a new story graph sorting task that can be configured to sort
	 * specific parts of the graph.
	 * 
	 * @param graph the graph to be sorted
	 * @param characters whether the graph's {@link StoryGraph#characters
	 * characters} should be sorted
	 * @param fluents whether the graph's {@link StoryGraph#fluents fluents}
	 * should be sorted
	 * @param values whether the graph's {@link StoryGraph#values values}
	 * should be sorted
	 * @param states whether the graph's {@link StoryGraph#states states} should
	 * be sorted
	 * @param actions whether the graph's {@link StoryGraph#actions actions}
	 * should be sorted
	 * @param plans whether the graph's {@link StoryGraph#plans plans} should be
	 * sorted
	 * @param nodes whether the graph's {@link StoryGraph#nodes nodes} should be
	 * sorted
	 * @param temporal whether the graph's {@link EdgeCollection#temporal
	 * temporal edges} should be sorted
	 * @param epistemic whether the graph's {@link EdgeCollection#epistemic
	 * epistemic edges} should be sorted
	 * @param explanations whether the graph's {@link
	 * StoryGraph#explanations explanations} should be sorted
	 */
	public Sort(
		StoryGraph graph,
		boolean characters,
		boolean fluents,
		boolean values,
		boolean states,
		boolean actions,
		boolean plans,
		boolean nodes,
		boolean temporal,
		boolean epistemic,
		boolean explanations
	) {
		this.graph = graph;
		this.characters = characters;
		this.fluents = fluents;
		this.values = values;
		this.states = states;
		this.actions = actions;
		this.plans = plans;
		this.nodes = nodes;
		this.temporal = temporal;
		this.epistemic = epistemic;
		this.explanations = explanations;
	}
	
	/**
	 * Constructs a story graph sorting task that sorts all elements of the
	 * graph.
	 * 
	 * @param graph the graph to be sorted
	 */
	public Sort(StoryGraph graph) {
		this(
			graph,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true
		);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This task sorts the elements of its {@link #graph story graph} that it
	 * has been configured to modify.
	 */
	@Override
	public void run(Status status) throws Exception {
		if(characters)
			new SortSymbols(graph.characters).run(status);
		if(fluents)
			new SortSymbols(graph.fluents).run(status);
		if(values)
			new SortSymbols(graph.values).run(status);
		if(actions)
			new SortSymbols(graph.actions).run(status);
		if(plans)
			new SortPlans(graph).run(status);
		if(nodes)
			new SortNodes(graph).run(status);
		if(states)
			new SortStates(graph).run(status);
		if(temporal)
			new SortEdges(graph.edges.temporal).run(status);
		if(epistemic)
			new SortEdges(graph.edges.epistemic).run(status);
		if(explanations)
			new SortExplanations(graph).run(status);
		status.setMessage("Story graph sorted");
	}
}