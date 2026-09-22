package edu.uky.cs.nil.sg;

import java.util.Set;

/**
 * A {@link Sequence sequence of actions} made by adding an {@link Action
 * action} to the beginning of another sequence.
 * 
 * @author Stephen G. Ware
 */
public class TailSequence implements Sequence {
	
	/** The first action in the sequence */
	public final Action first;
	
	/**
	 * The rest of the sequence after the first action, or null if there are no
	 * other actions in the sequence
	 */
	public final Sequence rest;
	
	/**
	 * Construct a sequence from a first action and an existing sequence.
	 * 
	 * @param first the first action in the sequence
	 * @param rest the rest of the actions in the sequence, or null if there are
	 * no other actions in the sequence
	 */
	public TailSequence(Action first, Sequence rest) {
		if(first == null)
			throw Exceptions.cannotBeNull("first action");
		this.first = first;
		this.rest = rest;
	}
	
	/**
	 * Construct a sequence of one action.
	 * 
	 * @param first the first and only action in the sequence
	 */
	public TailSequence(Action first) {
		this(first, null);
	}
	
	@Override
	public String toString() {
		String string = first.toString();
		if(rest != null)
			for(Action action : rest)
				string += " " + action;
		return string;
	}
	
	@Override
	public int size() {
		if(rest == null)
			return 1;
		else
			return 1 + rest.size();
	}
	
	@Override
	public Action get(int index) {
		if(index >= size())
			throw new IndexOutOfBoundsException("The index " + index + " does not exist in a sequence of size " + size() + ".");
		else if(index == 0)
			return first;
		else
			return rest.get(index - 1);
	}
	
	@Override
	public Set<Character> consenting() {
		Set<Character> consenting = rest.consenting();
		for(Character character : first.consenting)
			consenting.add(character);
		return consenting;
	}
	
	/**
	 * Adds an action to the start of this sequence.
	 * 
	 * @param action the action to add to the start of the sequence
	 * @return a new sequence with the given action added to the beginning
	 */
	public TailSequence prepend(Action action) {
		return new TailSequence(action, this);
	}
}