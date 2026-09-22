package edu.uky.cs.nil.sg;

import java.util.Comparator;

/**
 * A {@link Task task} that sorts the {@link Explanation explanations} in a
 * {@link StoryGraph story graph} ascending based on the {@link Node#getID() ID
 * number} of the {@link Explanation#node node} they are associated with, the
 * {@link Explanation#character character} forming the plan, and the {@link
 * Explanation#getPlan() plan} being formed.
 * 
 * @author Stephen G. Ware
 */
public class SortExplanations implements Task {
	
	/** A comparator that sorts explanations by node, character, and plan */
	public static final Comparator<Explanation> NODE_THEN_CHARACTER_THEN_PLAN = new Comparator<>() {
		
		@Override
		public int compare(Explanation explanation1, Explanation explanation2) {
			int comparison = Long.compare(explanation1.node.getID(), explanation2.node.getID());
			if(comparison == 0)
				comparison = SortExplanations.compare(explanation1.character, explanation2.character);
			if(comparison == 0)
				comparison = Long.compare(explanation1.getPlan().getID(), explanation2.getPlan().getID());
			return comparison;
		}
	};
	
	private static final int compare(Character c1, Character c2) {
		if(c1 == null && c2 == null)
			return 0;
		else if(c1 == null)
			return -1;
		else if(c2 == null)
			return 1;
		else
			return c1.compareTo(c2);
	}
	
	/** The story graph whose explanations will be sorted */
	protected final StoryGraph graph;
	
	/**
	 * Constructs a story graph explanation sort task.
	 * 
	 * @param graph the story graph whose explanations will be sorted
	 */
	public SortExplanations(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		graph.explanations.sort(NODE_THEN_CHARACTER_THEN_PLAN, status);
	}
}