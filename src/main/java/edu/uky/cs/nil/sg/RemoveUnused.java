package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * A {@link Task task} that removes unused elements from a {@link StoryGraph
 * story graph}.
 * <p>
 * Specifically, this task removes:
 * <ul>
 * <li>{@link Character Characters} who never appear as {@link Action#consenting
 * consenting characters} in any {@link TemporalEdge temporal edges}, which
 * never have {@link EpistemicEdge epistemic edges} from any {@link Node nodes},
 * and which never have {@link Explanation explanations}.
 * </li>
 * <li>{@link Fluent Fluents} which always have the same {@link
 * Node#getValue(Fluent) value} in all nodes.</li>
 * <li>{@link NominalValue Nominal values} that are never assigned as the {@link
 * Node#getValue(Fluent) value of a fluent} in any node.</li>
 * <li>{@link State State objects} which are never used as the {@link
 * Node#getState() state} of any nodes.</li>
 * <li>{@link Action Actions} which are never used as the {@link
 * TemporalEdge#label label} of any {@link TemporalEdge temporal edges} or in
 * any {@link StoryGraph#explanations explanations}.</li>
 * <li>{@link Plan Plan objects} which are never used as the {@link
 * Explanation#getPlan() plan} of any {@link Explanation explanation}.</li>
 * </ul>
 * 
 * @author Stephen G. Ware
 */
public class RemoveUnused implements Task {
	
	/** The graph whose unused elements will be removed */
	public final StoryGraph graph;
	
	/** Whether unused characters will be removed */
	public final boolean characters;
	
	/** Whether fluents that never changes will be removed */
	public final boolean fluents;
	
	/** Whether unused values will be removed */
	public final boolean values;
	
	/** Whether unused states will be removed */
	public final boolean states;
	
	/** Whether unused actions will be removed */
	public final boolean actions;
	
	/** Whether unused plans will be removed */
	public final boolean plans;
	
	/**
	 * Constructs a remove unused elements task for the given story graph.
	 * 
	 * @param graph the story graph whose unused elements will be removed
	 * @param characters whether unused characters will be removed
	 * @param fluents whether fluents that never change will be removed
	 * @param values whether unused values will be removed
	 * @param states whether unused states will be removed
	 * @param actions whether unused actions will be removed
	 * @param plans whether unused plans will be removed
	 */
	public RemoveUnused(
		StoryGraph graph,
		boolean characters,
		boolean fluents,
		boolean values,
		boolean states,
		boolean actions,
		boolean plans
	) {
		this.graph = graph;
		this.characters = characters;
		this.fluents = fluents;
		this.values = values;
		this.states = states;
		this.actions = actions;
		this.plans = plans;
	}
	
	/**
	 * Constructs a remove unused elements task for the given story graph that
	 * removes all unused elements.
	 * 
	 * @param graph the story graph whose unused elements will be removed
	 */
	public RemoveUnused(StoryGraph graph) {
		this(
			graph,
			true,
			true,
			true,
			true,
			true,
			true
		);
	}
	
	@Override
	public void run(Status status) throws Exception {
		UnusedElementSearch unused = new UnusedElementSearch(graph);
		unused.run(status);
		long total = 0;
		if(characters)
			total += prune(graph.characters, unused, status);
		if(fluents)
			total += prune(graph.fluents, unused, status);
		if(values)
			total += prune(graph.values, unused, status);
		if(states)
			total += prune(graph.states, unused, status);
		if(actions)
			total += prune(graph.actions, unused, status);
		if(plans)
			total += prune(graph.plans, unused, status);
		status.setMessage(total + " unused elements removed");
	}
	
	private final <T> int prune(SymbolList<?> list, Predicate<Object> predicate, Status status) {
		int before = list.size();
		list.remove(predicate, status);
		return before - list.size();
	}
	
	private final <T> long prune(NumberedList<?> list, Predicate<Object> predicate, Status status) {
		long before = list.size();
		list.remove(predicate, status);
		return before - list.size();
	}
}