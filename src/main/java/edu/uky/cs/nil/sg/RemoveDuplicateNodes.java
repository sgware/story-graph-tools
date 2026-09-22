package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that replaces duplicate {@link Node nodes} in a {@link
 * StoryGraph story graph}. When two equivalent nodes are found, one is replaced
 * with the other. Two nodes are considered equivalent if:
 * <ul>
 * <li>For every {@link Fluent fluent} the nodes have the same {@link
 * Node#getValue(Fluent) value}.</li>
 * <li>For the {@link Node#getUtility() author} and every {@link
 * Node#getUtility(Character) character}, the nodes have the same utility.</li>
 * <li>For every character, the nodes have the same {@link
 * Node#getBeliefs(Character) beliefs}--that is, their corresponding {@link
 * EpistemicEdge epistemic edges} lead to nodes which are the same or equivalent
 * nodes.</li>
 * </ul>
 * <p>
 * When this task finds a pair of equivalent nodes, one is removed, and all
 * edges pointing to the removed nodes are replaced with edges that point to
 * the equivalent node that was not removed. The result should be that the
 * content of the graph does not change, but memory may be saved.
 * <p>
 * If two equivalent nodes have different temporal edges, their edges will
 * be combined. If two equivalent nodes both have the same temporal edge (i.e.
 * a temporal edge with the same label) but the edges lead to nodes which are
 * not equivalent, neither node is replaced and both nodes will be preserved.
 * <p>
 * When equivalent nodes are replaces, their {@link Node#explanations
 * explanations} are merged.
 * 
 * @author Stephen G. Ware
 */
public class RemoveDuplicateNodes implements Task {
	
	/**
	 * An object used to store the tail, label, and head nodes of an edge that
	 * will be created as part of this task.
	 * 
	 * @author Stephen G. Ware
	 */
	private static class EdgeStub {
		
		/** The tail node of the edge to be created */
		public final Node tail;
		
		/** The label of the edge to be created */
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
	
	/** The graph whose duplicate nodes will be removed */
	public final StoryGraph graph;
	
	/**
	 * Constructs a remove duplicate nodes task for the given story graph.
	 * 
	 * @param graph the story graph whose duplicate nodes will be removed
	 */
	public RemoveDuplicateNodes(StoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void run(Status status) throws Exception {
		status.set("Finding duplicate node candidates", graph.nodes.size());
		long before = graph.nodes.size();
		NodeHashMap<Node> replacements = new NodeHashMap<>(graph);
		for(Node node : graph.nodes) {
			Node replacement = replacements.get(node);
			if(replacement == null)
				replacements.put(node, node);
			status.increment();
		}
		int round = 1;
		boolean repeat;
		do {
			repeat = false;
			status.set("Checking duplicate node temporal edges (round " + (round++) + ")", graph.nodes.size());
			for(Node node : graph.nodes) {
				if(!checkTemporal(node, replacements)) {
					replacements.put(node, node);
					repeat = true;
				}
			}
		} while(repeat);
		status.set("Copying explanations", graph.nodes.size());
		ExplanationSet explanations = new ExplanationSet(graph);
		for(Node node : graph.nodes) {
			Node replacement = replacements.get(node);
			if(node != replacement)
				for(Explanation explanation : node.explanations)
					explanations.add(replacement, explanation.character, explanation.getPlan());
		}
		status.set("Checking edges for replacement", graph.edges.size());
		BigArrayList<EdgeStub> stubs = new BigArrayList<>();
		for(Edge edge : graph.edges) {
			Node tail = replacements.get(edge.tail);
			Node head = replacements.get(edge.head);
			if(!graph.edges.contains(tail, edge.label, head))
				stubs.add(new EdgeStub(tail, edge.label, head));
			status.increment();
		}
		graph.nodes.remove(node -> node != replacements.get(node));
		status.set("Creating new edges", stubs.size());
		for(EdgeStub stub : stubs) {
			if(stub.label instanceof Action)
				graph.edges.temporal.add(stub.tail, (Action) stub.label, stub.head);
			else
				graph.edges.epistemic.add(stub.tail, (Character) stub.label, stub.head);
		}
		status.setMessage("Replaced " + (before - graph.nodes.size()) + " duplicate nodes");
	}
	
	private static final boolean checkTemporal(Node node, NodeHashMap<Node> replacements) {
		Node replacement = replacements.get(node);
		if(node != replacement) {
			for(TemporalEdge edge : node.edges.temporal.out) {
				TemporalEdge equivalent = replacement.edges.temporal.out.get(edge.label);
				if(equivalent != null && replacements.get(edge.head) != replacements.get(equivalent.head))
					return false;
			}
		}
		return true;			
	}
}