package edu.uky.cs.nil.sg;

import java.util.Comparator;

/**
 * A {@link Task task} that sorts the {@link Plan plans} in a {@link StoryGraph
 * story graph} to maximize the chances that a {@link PlanList#add(Action, Plan)
 * a tail plan will be created} to save memory when reading the story graph's
 * plans.
 * <p>
 * In other words, plans will be sorted by considering their actions in reverse.
 * This means that first the last actions of two plans is compared, then the
 * second to last actions, and so on. When two plans end with the same actions,
 * the shorter plan is ordered first.
 * <p>
 * The end result is this: when possible, a plan is the same as the plan ordered
 * immediately before it, except for possibly its first action. This allows a
 * story graph to save memory by creating tail plans when reading in its list of
 * plans.
 * 
 * @author Stephen G. Ware
 */
public class SortPlans implements Task {
	
	/** A comparator that sorts plans based on their actions in reverse */
	public static final Comparator<Plan> PLAN_SUFFIX = new Comparator<>() {
		@Override
		public int compare(Plan plan1, Plan plan2) {
			int size = Math.min(plan1.size(), plan2.size());
			for(int i = 0; i < size; i++) {
				Action action1 = plan1.get(plan1.size() - 1 - i);
				Action action2 = plan2.get(plan2.size() - 1 - i);
				int comparison = action1.compareTo(action2);
				if(comparison != 0)
					return comparison;
			}
			return plan1.size() - plan2.size();
		}
	};
	
	/** The story graph whose plans will be sorted */
	protected StoryGraph graph;
	
	/**
	 * Constructs a new story graph plan sort task.
	 * 
	 * @param graph the story graph whose plans will be sorted
	 */
	public SortPlans(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		graph.plans.sort(PLAN_SUFFIX, status);
	}
}