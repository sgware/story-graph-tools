package edu.uky.cs.nil.sg;

import java.util.ArrayList;

/**
 * A priority queue that stores {@link ExplanationTree explanation trees} and
 * {@link #pop() pops} them based on their {@link ExplanationTree#size() plan
 * length}, from shortest to longest.
 * 
 * @author Stephen G. Ware
 */
public class ExplanationPriorityQueue {
	
	/**
	 * A list of queues, where the queue at index n stores all explanation trees
	 * whose plan length is n
	 */
	private final ArrayList<BigQueue<ExplanationTree>> queues = new ArrayList<>();
	
	/**
	 * Creates a new empty explanation priority queue.
	 */
	public ExplanationPriorityQueue() {
		// default constructor
	}
	
	/**
	 * Returns the number of explanation trees currently stored in the queue.
	 * 
	 * @return the number of explanation trees
	 */
	public long size() {
		long size = 0;
		for(BigQueue<ExplanationTree> queue : queues)
			size += queue.size();
		return size;
	}
	
	/**
	 * Adds an explanation tree to the queue.
	 * 
	 * @param explanation the explanation tree to add
	 */
	public void push(ExplanationTree explanation) {
		while(explanation.size() >= queues.size())
			queues.add(new BigQueue<>());
		queues.get(explanation.size()).push(explanation);
	}
	
	/**
	 * Removes and returns the explanation tree with the lowest {@link
	 * ExplanationTree#size() plan length}. If more than one explanation tree
	 * in the queue has the same length, the one that has been on the queue
	 * longer will be returned.
	 * 
	 * @return the explanation tree with the shortest plan that has been on the
	 * queue the longest
	 */
	public ExplanationTree pop() {
		for(BigQueue<ExplanationTree> queue : queues)
			if(queue.size() > 0)
				return queue.pop();
		return queues.get(queues.size() - 1).pop();
	}
}