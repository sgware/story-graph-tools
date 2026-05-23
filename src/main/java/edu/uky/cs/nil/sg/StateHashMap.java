package edu.uky.cs.nil.sg;

/**
 * A {@link BigHashMap hash map} that uses {@link State states} as keys, where
 * two states are considered the same if they {@link State#getValue(Fluent)
 * return the same values} for all {@link Fluent fluents} and {@link
 * State#getUtility() return the same utility values} for all {@link Character
 * characters}.
 * 
 * @param <V> the type of element associated with the states
 * @author Stephen G. Ware
 */
public class StateHashMap<V> extends BigHashMap<State, V> {
	
	/** The story graph in which the state keys were created */
	protected final StoryGraph graph;
	
	/**
	 * Constructs a new state hash map from a given story graph.
	 * 
	 * @param graph the story graph in which the state keys were created
	 */
	public StateHashMap(StoryGraph graph) {
		super(graph.states.size() / 2);
		this.graph = graph;
	}
	
	@Override
	public boolean equals(Object object, State s2) {
		if(object instanceof State s1) {
			for(Fluent fluent : graph.fluents)
				if(!Utilities.equals(s1.getValue(fluent), s2.getValue(fluent)))
					return false;
			if(s1.getUtility() != s2.getUtility())
				return false;
			for(Character character : graph.characters)
				if(s1.getUtility(character) != s2.getUtility(character))
					return false;
			return true;
		}
		return false;
	}
	
	@Override
	public long hashCode(Object object) {
		long code = 0;
		if(object instanceof State state) {
			for(Fluent fluent : graph.fluents)
				code = code * 31 + Utilities.hashCode(state.getValue(fluent));
			code = code * 31 + Double.hashCode(state.getUtility());
			for(Character character : graph.characters)
				code = code * 31 + Double.hashCode(state.getUtility(character));
		}
		return code;
	}
}