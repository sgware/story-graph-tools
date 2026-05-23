package edu.uky.cs.nil.sg;

import java.util.Comparator;

/**
 * A {@link Task task} that sorts the {@link Explanation explanations} in a
 * {@link StoryGraph story graph} based on the {@link Explanation#node node}
 * they are associated with, the {@link Explanation#character character} forming
 * the plan, and the {@link Explanation#getPlan() plan} being formed.
 * <p>
 * Explanations are sorted as follows:
 * <ul>
 * <li>Explanations are first sorted by {@link Explanation#node node}, with
 * earlier nodes ordered first.</li>
 * <li>Explanations are then sorted by {@link Explanation#character character},
 * with explanations for the author (null character) first.</li>
 * <li>Explanations are then sorted by the first action in their {@link
 * Explanation#getPlan() plan}.</li>
 * <li>Explanations are then sorted by {@link Explanation#getPlan() their plan}
 * as a whole.</li>
 * </ul>
 * 
 * @author Stephen G. Ware
 */
public class SortExplanations implements Task {
	
	/** A comparator that sorts explanations by node, character, and plan */
	public static final Comparator<Explanation> EXPLANATION_NODE = new Comparator<>() {
		
		@Override
		public int compare(Explanation explanation1, Explanation explanation2) {
			int comparison = explanation1.node.compareTo(explanation2.node);
			if(comparison == 0)
				comparison = SortExplanations.compare(explanation1.character, explanation2.character);
			if(comparison == 0)
				comparison = explanation1.getPlan().get(0).compareTo(explanation2.getPlan().get(0));
			if(comparison == 0)
				comparison = explanation1.getPlan().compareTo(explanation2.getPlan());
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
	 * Constructs a new story graph explanation sort task.
	 * 
	 * @param graph the story graph whose explanations will be sorted
	 */
	public SortExplanations(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		graph.explanations.sort(EXPLANATION_NODE, status);
	}
}