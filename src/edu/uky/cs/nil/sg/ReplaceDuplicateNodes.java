package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that replaces duplicate {@link Node nodes} in a {@link
 * StoryGraph story graph}. Two nodes are considered duplicates if:
 * <ul>
 * <li>For every {@link Fluent fluent} the nodes have the same {@link
 * Node#getValue(Fluent) value}.</li>
 * <li>For every {@link Character character} and the author, the nodes have the
 * same {@link Node#getUtility(Character) utility}.</li>
 * <li>The nodes have the same set of outgoing {@link TemporalEdge temporal
 * edges}, and corresponding temporal edges lead to nodes which are the same or
 * duplicates of one another.</li>
 * <li>For every character, the nodes have the same {@link
 * Node#getBeliefs(Character) beliefs}--that is, their corresponding {@link
 * EpistemicEdge epistemic edges} lead to nodes which are the same or duplicates
 * or one another.</li>
 * </ul>
 * <p>
 * If this task removes any nodes, then for any edges that have a removed node
 * as the {@link Edge#head head} a new edge is created that leads to the node
 * the removed nodes was replaced with.
 * 
 * @author Stephen G. Ware
 */
public class ReplaceDuplicateNodes implements Task {
	
	/**
	 * An object used to store the tail, label, and head nodes of an edge that
	 * will be created as part of this task.
	 * 
	 * @author Stephen G. Ware
	 */
	private static class EdgeStub {
		
		/** The tail node of the edge to be created */
		public final Node tail;
		
		/** The labe of the edge to be created */
		public final Object label;
		
		/** The head node of the edge to be created */
		public final Node head;
		
		/**
		 * Constructs a new edge stub from a tail node, label, and head node.
		 * 
		 * @param tail the tail node
		 * @param label the label
		 * @param head the head node
		 */
		public EdgeStub(Node tail, Object label, Node head) {
			this.tail = tail;
			this.label = label;
			this.head = head;
		}
	}
	
	/** The story graph whose duplicate nodes will be replaced */
	protected final StoryGraph graph;
	
	/**
	 * Constructs a new duplicate node replacement task.
	 * 
	 * @param graph the graph whose duplicate nodes will be replaced
	 */
	public ReplaceDuplicateNodes(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		status.set("Finding duplicate node candidates", graph.nodes.size());
		long before = graph.nodes.size();
		NodeHashMap<Node> replacements = new NodeHashMap<>(graph);
		BigNumberedSet<Node> toRemove = new BigNumberedSet<>(graph.nodes);
		for(Node node : graph.nodes) {
			Node replacement = replacements.get(node);
			if(replacement == null)
				replacements.put(node, node);
			else
				toRemove.add(node);
			status.increment();
		}
		status.set("Checking temporal edges of duplicate node candidates", toRemove.size());
		for(Node node : toRemove) {
			if(!checkTemporal(node, replacements))
				toRemove.remove(node);
			status.increment();
		}
		status.set("Creating edge stubs", graph.edges.size());
		BigArrayList<EdgeStub> stubs = new BigArrayList<>();
		for(Edge edge : graph.edges) {
			if(toRemove.contains(edge.tail) || toRemove.contains(edge.head)) {
				Node tail = replacements.get(edge.tail);
				Node head = replacements.get(edge.head);
				if(!graph.edges.contains(tail, edge.label, head))
					stubs.add(new EdgeStub(replacements.get(edge.tail), edge.label, replacements.get(edge.head)));
			}
			status.increment();
		}
		graph.nodes.remove(node -> toRemove.contains(node), status);
		status.set("Creating new edges", stubs.size());
		for(EdgeStub stub : stubs) {
			if(stub.label instanceof Action)
				graph.edges.temporal.add(stub.tail, (Action) stub.label, stub.head);
			else
				graph.edges.epistemic.add(stub.tail, (Character) stub.label, stub.head);
			status.increment();
		}
		status.setMessage("Replaced " + (before - graph.nodes.size()) + " duplicate nodes");
	}
	
	private static final boolean checkTemporal(Node node1, NodeHashMap<Node> replacements) {
		Node node2 = replacements.get(node1);
		if(node1.edges.temporal.out.size() != node2.edges.temporal.out.size())
			return false;
		for(TemporalEdge edge1 : node1.edges.temporal.out) {
			TemporalEdge edge2 = node2.edges.temporal.out.get(edge1.label);
			if(edge2 == null)
				return false;
			Node head1 = edge1.head;
			Node head2 = replacements.get(head1);
			if(head1 != head2)
				return false;
		}
		return true;
	}
}