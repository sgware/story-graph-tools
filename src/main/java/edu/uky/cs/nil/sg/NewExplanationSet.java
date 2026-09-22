package edu.uky.cs.nil.sg;

/**
 * Tracks a set of {@link Explanation explanations} in a {@link StoryGraph story
 * graph} and provides a way to {@link #add(Node, Character, Sequence) add}
 * explanations to this set without creating duplicates in the story graph.
 * <p>
 * This class is similar to {@link ExplanationSet}, but it tracks only
 * explanations that have been added to this set; it does not treat the
 * explanations that already exist in the story graph as part of this set.
 * 
 * @author Stephen G. Ware
 */
public class NewExplanationSet {
	
	/** Tracks all explanations in a story graph */
	protected final ExplanationSet explanations;
	
	/** Tracks new explanations added to the graph by this set */
	private final BigNumberedSet<Explanation> set;
	
	/**
	 * Constructs a new explanation set for a given story graph.
	 * 
	 * @param graph the graph to which new explanations will be added
	 */
	public NewExplanationSet(StoryGraph graph) {
		this.explanations = new ExplanationSet(graph);
		this.set = new BigNumberedSet<>(graph.explanations);
	}
	
	/**
	 * Returns the number of new explanations that have been added to this set.
	 * 
	 * @return the number of explanations that have been added to this set
	 */
	public long size() {
		return set.size();
	}
	
	/**
	 * Checks whether an explanation with the given qualities has been added to
	 * this set.
	 * 
	 * @param node the explanation's node
	 * @param character the explanation's character
	 * @param plan the explanation's plan
	 * @return true if an explanation with these qualities has been added to
	 * this set, false otherwise
	 */
	public boolean contains(Node node, Character character, Sequence plan) {
		Explanation explanation = explanations.get(node, character, plan);
		return explanation != null && set.contains(explanation);
	}
	
	/**
	 * Finds and returns an explanation with the given qualities that has been
	 * added to this set.
	 * 
	 * @param node the explanation's node
	 * @param character the explanation's character
	 * @param plan the explanation's plan
	 * @return an explanation added to this set with the given node, character,
	 * and plan, or null if this set has no such explanation
	 */
	public Explanation get(Node node, Character character, Sequence plan) {
		Explanation explanation = explanations.get(node, character, plan);
		if(explanation == null || !set.contains(explanation))
			return null;
		else
			return explanation;
	}
	
	/**
	 * Adds an explanation to this set and, if it did not exist in the
	 * underlying story graph, also adds it to the story graph.
	 * 
	 * @param node the explanation's node
	 * @param character the explanation's character
	 * @param plan the explanation's plan
	 * @return true if this set did not contain this explanation and it has been
	 * added, or false if this set already contained the explanation
	 */
	public boolean add(Node node, Character character, Sequence plan) {
		Explanation explanation = explanations.get(node, character, plan);
		if(explanation == null)
			explanation = explanations.graph.explanations.add(node, character, plan);
		return set.add(explanation);
	}
}