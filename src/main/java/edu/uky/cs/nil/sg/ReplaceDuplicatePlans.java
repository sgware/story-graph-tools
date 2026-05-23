package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that replaces duplicate {@link Plan plans} in a {@link
 * StoryGraph story graph} to save memory--the plans are considered the same
 * if they are the same series of {@link Action actions}.
 * 
 * @author Stephen G. Ware
 */
public class ReplaceDuplicatePlans implements Task {
	
	/** The story graph whose duplicate plans will be replaced */
	protected final StoryGraph graph;
	
	/**
	 * Constructs a new duplicate plan replacement task.
	 * 
	 * @param graph the graph whose duplicate plans will be replaced
	 */
	public ReplaceDuplicatePlans(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		status.set("Replacing duplicate plans", graph.explanations.size());
		long before = graph.plans.size();
		PlanHashMap<Plan> replacements = new PlanHashMap<>(graph);
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
		status.setMessage("Replaced " + (before - graph.plans.size()) + " duplicate plans");
	}
}