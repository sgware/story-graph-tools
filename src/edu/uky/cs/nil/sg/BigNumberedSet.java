package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * A {@link BigSet set} of {@link Numbered numbered story graph elements}.
 * 
 * @param <N> type of numbered element stored in the set
 * @author Stephen G. Ware
 */
public class BigNumberedSet<N extends Numbered> extends BigSet<N> {
	
	/** Maps elements to true if they are in the set and false otherwise */
	private final BigNumberedMap<N, Boolean> map;
	
	/**
	 * Constructs an empty set of numbered story graph elements from a list of
	 * the elements that could potentially be members of the set.
	 * 
	 * @param list the collection of elements that could potentially be members
	 * of the set
	 */
	public BigNumberedSet(NumberedList<N> list) {
		map = new BigNumberedMap<>(list);
	}
	
	@Override
	public String toString() {
		return "[Big Numbered Set: " + size() + " elements]";
	}
	
	/**
	 * An {@link Iterator iterator} over the elements in a big set of numbered
	 * story graph elements.
	 * 
	 * @author Stephen G. Ware
	 */
	private class BigNumberedSetIterator implements Iterator<N> {
		
		/**
		 * The iterator over the set's {@link BigNumberedSet#map element map}
		 */
		private final Iterator<Entry<N, Boolean>> entries = map.iterator();

		@Override
		public boolean hasNext() {
			return entries.hasNext();
		}

		@Override
		public N next() {
			return entries.next().key;
		}
	}
	
	@Override
	public Iterator<N> iterator() {
		return new BigNumberedSetIterator();
	}
	
	@Override
	public long size() {
		return map.size();
	}
	
	@Override
	public boolean contains(Object element) {
		return Utilities.equals(map.get(element), true);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The element added must be one of the objects from the set of objects this
	 * set may contain, otherwise an exception will be thrown.
	 * 
	 * @throws IllegalArgumentException if the object is not one of the objects
	 * that can potentially be member of this set
	 */
	@Override
	public boolean add(N element) {
		if(contains(element))
			return false;
		else {
			map.put(element, true);
			return true;
		}
	}
	
	@Override
	public boolean remove(Object element) {
		if(contains(element)) {
			map.remove(element);
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void clear() {
		map.clear();
	}
}