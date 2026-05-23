package edu.uky.cs.nil.sg;

import java.util.HashMap;
import java.util.Map;

/**
 * An object for building {@link Plan plans} by {@link #prepend(Action)
 * prepending} {@link Action actions} to the start of the sequence so that when
 * duplicate plans are created the same object is reused to save memory.
 * 
 * @author Stephen G. Ware
 */
public abstract class PlanTree implements Comparable<PlanTree> {
	
	/**
	 * The empty plan for a given {@link StoryGraph story graph}.
	 * 
	 * @author Stephen G. Ware
	 */
	public static class Root extends PlanTree {
		
		/** The story graph for which plan objects will be created */
		private final StoryGraph graph;
		
		/** The empty plan object for this story graph */
		private Plan plan = null;
		
		/**
		 * Constructs a new empty plan object for a given story graph.
		 * 
		 * @param graph the story graph for which plan objects will be created
		 */
		public Root(StoryGraph graph) {
			this.graph = graph;
		}
		
		@Override
		public int size() {
			return 0;
		}
		
		@Override
		public Action get(int index) {
			throw Exceptions.indexOutOfBounds(index, 0);
		}
		
		@Override
		public Plan toPlan() {
			if(plan == null)
				plan = graph.plans.add();
			return plan;
		}
		
		@Override
		protected StoryGraph getStoryGraph() {
			return graph;
		}
	}
	
	/**
	 * A {@link PlanTree plan tree} formed by prepending a single {@link Action
	 * action} to the start of an existing plan tree.
	 * 
	 * @author Stephen G. Ware
	 */
	public static class Branch extends PlanTree {
		
		/** The first action in the plan */
		private final Action first;
		
		/** The plan after the first action */
		private final PlanTree rest;
		
		/** The plan object created for this sequence */
		private Plan plan = null;
		
		/**
		 * Constructs a new plan tree by prepending a single action to the start
		 * of an existing plan.
		 * 
		 * @param first the first action in the plan
		 * @param rest the other actions after the first action
		 */
		private Branch(Action first, PlanTree rest) {
			this.first = first;
			this.rest = rest;
		}
		
		@Override
		public int size() {
			return 1 + rest.size();
		}
		
		@Override
		public Action get(int index) {
			if(index < 0 || index >= size())
				throw Exceptions.indexOutOfBounds(index, size());
			else if(index == 0)
				return first;
			else
				return rest.get(index - 1);
		}
		
		@Override
		public Plan toPlan() {
			if(plan == null) {
				if(size() == 1)
					plan = getStoryGraph().plans.add(first);
				else
					plan = getStoryGraph().plans.add(first, rest.toPlan());
			}
			return plan;
		}
		
		@Override
		protected StoryGraph getStoryGraph() {
			return rest.getStoryGraph();
		}
	}
	
	/** Tracks the children of this plan tree based on the actions prepended */
	private final Map<Action, PlanTree> children = new HashMap<>();
	
	/**
	 * Creates a new empty plan tree.
	 */
	public PlanTree() {
		// default constructor
	}
	
	@Override
	public String toString() {
		String string = "";
		for(int i = 0; i < size(); i++)
			string += (i == 0 ? "" : " ") + get(i);
		return string;
	}
	
	@Override
	public int compareTo(PlanTree other) {
		int comparison = 0;
		int size = Math.min(this.size(), other.size());
		for(int i = 0; i < size && comparison == 0; i++)
			comparison = this.get(i).compareTo(other.get(i));
		if(comparison == 0)
			comparison = this.size() - other.size();
		return comparison;
	}
	
	/**
	 * Returns the story graph in which this object will create plans.
	 * 
	 * @return the story graph
	 */
	protected abstract StoryGraph getStoryGraph();
	
	/**
	 * Returns the number of action in this plan.
	 * 
	 * @return the number of actions
	 */
	public abstract int size();
	
	/**
	 * Returns the action in this plan at the given index.
	 * 
	 * @param index the index of the desired action
	 * @return the action at that index
	 * @throws IndexOutOfBoundsException if the index is negative or larger than
	 * the index of the last action in the plan
	 */
	public abstract Action get(int index);
	
	/**
	 * Creates a {@link Plan plan object} in {@link #getStoryGraph() this plan
	 * tree's story graph} corresponding to this plan.
	 * 
	 * @return the plan object
	 */
	public abstract Plan toPlan();
	
	/**
	 * Returns a new plan tree object with the given action as the first action.
	 * If such a plan already exists, the existing object will be returned
	 * instead of creating a new object, ensuring that multiple copies of the
	 * same {@link #toPlan() story graph plan} are not created.
	 * 
	 * @param action the action to prepend to the start of this plan
	 * @return a unique plan with that action as its first action
	 */
	public PlanTree prepend(Action action) {
		PlanTree child = children.get(action);
		if(child == null) {
			child = new Branch(action, this);
			children.put(action, child);
		}
		return child;
	}
}