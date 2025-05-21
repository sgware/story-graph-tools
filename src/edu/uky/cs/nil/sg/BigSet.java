package edu.uky.cs.nil.sg;

/**
 * A custom implementation of a collection that does not allow duplicates which
 * can hold up to {@link BigArrayList#MAX_CAPACITY} elements and which uses long
 * integers to measure its {@link #size()}.
 * 
 * @param <T> type of element stored in the set
 * @author Stephen G. Ware
 */
public abstract class BigSet<T> implements Iterable<T> {
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof BigSet otherSet) {
			if(this.size() != otherSet.size())
				return false;
			for(Object element : this)
				if(!otherSet.contains(element))
					return false;
			for(Object element : otherSet)
				if(!this.contains(element))
					return false;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for(T element : this)
			code += Utilities.hashCode(element);
		return code;
	}
	
	/**
	 * Returns the number of elements currently stored in the set.
	 * 
	 * @return the number of elements in the set
	 */
	public abstract long size();
	
	/**
	 * Checks whether an element is a member of the set.
	 * 
	 * @param element the element
	 * @return true if the element exists in this set, false otherwise
	 */
	public abstract boolean contains(Object element);
	
	/**
	 * Adds an element to the set if it was not already a member.
	 * 
	 * @param element the element
	 * @return true if the element was not a member and the set is now larger
	 * because the element was added, false if the element was already a member
	 */
	public abstract boolean add(T element);
	
	/**
	 * Removes an element from the set if it was a member.
	 * 
	 * @param element the element
	 * @return true if the element was a member and the set is now smaller
	 * because the element was removed, false if the element was not in the set
	 */
	public abstract boolean remove(Object element);
	
	/**
	 * Removes all elements from the set.
	 */
	public abstract void clear();
}