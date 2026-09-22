package edu.uky.cs.nil.sg;

import java.util.Comparator;

/**
 * A {@link Task task} that {@link EdgeList#sort(Comparator, Status) sorts} the
 * {@link EdgeList edges} of a {@link StoryGraph story graph} in ascending order
 * based on the {@link Node#getID() ID numbers} of their {@link Edge#tail tail
 * nodes} and then the {@link Symbol#getID() ID numbers} of their {@link
 * Edge#label labels}.
 * 
 * @author Stephen G. Ware
 */
public class SortEdges implements Task {
	
	/** A comparator that orders story graph edges by tail and label */
	public static final Comparator<Edge> TAIL_THEN_LABEL = new Comparator<>() {
		
		@Override
		public int compare(Edge edge1, Edge edge2) {
			int comparison = Long.compare(edge1.tail.getID(), edge2.tail.getID());
			if(comparison == 0)
				comparison = Integer.compare(edge1.label.getID(), edge2.label.getID());
			return comparison;
		}
	};
	
	/** The list of edges to sort */
	protected final EdgeList<?> edges;
	
	/**
	 * Constructs a story graph edge sort task.
	 * 
	 * @param edges the list of edges to sort
	 */
	public SortEdges(EdgeList<?> edges) {
		this.edges = edges;
	}
	
	@Override
	public void run(Status status) throws Exception {
		edges.sort(TAIL_THEN_LABEL, status);
	}
}