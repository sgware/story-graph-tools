package edu.uky.cs.nil.sg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A {@link BreadthFirstSearch breadth-first search} {@link Task task} that
 * marks {@link StoryGraph story graph} elements used as they are encountered,
 * allowing any unused elements to be identified.
 * 
 * @author Stephen G. Ware
 */
public class UnusedElementSearch extends BreadthFirstSearch implements Predicate<Object> {
	
	/** The set of used characters */
	private final Set<Character> characters;
	
	/** The map of fluents to all possible values they can have */
	private final Map<Fluent, Set<Value>> fluents;
	
	/** The set of used values */
	private final Set<NominalValue> values;
	
	/** The set of used states */
	private final BigSet<State> states;
	
	/** The set of used actions */
	private final Set<Action> actions;
	
	/** The set of used plans */
	private final BigSet<Plan> plans;
	
	/**
	 * Constructs a new unused element search task.
	 * 
	 * @param graph the story graph to be searched for unused elements
	 */
	public UnusedElementSearch(StoryGraph graph) {
		super(graph, true);
		this.characters = new HashSet<>(graph.characters.size());
		this.fluents = new HashMap<>(graph.fluents.size());
		for(Fluent fluent : graph.fluents)
			this.fluents.put(fluent, new HashSet<>());
		this.values = new HashSet<>(graph.values.size());
		this.states = new BigNumberedSet<>(graph.states);
		this.actions = new HashSet<>(graph.actions.size());
		this.plans = new BigNumberedSet<>(graph.plans);
	}
	
	@Override
	protected void visit(Node node) {
		for(Fluent fluent : graph.fluents) {
			Value value = node.getValue(fluent);
			fluents.get(fluent).add(node.getValue(fluent));
			if(value instanceof NominalValue nominal)
				values.add(nominal);
		}
		states.add(node.getState());
		for(Explanation explanation : node.explanations)
			plans.add(explanation.getPlan());
		super.visit(node);
	}
	
	@Override
	protected void visit(TemporalEdge temporal) {
		for(Character character : temporal.label.consenting)
			characters.add(character);
		actions.add(temporal.label);
		super.visit(temporal);
	}
	
	@Override
	protected void visit(EpistemicEdge epistemic) {
		characters.add(epistemic.label);
		super.visit(epistemic);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method returns true if the given element is an unused story graph
	 * element, or if it is a {@link Fluent fluent} which has the same value in
	 * every state.
	 */
	@Override
	public boolean test(Object object) {
		if(object instanceof Character character)
			return !characters.contains(character);
		else if(object instanceof Fluent fluent)
			return fluents.get(fluent).size() < 2;
		else if(object instanceof NominalValue nominal)
			return !values.contains(nominal);
		else if(object instanceof State state)
			return !states.contains(state);
		else if(object instanceof Action action)
			return !actions.contains(action);
		else if(object instanceof Plan plan)
			return !plans.contains(plan);
		else
			return false;
	}
}