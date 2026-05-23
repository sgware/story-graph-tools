package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * A custom implementation of a first-in first-out queue which can hold up to
 * {@link Long#MAX_VALUE} elements and which uses long integers to measure its
 * {@link #size()}.
 * 
 * @param <T> type of element stored in the queue
 * @author Stephen G. Ware
 */
public class BigQueue<T> implements Iterable<T> {
	
	/**
	 * A singly linked list node for holding queue elements.
	 * 
	 * @author Stephen G. Ware
	 */
	private class Node {
		
		/** The queue element stored in this node */
		public final T element;
		
		/** The next node in the list */
		private Node next = null;
		
		/**
		 * Constructs a new queue node with a given element.
		 * 
		 * @param element the element to be stored in this node
		 */
		public Node(T element) {
			this.element = element;
		}
	}
	
	/**
	 * The element that has been on the queue the longest and will be removed
	 * by the next call to {@link #pop()}
	 */
	private Node front = null;
	
	/**
	 * The element that was most recently added to the queue by {@link
	 * #push(Object)}
	 */
	private Node back = null;
	
	/** The number of elements currently in the queue */
	private long size = 0;
	
	/**
	 * Creates a new empty queue.
	 */
	public BigQueue() {
		// default constructor
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof BigQueue otherQueue) {
			if(this.size() != otherQueue.size())
				return false;
			Iterator<?> mine = this.iterator();
			Iterator<?> theirs = otherQueue.iterator();
			while(mine.hasNext())
				if(!Utilities.equals(mine.next(), theirs.next()))
					return false;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for(T element : this)
			code += code * 31 + Utilities.hashCode(element);
		return code;
	}
	
	@Override
	public String toString() {
		return "[Big Queue: " + size() + " elements]";
	}
	
	/**
	 * An {@link Iterator iterator} over the elements in a {@link BigQueue
	 * queue}.
	 * 
	 * @author Stephen G. Ware
	 */
	private class BigQueueIterator implements Iterator<T> {
		
		/** The element to be returned by the next call to {@link #next()} */
		private Node current = front;
		
		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public T next() {
			if(!hasNext())
				throw Exceptions.iteratorEmpty();
			T next = current.element;
			current = current.next;
			return next;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * A queue's iterator returns elements in the order they would be {@link
	 * #pop() popped}.
	 */
	@Override
	public Iterator<T> iterator() {
		return new BigQueueIterator();
	}
	
	/**
	 * Returns the number of elements currently stored in the queue.
	 * 
	 * @return the number of elements
	 */
	public long size() {
		return size;
	}
	
	/**
	 * Returns, but does not remove, the element that has been on the queue the
	 * longest and which will be remove by the next call to {@link #pop() pop}.
	 * 
	 * @return the element at the front of the queue
	 * @throws java.util.NoSuchElementException if the queue is empty
	 */
	public T peek() {
		if(size() == 0)
			throw ToolsExceptions.queueEmpty();
		else
			return front.element;
	}
	
	/**
	 * Adds an element to the back of the queue. This element will not be {@link
	 * #pop() popped} until all elements on the queue at the time it was pushed
	 * have been popped.
	 * 
	 * @param element the element to add to the queue
	 */
	public void push(T element) {
		if(back == null) {
			front = new Node(element);
			back = front;
		}
		else {
			back.next = new Node(element);
			back = back.next;
		}
		size++;
	}
	
	/**
	 * Removes and returns the element that has been on the queue the longer.
	 * 
	 * @return the element that has been on the queue longest
	 * @throws java.util.NoSuchElementException if the queue is empty
	 */
	public T pop() {		
		T element = peek();
		front = front.next;
		size--;
		if(size() == 0)
			back = null;
		return element;
	}
}