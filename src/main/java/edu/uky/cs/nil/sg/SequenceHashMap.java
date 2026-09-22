package edu.uky.cs.nil.sg;

/**
 * A {@link BigHashMap hash map} that uses {@link Sequence action sequences} as
 * keys, where two sequences are considered the same if they represent the same
 * actions in the same order.
 * 
 * @param <V> the type of element associated with the sequences
 * @author Stephen G. Ware
 */
public class SequenceHashMap<V> extends BigHashMap<Sequence, V> {
	
	/**
	 * Constructs a new sequence hash map with a given initial capacity.
	 * 
	 * @param capacity the initial capacity of the map
	 */
	public SequenceHashMap(long capacity) {
		super(capacity);
	}
	
	@Override
	public boolean equals(Object object, Sequence s2) {		
		return object instanceof Sequence s1 && s1.size() == s2.size() && s1.contains(s2);
	}
	
	@Override
	public long hashCode(Object object) {
		long code = 0;
		if(object instanceof Sequence sequence)
			for(Action action : sequence)
				code = code * 31 + action.hashCode();
		return code;
	}
}