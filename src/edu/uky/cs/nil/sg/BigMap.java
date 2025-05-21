package edu.uky.cs.nil.sg;

/**
 * A custom implementation of a map, which associates unique keys with values,
 * which can hold up to {@link BigArrayList#MAX_CAPACITY} {@link Entry entries},
 * and which uses long integers to measure its {@link #size()}.
 * 
 * @param <K> the type of unique key associated with values in this map
 * @param <V> the type of element associated with the keys
 * @author Stephen G. Ware
 */
public abstract class BigMap<K, V> implements Iterable<Entry<K, V>> {
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof BigMap otherMap) {
			if(this.size() != otherMap.size())
				return false;
			for(Entry<K, V> entry : this)
				if(!otherMap.contains(entry.key, entry.getValue()))
					return false;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for(Entry<?, ?> entry : this)
			code += entry.hashCode();
		return code;
	}
	
	/**
	 * Returns the number of elements currently stored in the map.
	 * 
	 * @return the number of elements in the map
	 */
	public abstract long size();
	
	/**
	 * Returns true if the given key/value mapping exists in this map.
	 * 
	 * @param entry a key/value pair
	 * @return true if this entry appears in the map, false otherwise
	 * @see #contains(Object, Object)
	 */
	public boolean contains(Entry<?, ?> entry) {
		return contains(entry.key, entry.getValue());
	}
	
	/**
	 * Returns true if the map contains the given key and if it maps to the
	 * given value.
	 * 
	 * @param key the key in the key/value pair
	 * @param value the value in the key/value pair
	 * @return true if this map contains the key and maps it to the value
	 */
	public abstract boolean contains(Object key, Object value);
	
	/**
	 * Returns the value mapped to the given key, or null if the key does not
	 * exist or maps to null.
	 * 
	 * @param key the key whose value is desired
	 * @return the value associated with the key, or null if the key is not
	 * mapped to a value or if the key is mapped to null
	 */
	public abstract V get(Object key);
	
	/**
	 * Maps the given key to the given value, possibly overriding the previous
	 * mapping if the key already exists in the map.
	 * 
	 * @param key the key to be associated with the value
	 * @param value the value to associate with the key
	 */
	public abstract void put(K key, V value);
	
	/**
	 * Removes a key from the map, if it exists, as well as the value that key
	 * is mapped to.
	 * 
	 * @param key the key to remove from the map
	 */
	public abstract void remove(Object key);
	
	/**
	 * Removes all keys and values from this map.
	 */
	public abstract void clear();
}