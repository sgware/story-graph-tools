package edu.uky.cs.nil.sg;

import java.util.Objects;

/**
 * A wrapper around a {@link StoryGraph story graph} that makes it possible to
 * {@link #add(Node, Character, Sequence) add} {@link Explanation explanations}
 * only if an equivalent explanation does not already exist.
 * 
 * @author Stephen G. Ware
 */
public class ExplanationSet {
	
	/** The graph where explanations will be checked and added */
	protected final StoryGraph graph;
	
	/**
	 * Constructs an explanation set for a given story graph.
	 * 
	 * @param graph the graph whose explanations can be checked and to which
	 * explanations can be added
	 */
	public ExplanationSet(StoryGraph graph) {
		this.graph = graph;
	}
	
	/**
	 * Returns the number of explanations in the underlying story graph.
	 * 
	 * @return the number of explanations in the graph
	 */
	public long size() {
		return graph.explanations.size();
	}
	
	/**
	 * Checks whether an explanation exists in the underlying story graph.
	 * 
	 * @param node the explanation's node
	 * @param character the explanation's character
	 * @param plan the explanation's plan
	 * @return true if the node contains an explanation for the given character
	 * with the given plan
	 */
	public boolean contains(Node node, Character character, Sequence plan) {
		return get(node, character, plan) != null;
	}
	
	/**
	 * Finds and returns an explanation in the underlying story graph.
	 * 
	 * @param node the explanation's node
	 * @param character the explanation's character
	 * @param plan the explanation's plan
	 * @return an explanation from the given node with the given character with
	 * the given plan if one exists, or null otherwise
	 */
	public Explanation get(Node node, Character character, Sequence plan) {
		for(Explanation explanation : node.explanations)
			if(equals(explanation, character, plan))
				return explanation;
		return null;
	}
	
	private static final boolean equals(Explanation explanation, Character character, Sequence plan) {
		if(!Objects.equals(explanation.character, character))
			return false;
		if(explanation.size() != plan.size())
			return false;
		for(int i = 0; i < explanation.size(); i++)
			if(!explanation.get(i).equals(plan.get(i)))
				return false;
		return true;
	}
	
	/**
	 * Adds an explanation to the underlying story graph, unless an equivalent
	 * one already exists.
	 * 
	 * @param node the explanation's node
	 * @param character the explanation's character
	 * @param plan the explanation's plan
	 * @return true if the node did not contain an equivalent explanation
	 * and one has been added, or false if the node already contained an
	 * equivalent explanation and nothing has changed
	 */
	public boolean add(Node node, Character character, Sequence plan) {
		if(contains(node, character, plan))
			return false;
		else {
			graph.explanations.add(node, character, plan);
			return true;
		}
	}
}