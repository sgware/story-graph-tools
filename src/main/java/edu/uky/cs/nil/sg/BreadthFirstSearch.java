package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that performs a breadth-first search of a {@link
 * StoryGraph story graph}, starting at node 0, and labeling each {@link Node
 * node} based on the {@link #getOrder(Node) order} in which it was visited by
 * the search.
 * 
 * @author Stephen G. Ware
 */
public class BreadthFirstSearch implements Task {
	
	/** The story graph whose nodes are being searched */
	protected final StoryGraph graph;
	
	/**
	 * Whether the search should visit all disconnected components of the graph
	 * or stop after visiting all nodes accessible from node 0
	 */
	protected final boolean disconnected;
	
	/** The order in which each nodes was visited by the search */
	private final BigArrayList<Long> order;
	
	/**
	 * Nodes waiting to be visited that were discovered via an epistemic edge
	 */
	private final BigQueue<Node> epistemic = new BigQueue<>();
	
	/** Nodes waiting to be visited that were discovered via a temporal edge */
	private final BigQueue<Node> temporal = new BigQueue<>();
	
	/**
	 * The {@link Node#getID() ID number} of the first node that is known to
	 * have been visited by this search
	 */
	private long start = 0;
	
	/** The order number to assign to the next node to be visited */
	private long next = 0;
	
	/**
	 * Constructs a new breadth-first search task for a given story graph.
	 * 
	 * @param graph the story graph whose nodes will be searched
	 * @param disconnected true if the search should visit all nodes, even if
	 * they are not accessible from node 0, or false if the search should stop
	 * after visiting all nodes accessible from node 0
	 */
	public BreadthFirstSearch(StoryGraph graph, boolean disconnected) {
		this.graph = graph;
		this.disconnected = disconnected;
		this.order = new BigArrayList<>(graph.nodes.size());
	}
	
	/**
	 * Constructs a new breadth-first search task for a given story graph that
	 * will visit all nodes, even if they are not accessible from node 0.
	 * 
	 * @param graph the story graph whose nodes will be searched
	 */
	public BreadthFirstSearch(StoryGraph graph) {
		this(graph, true);
	}
	
	@Override
	public void run(Status status) throws Exception {
		status.setMessage("Searching story graph");
		Node start = findStart();
		if(start != null) {
			setOrder(start, Long.MAX_VALUE);
			temporal.push(start);
		}
		int breadth = -1;
		while(temporal.size() > 0) {
			long size = temporal.size();
			status.set("Searching breadth " + (++breadth), size);
			for(long i = 0; i < size; i++) {
				visit(temporal.pop());
				while(epistemic.size() > 0)
					visit(epistemic.pop());
				status.increment();
			}
			if(temporal.size() == 0 && disconnected) {
				start = findStart();
				if(start != null) {
					setOrder(start, Long.MAX_VALUE);
					temporal.push(start);
				}
			}
		}
		status.setMessage("Search complete");
	}
	
	/**
	 * Returns the order the given nodes was visited during the search, or null
	 * if the node was not visited. The first node visited will have order 0,
	 * the second order 1, etc. During search, nodes which have been added to
	 * the queue to be visited later, but not yet visited, may have a value of
	 * {@link Long#MAX_VALUE}; however, no nodes would have this value after
	 * the search is complete.
	 * 
	 * @param node the node whose order number is desired
	 * @return the order the node was visited during the search
	 */
	public Long getOrder(Node node) {
		if(node.getID() < order.size())
			return order.get(node.getID());
		else
			return null;
	}
	
	private boolean setOrder(Node node, long value) {
		Long current = getOrder(node);
		if(current == null || value < current) {
			order.set(node.getID(), value);
			return true;
		}
		else
			return false;
	}
	
	private Node findStart() {
		while(start < graph.nodes.size() && getOrder(graph.nodes.get(start)) != null)
			start++;
		if(start < graph.nodes.size())
			return graph.nodes.get(start);
		else
			return null;
	}
	
	/**
	 * This method is called when a {@link Node node} is visited during the
	 * search. By default, it sets the {@link #getOrder(Node) order number} for
	 * the node visits all of the node's {@link #visit(TemporalEdge) temporal}
	 * and {@link #visit(EpistemicEdge) epistemic} edges. By default, this
	 * method will be called at most once per node.
	 * 
	 * @param node the node to visit
	 */
	protected void visit(Node node) {
		if(setOrder(node, next))
			next++;
		for(Action action : graph.actions) {
			TemporalEdge temporal = node.edges.temporal.out.get(action);
			if(temporal != null)
				visit(temporal);
		}
		for(Character character : graph.characters) {
			EpistemicEdge epistemic = node.edges.epistemic.out.get(character);
			if(epistemic != null)
				visit(epistemic);
		}
	}
	
	/**
	 * This method is called when a {@link TemporalEdge temporal edge} is
	 * visited during the search. By default, if the edge's {@link Edge#head
	 * head} is unvisited, its {@link #getOrder(Node) order number} will be
	 * marked as {@link Long#MAX_VALUE} and the node will be queued to be
	 * visited later. By default, this method will be called at most once per
	 * temporal edge.
	 * 
	 * @param temporal the temporal edge to visit
	 */
	protected void visit(TemporalEdge temporal) {
		if(getOrder(temporal.head) == null) {
			setOrder(temporal.head, Long.MAX_VALUE);
			this.temporal.push(temporal.head);
		}
	}
	
	/**
	 * This method is called when an {@link EpistemicEdge epistemic edge} is
	 * visited during the search. By default, if the edge's {@link Edge#head
	 * head} is unvisited, its {@link #getOrder(Node) order number} will be
	 * marked as {@link Long#MAX_VALUE} and the node will be queued to be
	 * visited later. By default, this method will be called at most once per
	 * epistemic edge.
	 * 
	 * @param epistemic the epistemic edge to visit
	 */
	protected void visit(EpistemicEdge epistemic) {
		if(getOrder(epistemic.head) == null) {
			setOrder(epistemic.head, Long.MAX_VALUE);
			this.epistemic.push(epistemic.head);
		}
	}
}