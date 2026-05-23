package edu.uky.cs.nil.sg;

import edu.uky.cs.nil.sabre.graph.StateGraph;
import edu.uky.cs.nil.sabre.graph.StateNode;

/**
 * A {@link BigHashMap hash map} that maps {@link StateNode state graph nodes}
 * to their corresponding {@link State story graph state objects}.
 * 
 * @author Stephen G. Ware
 */
public class StateMapping extends BigHashMap<StateNode, State> {
	
	/**
	 * Returns a unique sequential integer index for each {@link Character} in a
	 * {@link StoryGraph story graph} and the author, which is represented by
	 * {@code null}.
	 * 
	 * @param character the character, or null to represent the author
	 * @return a unique sequential integer index starting at 0
	 */
	private static final int index(Character character) {
		if(character == null)
			return 0;
		else
			return character.getID() + 1;
	}
	
	/** The story graph where the state objects will be created */
	protected final StoryGraph graph;
	
	/**
	 * Maps {@link Fluent story graph fluents} to their corresponding {@link
	 * edu.uky.cs.nil.sabre.Fluent Sabre fluents}
	 */
	private final edu.uky.cs.nil.sabre.Fluent[] fluents;
	
	/**
	 * Maps {@link Character story graph characters} to their corresponding
	 * {@link edu.uky.cs.nil.sabre.Character Sabre characters}
	 */
	private final edu.uky.cs.nil.sabre.Character[] characters;
	
	/**
	 * Constructs a new state mapping between a given state graph and story
	 * graph.
	 * 
	 * @param states a Sabre state graph whose nodes will be mapped to story
	 * graph states
	 * @param story the story graph where the corresponding state objects will
	 * be created
	 */
	public StateMapping(StateGraph states, StoryGraph story) {
		this.graph = story;
		this.fluents = new edu.uky.cs.nil.sabre.Fluent[states.fluents.size()];
		for(edu.uky.cs.nil.sabre.Fluent fluent : states.fluents)
			this.fluents[story.fluents.add(fluent.toString()).getID()] = fluent;
		this.characters = new edu.uky.cs.nil.sabre.Character[states.characters.size() + 1];
		for(edu.uky.cs.nil.sabre.Character character : states.characters) {
			if(character == null)
				this.characters[index(null)] = character;
			else
				this.characters[index(story.characters.add(character.toString()))] = character;
		}
	}
	
	@Override
	public boolean equals(Object object, StateNode s2) {
		if(object instanceof StateNode s1) {
			for(edu.uky.cs.nil.sabre.Fluent fluent : s1.graph.fluents)
				if(!s1.getValue(fluent).equals(s2.getValue(fluent)))
					return false;
			if(!s1.getUtility(null).equals(s2.getUtility(null)))
				return false;
			for(edu.uky.cs.nil.sabre.Character character : s1.graph.characters)
				if(!s1.getUtility(character).equals(s2.getUtility(character)))
					return false;
			return true;
		}
		else
			return false;
	}
	
	@Override
	public long hashCode(Object object) {
		long code = 0;
		if(object instanceof StateNode state) {
			for(edu.uky.cs.nil.sabre.Fluent fluent : state.graph.fluents)
				code = code * 31 + state.getValue(fluent).hashCode();
			code = code * 31 + state.getUtility(null).hashCode();
			for(edu.uky.cs.nil.sabre.Character character : state.graph.characters)
				code = code * 31 + state.getUtility(character).hashCode();
		}
		return code;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the given key object is a {@link StateNode Sabre state graph node},
	 * this method will return the corresponding {@link State story graph state
	 * object}, creating it if it does not yet exist. In the returned state,
	 * each {@link Fluent fluent} in the story graph will have its value set to
	 * the {@link Value story graph value} that corresponds to the value from
	 * the state node for the corresponding Sabre fluent.
	 */
	@Override
	public State get(Object key) {
		State state = super.get(key);
		if(state == null && key instanceof StateNode node) {
			state = graph.states.add(fluent -> value(fluent, node), character -> utility(character, node));
			put(node, state);
		}
		return state;
	}
	
	/**
	 * Returns the {@link Value story graph value} to assign to the given {@link
	 * Fluent story graph fluent} based on the given {@link StateNode Sabre
	 * state graph node}.
	 * 
	 * @param fluent the story graph fluent whose value is desired
	 * @param node the Sabre state graph node from which to get the fluent's
	 * value
	 * @return the story graph value to assign to the story graph fluent
	 */
	protected Value value(Fluent fluent, StateNode node) {
		edu.uky.cs.nil.sabre.logic.Value value = node.getValue(fluents[fluent.getID()]);
		if(value.equals(edu.uky.cs.nil.sabre.logic.Unknown.UNKNOWN))
			return null;
		else if(value instanceof edu.uky.cs.nil.sabre.Number)
			return NumericValue.get(((edu.uky.cs.nil.sabre.Number) value).value);
		else
			return graph.values.add(value.toString());
	}
	
	/**
	 * Returns the numeric utility value to assign to the given {@link Character
	 * story graph character} based on the given {@link StateNode Sabre state
	 * graph node}.
	 * 
	 * @param character the story graph character whose utility value is desired
	 * @param node the Sabre state graph node from which to get the fluent's
	 * value
	 * @return the utility value to assign to the story graph character
	 */
	protected double utility(Character character, StateNode node) {
		edu.uky.cs.nil.sabre.logic.Value value = node.getUtility(characters[index(character)]);
		if(value instanceof edu.uky.cs.nil.sabre.Number)
			return ((edu.uky.cs.nil.sabre.Number) value).value;
		else
			return Double.NaN;
	}
}