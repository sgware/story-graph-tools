package edu.uky.cs.nil.sg;

/**
 * A {@link BigHashMap hash map} that uses {@link Plan plans} as keys, where two
 * plans are considered the same if they represent the same sequence of actions.
 * 
 * @param <V> the type of element associated with the plans
 * @author Stephen G. Ware
 */
public class PlanHashMap<V> extends BigHashMap<Plan, V> {
	
	/**
	 * Constructs a new plan hash map from a given story graph.
	 * 
	 * @param graph the story graph in which the plan keys were created
	 */
	public PlanHashMap(StoryGraph graph) {
		super(graph.plans.size() / 2);
	}
	
	@Override
	public boolean equals(Object object, Plan p2) {
		return object instanceof Plan p1 && p1.size() == p2.size() && p1.contains(p2);
	}
	
	@Override
	public long hashCode(Object object) {
		long code = 0;
		if(object instanceof Plan plan)
			for(Action action : plan)
				code = code * 31 + action.hashCode();
		return code;
	}
}