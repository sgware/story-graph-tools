package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * A {@link BigMap map} based on a hash table, which uses a hash function to map
 * keys to an array of values. The function for {@link #hashCode(Object)
 * calculating the hash code} and {@link #equals(Object, Object) determining if
 * two objects are equal} can be overridden, and assuming the hash function
 * uniformly distributes keys over the array, entries can be accessed in
 * amortized constant time.
 * <p>
 * This hash table rehashes only when the {@link #size() number of entries}
 * exceeds the size of the {@link BigArrayList array list} they are stored in
 * (i.e. when the load factor exceeds 1).
 * 
 * @param <K> the type of unique key associated with values in this map
 * @param <V> the type of element associated with the keys
 * @author Stephen G. Ware
 */
public class BigHashMap<K, V> extends BigMap<K, V> {
	
	/**
	 * A custom {@link Entry entry} that also tracks the hash code of the key
	 * and the next entry in its bucket.
	 * 
	 * @param <K> the type of unique key associated with values in this map
	 * @param <V> the type of element associated with the keys
	 * @author Stephen G. Ware
	 */
	private static class HashEntry<K, V> extends Entry<K, V> {
		
		/**
		 * The hash code of the key, as calculated by {@link
		 * BigHashMap#hashCode(Object) this map's hash code functions}
		 */
		private final long code;
		
		/** The next entry in the same bucket as this entry */
		private HashEntry<K, V> next = null;
		
		/**
		 * Constructs a new big hash map entry with the given key, hash code,
		 * and value.
		 * 
		 * @param key the key
		 * @param code the hash code of the key
		 * @param value the value mapped to the key
		 */
		private HashEntry(K key, long code, V value) {
			super(key, value);
			this.code = code;
		}
	}
	
	/**
	 * The {@link BigArrayList array list} in which the entries of this map are
	 * stored. Entries form a {@link HashEntry#next linked list} in case more
	 * that one entry is mapped to the same index in the list.
	 */
	private final BigArrayList<HashEntry<K, V>> buckets;
	
	/** The number of entries currently stored in this map */
	private long size = 0;
	
	/**
	 * Constructs a new hash map which can initially hold up to a given number
	 * of entries before it needs to be expanded and rehashed.
	 * 
	 * @param capacity the number of entries that can be stored in the map
	 * before it is rehashed
	 */
	public BigHashMap(long capacity) {
		buckets = new BigArrayList<>(capacity);
		buckets.set(Math.max(capacity - 1, 0), null);
	}
	
	/**
	 * Constructs a new hash map with the {@link
	 * BigArrayList#DEFAULT_INITIAL_CAPACITY default initial capacity}.
	 */
	public BigHashMap() {
		this(BigArrayList.DEFAULT_INITIAL_CAPACITY);
	}
	
	@Override
	public String toString() {
		return "[Big Hash Map: " + size() + " entries]";
	}
	
	/**
	 * An {@link Iterator iterator} over the entries in a {@link BigHashMap hash
	 * map}.
	 * 
	 * @author Stephen G. Ware
	 */
	private class BigHashMapIterator implements Iterator<Entry<K, V>> {
		
		/**
		 * The current index in the {@link BigHashMap#buckets map's array list}
		 */
		private long index = -1;
		
		/**
		 * The entry that will be returned by the next call to {@link #next()}
		 */
		private HashEntry<K, V> current = null;
		
		/**
		 * Constructs a new hash map iterator.
		 */
		public BigHashMapIterator() {
			advance();
		}
		
		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public HashEntry<K, V> next() {
			if(!hasNext())
				throw Exceptions.iteratorEmpty();
			HashEntry<K, V> next = current;
			advance();
			return next;
		}
		
		private void advance() {
			if(current != null && current.next != null)
				current = current.next;
			else {
				current = null;
				while(current == null && index < buckets.capacity())
					current = buckets.get(++index);
			}
		}
	}
	
	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new BigHashMapIterator();
	}
	
	@Override
	public long size() {
		return size;
	}
	
	@Override
	public boolean contains(Object key, Object value) {
		HashEntry<K, V> entry = getEntry(key);
		return entry != null && Utilities.equals(entry.getValue(), value);
	}
	
	@Override
	public V get(Object key) {
		HashEntry<K, V> entry = getEntry(key);
		if(entry == null)
			return null;
		else
			return entry.getValue();
	}
	
	@Override
	public void put(K key, V value) {
		long code = hashCode(key);
		HashEntry<K, V> entry = getEntry(key, code);
		if(entry == null) {
			addEntry(new HashEntry<>(key, code, value));
			size++;
		}
		if(size() > buckets.size())
			rehash();
	}
	
	@Override
	public void remove(Object key) {
		long code = hashCode(key);
		long bucket = bucket(code);
		HashEntry<K, V> entry = buckets.get(bucket);
		if(entry == null)
			return;
		else if(code == entry.code && equals(key, entry.key))
			buckets.set(bucket, entry.next);
		else {
			HashEntry<K, V> previous = entry;
			while(previous.next != null) {
				if(code == previous.next.code && equals(key, previous.next.key)) {
					previous.next = previous.next.next;
					size--;
					return;
				}
				previous = previous.next;
			}
		}
	}
	
	@Override
	public void clear() {
		buckets.clear();
		size = 0;
	}
	
	/**
	 * Determines whether two key objects should be considered the same by this
	 * map. By default, this method returns the result of {@link
	 * Utilities#equals(Object, Object)}.
	 * 
	 * @param k1 the first key object
	 * @param k2 the second key object
	 * @return true if the two key objects should be considered the same
	 */
	protected boolean equals(Object k1, K k2) {
		return Utilities.equals(k1, k2);
	}
	
	/**
	 * Calculates the hash code of a key for this map. By default, this method
	 * returns the result of {@link Utilities#hashCode(Object)}, which only
	 * generates Java {@code int} hash codes. In order to ensure constant time
	 * access when this map holds more than {@link Integer#MAX_VALUE} entries,
	 * this method needs to be overridden to return a Java {@code long}.
	 * 
	 * @param key the key object whose hash code will be calculated
	 * @return the hash code of the key
	 */
	protected long hashCode(Object key) {
		return Utilities.hashCode(key);
	}
	
	private long bucket(long code) {
		return Math.abs(code % buckets.size());
	}
	
	private HashEntry<K, V> getEntry(Object key) {
		return getEntry(key, hashCode(key));
	}
	
	private HashEntry<K, V> getEntry(Object key, long code) {
		HashEntry<K, V> entry = buckets.get(bucket(code));
		while(entry != null && !(code == entry.code && equals(key, entry.key)))
			entry = entry.next;
		return entry;
	}
	
	private void addEntry(HashEntry<K, V> entry) {
		long bucket = bucket(entry.code);
		entry.next = buckets.get(bucket);
		buckets.set(bucket, entry);
	}
	
	private void rehash() {
		// Link all entries into a linked list and clear all buckets.
		HashEntry<K, V> first = null;
		HashEntry<K, V> last = null;
		for(long i = 0; i < buckets.size(); i++) {
			HashEntry<K, V> entry = buckets.get(i);
			if(entry != null) {
				if(first == null) {
					first = entry;
					last = entry;
				}
				else
					last.next = entry;
				while(last.next != null)
					last = last.next;
				buckets.set(i, null);
			}
		}
		// Double the number of buckets.
		buckets.set(buckets.size() * 2 - 1, null);
		// Put all entries into their new buckets.
		while(first != null) {
			HashEntry<K, V> entry = first;
			first = first.next;
			addEntry(entry);
		}
	}
}