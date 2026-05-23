package edu.uky.cs.nil.sg;

/**
 * An object for building {@link Explanation explanations} by {@link
 * #prepend(TemporalEdge) prepending} {@link TemporalEdge temporal edges} to the
 * start of the sequence.
 * 
 * @author Stephen G. Ware
 */
public abstract class ExplanationTree implements Comparable<ExplanationTree> {
	
	/**
	 * An explanation tree that represents an explanation that ends at a node
	 * for a character.
	 * 
	 * @author Stephen G. Ware
	 */
	public static class Root extends ExplanationTree {
		
		/** The node at which the explanation ends */
		private final Node node;
		
		/** The character who is taking the plan */
		private final Character character;
		
		/** The (empty) plan the character takes */
		private final PlanTree plan;
		
		/**
		 * Constructs a new explanation root which ends at a given node, for a
		 * given character (or null for the author), and has an empty plan.
		 * 
		 * @param node the node at which the explanation ends
		 * @param character the character taking the plan
		 * @param plan the (empty) plan the character takes
		 */
		public Root(Node node, Character character, PlanTree.Root plan) {
			this.node = node;
			this.character = character;
			this.plan = plan;
		}
		
		@Override
		public Node getStart() {
			return node;
		}
		
		@Override
		public Character getCharacter() {
			return character;
		}
		
		@Override
		protected PlanTree getPlanTree() {
			return plan;
		}
		
		@Override
		public Node getEnd() {
			return node;
		}
		
		@Override
		public int size() {
			return 0;
		}
		
		@Override
		public TemporalEdge get(int index) {
			throw Exceptions.indexOutOfBounds(index, 0);
		}
	}
	
	/**
	 * An explanation tree created by prepending a {@link TemporalEdge temporal
	 * edge} to an existing explanation tree.
	 * 
	 * @author Stephen G. Ware
	 */
	public static class Branch extends ExplanationTree {
		
		/** The first temporal edge in the explanation */
		private final TemporalEdge edge;
		
		/** All temporal edges after the first */
		private final ExplanationTree rest;
		
		/**
		 * Constructs a new explanation by prepending a single temporal edge to
		 * an existing explanation tree.
		 * 
		 * @param edge the first temporal edge in the explanation
		 * @param rest the rest of the explanation after the first edge
		 */
		private Branch(TemporalEdge edge, ExplanationTree rest) {
			this.edge = edge;
			this.rest = rest;
		}
		
		@Override
		public Node getStart() {
			return edge.tail;
		}
		
		@Override
		public Character getCharacter() {
			return rest.getCharacter();
		}
		
		@Override
		protected PlanTree getPlanTree() {
			return rest.getPlanTree().prepend(edge.label);
		}
		
		@Override
		public Node getEnd() {
			return rest.getEnd();
		}
		
		@Override
		public int size() {
			return 1 + rest.size();
		}
		
		@Override
		public TemporalEdge get(int index) {
			if(index < 0 || index >= size())
				throw Exceptions.indexOutOfBounds(index, size());
			else if(index == 0)
				return edge;
			else
				return rest.get(index - 1);
		}
	}
	
	/**
	 * Creates a new empty explanation tree.
	 */
	public ExplanationTree() {
		// default constructor
	}
	
	@Override
	public String toString() {
		String string = "from node " + getStart().getID();
		if(getCharacter() != null)
			string += " for " + getCharacter();
		if(getPlanTree().size() > 0)
			string += ": " + getPlanTree();
		return string;
	}
	
	@Override
	public int compareTo(ExplanationTree other) {
		int comparison = this.getCharacter().compareTo(other.getCharacter());
		if(comparison == 0)
			comparison = this.getPlanTree().compareTo(other.getPlanTree());
		return comparison;
	}
	
	/**
	 * Returns the {@link Node node} in which the explanation starts.
	 * 
	 * @return the node where the explanation starts
	 */
	public abstract Node getStart();
	
	/**
	 * Returns the {@link Character character} who forms this explanation's
	 * plan, or null if this is an explanation for the author.
	 * 
	 * @return the character or null for the author
	 */
	public abstract Character getCharacter();
	
	/**
	 * Returns the {@link PlanTree plan tree} representing the sequence of
	 * actions that correspond to this explanation's sequence of temporal edges.
	 * 
	 * @return the explanation's plan tree
	 */
	protected abstract PlanTree getPlanTree();
	
	/**
	 * Returns the {@link Plan plan} object represented by the sequence of
	 * actions that correspond to this explanation's sequence of temporal edges.
	 * This plan object will be unique to the story graph in which this
	 * explanation is created--that is, different explanations with the same
	 * plan will return the same plan object.
	 * 
	 * @return the explanation's plan
	 */
	public Plan getPlan() {
		return getPlanTree().toPlan();
	}
	
	/**
	 * Returns the {@link Node node} at which the explanation ends.
	 * 
	 * @return the node where the explanation ends
	 */
	public abstract Node getEnd();
	
	/**
	 * Returns the number of temporal edges in this explanation.
	 * 
	 * @return the number of temporal edges
	 */
	public abstract int size();
	
	/**
	 * Returns the temporal edge in this explanation at the given index.
	 * 
	 * @param index the index of the desired temporal edge
	 * @return the temporal edge at that index
	 * @throws IndexOutOfBoundsException if the index is negative or larger than
	 * the index of the last temporal edge in the explanation
	 */
	public abstract TemporalEdge get(int index);
	
	/**
	 * Returns a new explanation formed by prepending the given temporal edge
	 * to the start of this explanation.
	 * 
	 * @param edge the temporal edge that will be the first in the new
	 * explanation
	 * @return the new explanation with the given temporal edge as its first
	 * edge
	 */
	public ExplanationTree prepend(TemporalEdge edge) {
		return new Branch(edge, this);
	}
}