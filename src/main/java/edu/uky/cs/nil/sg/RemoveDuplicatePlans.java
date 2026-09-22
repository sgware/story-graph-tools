package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that removes duplicate {@link Plan plans} from a story
 * graph's list of {@link StoryGraph#plans}. If two {@link Explanation
 * explanations} are found that have the same {@link Explanation#getPlan()
 * sequence of actions} but use different plan objects, one plan object is
 * removed and the explanation that was using the duplicate plan will now use
 * the preserved plan.
 * <p>
 * This operation does not modify the content of a story graph but may save
 * memory by removing unnecessary duplicate objects.
 * 
 * @author Stephen G. Ware
 */
public class RemoveDuplicatePlans implements Task {
	
	/** The graph whose duplicate plans will be removed */
	public final StoryGraph graph;
	
	/**
	 * Constructs a remove duplicate plans task for the given story graph.
	 * 
	 * @param graph the story graph whose duplicate nodes will be removed
	 */
	public RemoveDuplicatePlans(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		status.set("Removing duplicate plans", graph.explanations.size());
		long before = graph.plans.size();
		SequenceHashMap<Plan> replacements = new SequenceHashMap<>(graph.plans.size() / 2);
		for(Explanation explanation : graph.explanations) {
			Plan replacement = replacements.get(explanation.getPlan());
			if(replacement == null)
				replacements.put(explanation.getPlan(), explanation.getPlan());
			else if(replacement != explanation.getPlan()) {
				explanation.getPlan().setID(Settings.PRUNED);
				explanation.setPlan(replacement);
			}
			status.increment();
		}
		graph.plans.renumber(status);
		status.setMessage("Removed " + (before - graph.plans.size()) + " duplicate plans");
	}
}
