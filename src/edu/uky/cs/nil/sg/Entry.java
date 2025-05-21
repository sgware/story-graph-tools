package edu.uky.cs.nil.sg;

/**
 * An individual key/value pair in a {@link BigMap map}. While the key remains
 * a constant, the {@link #getValue() value associated with it in this entry}
 * may change.
 * 
 * @param <K> the type of unique key associated with values in this map
 * @param <V> the type of element associated with the keys
 */
public class Entry<K, V> {
	
	/** The key of this key/value pair */
	public final K key;
	
	/** The value associated with the key */
	private V value = null;
	
	/**
	 * Constructs a new entry with a given key and value.
	 * 
	 * @param key the key
	 * @param value the value mapped to the key
	 */
	public Entry(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Entry otherEntry)
			return Utilities.equals(this.key, otherEntry.key) && Utilities.equals(this.getValue(), otherEntry.getValue());
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return Utilities.hashCode(key) + Utilities.hashCode(getValue());
	}
	
	@Override
	public String toString() {
		return "(" + key + ", " + getValue() + ")";
	}
	
	/**
	 * Returns the value currently associated with this entry's key.
	 * 
	 * @return the value
	 */
	public V getValue() {
		return value;
	}
	
	/**
	 * Sets the value associated with this entry's key, overwriting the previous
	 * value.
	 * 
	 * @param value the new value to be associated with the key
	 */
	public void setValue(V value) {
		this.value = value;
	}
}