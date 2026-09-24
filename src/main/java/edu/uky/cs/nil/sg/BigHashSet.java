package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * A {@link BigSet} backed by a {@link BigHashMap}.
 * 
 * @param <T> the type of element in the set
 * @author Stephen G. Ware
 */
public class BigHashSet<T> extends BigSet<T> {
	
	/** A map that maps objects to themselves */
	private final BigHashMap<T, T> map;
	
	/**
	 * Constructs an empty big set backed by a given hash map.
	 * 
	 * @param map a hash map that maps objects to themselves
	 */
	public BigHashSet(BigHashMap<T, T> map) {
		this.map = map;
	}
	
	/**
	 * Constructs an empty big set backed by a new hash map.
	 */
	public BigHashSet() {
		this(new BigHashMap<T, T>());
	}
	
	/**
	 * Iterates through the entries in the hash map and returns each entry's
	 * {@link Entry#key key}.
	 * 
	 * @author Stephen G. Ware
	 */
	private class BigHashSetIterator implements Iterator<T> {
		
		/** The keys in the map */
		private final Iterator<Entry<T, T>> entries = map.iterator();
		
		@Override
		public boolean hasNext() {
			return entries.hasNext();
		}
		
		@Override
		public T next() {
			Entry<T, T> entry = entries.next();
			return entry.key;
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		return new BigHashSetIterator();
	}
	
	@Override
	public long size() {
		return map.size();
	}
	
	@Override
	public boolean contains(Object element) {
		return map.contains(element, element);
	}
	
	@Override
	public boolean add(T element) {
		T value = map.get(element);
		if(value == null) {
			map.put(element, element);
			return true;
		}
		else
			return false;
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