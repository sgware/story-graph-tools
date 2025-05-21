package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * A {@link BigMap map} that uses {@link Numbered numbered story graph elements}
 * as keys and stores values in an {@link BigArrayList array list} indexed by
 * the key's {@link Numbered#getID() ID number}. This map takes less memory than
 * a {@link BigHashMap hash map} and guarantees constant time access to entries;
 * however, it may not work when the ID numbers of keys change, such as during
 * {@link StoryGraph#prune(java.util.function.Predicate, Status) graph pruning}.
 * 
 * @param <K> the type of unique numbered key associated with values in this map
 * @param <V> the type of element associated with the keys
 * @author Stephen G. Ware
 */
public class BigNumberedMap<K extends Numbered, V> extends BigMap<K, V> {
	
	/** The keys used by this map */
	private final NumberedList<K> keys;
	
	/** The list where values will be stored */
	private final BigArrayList<V> values;
	
	/** The number of entries currently stored in this map */
	private long size = 0;
	
	/**
	 * Constructs a new numbered map that uses the given collection of elements
	 * as keys. The capacity of the array list used to store the values will be
	 * the size of the list of keys.
	 * 
	 * @param list the collection of keys
	 */
	public BigNumberedMap(NumberedList<K> list) {
		this.keys = list;
		this.values = new BigArrayList<>(list.size());
	}
	
	@Override
	public String toString() {
		return "[Big Numbered Map: " + size() + " entries]";
	}
	
	/**
	 * An {@link Iterator iterator} over the entries in a {@link BigNumberedMap
	 * numbered map}.
	 * 
	 * @author Stephen G. Ware
	 */
	private class BigNumberedMapIterator implements Iterator<Entry<K, V>> {
		
		/** The index of the current key */
		private long index = -1;
		
		/**
		 * Constructs a new numbered map iterator.
		 */
		public BigNumberedMapIterator() {
			advance();
		}
		
		@Override
		public boolean hasNext() {
			return index < values.size();
		}
		
		@Override
		public Entry<K, V> next() {
			if(!hasNext())
				throw Exceptions.iteratorEmpty();
			Entry<K, V> next = new Entry<>(keys.get(index), values.get(index));
			advance();
			return next;
		}
		
		private void advance() {
			do index++;
			while(index < values.size() && values.get(index) == null);
		}
	}
	
	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new BigNumberedMapIterator();
	}
	
	@Override
	public long size() {
		return size;
	}
	
	@Override
	public boolean contains(Object key, Object value) {
		return Utilities.equals(get(key), value);
	}
	
	@Override
	public V get(Object key) {
		if(key instanceof Numbered n && keys.validate(n) && n.getID() < values.size())
			return values.get(n.getID());
		else
			return null;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The key must be an object of the list of keys this map was constructed
	 * with.
	 * <p>
	 * This map does not distinguish between a key having no value and having
	 * null as its value. Settings a key to null effectively removes it and
	 * reduces the {@link #size() size} of the map.
	 * 
	 * @throws IllegalArgumentException if the given key is not in this map's
	 * set of keys
	 */
	@Override
	public void put(K key, V value) {
		keys.require(key);
		if(get(key) == null && value != null)
			size++;
		else if(get(key) != null && value == null)
			size--;
		values.set(key.getID(), value);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void remove(Object key) {
		if(keys.validate(key))
			put((K) key, null);
	}
	
	@Override
	public void clear() {
		values.clear();
		size = 0;
	}
}