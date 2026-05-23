package edu.uky.cs.nil.sg;

import java.util.ArrayList;

/**
 * A {@link BigHashMap hash map} that uses {@link Node nodes} as keys, where two
 * nodes are considered the same if their {@link Node#getState() states} are the
 * same and, for each {@link Character character}, the {@link
 * Node#getBeliefs(Character) beliefs of that character} lead to nodes which are
 * the same.
 * 
 * @param <V> the type of element associated with the nodes
 * @author Stephen G. Ware
 */
public class NodeHashMap<V> extends BigHashMap<Node, V> {
	
	/**
	 * A list for tracking unordered pairs of nodes.
	 * 
	 * @author Stephen G. Ware
	 */
	private static class PairList {
		
		/** The list in which node pairs are stored */
		private final ArrayList<Node> list = new ArrayList<>();
		
		/**
		 * Checks whether a pair of nodes appears in this list in any order.
		 * 
		 * @param n1 one of the nodes in the pair
		 * @param n2 the other node in the pair
		 * @return true if this pair of nodes appears in the list in any order
		 */
		public boolean contains(Node n1, Node n2) {
			for(int i = 0; i < list.size(); i += 2)
				if((list.get(i) == n1 && list.get(i + 1) == n2) || (list.get(i) == n2 && list.get(i + 1) == n1))
					return true;
			return false;
		}
		
		/**
		 * Adds a pair of nodes to the list.
		 * 
		 * @param n1 one of the nodes in the pair
		 * @param n2 the other node in the pair
		 */
		public void add(Node n1, Node n2) {
			list.add(n1);
			list.add(n2);
		}
		
		/**
		 * Removes all node pairs from the list.
		 */
		public void clear() {
			list.clear();
		}
	}
	
	/** The story graph in which the node keys were created */
	protected final StoryGraph graph;
	
	/**
	 * A list of unordered node pairs used when checking if nodes are equal
	 */
	private final PairList pairs = new PairList();
	
	/**
	 * Constructs a new node hash map from a given story graph.
	 * 
	 * @param graph the story graph in which the node keys were created
	 */
	public NodeHashMap(StoryGraph graph) {
		super(graph.nodes.size() / 2);
		this.graph = graph;
	}
	
	@Override
	public boolean equals(Object object, Node n2) {
		if(object instanceof Node n1) {
			pairs.clear();
			return same(n1, n2);
		}
		else
			return false;
	}
	
	private final boolean same(Node n1, Node n2) {
		if(n1 == n2)
			return true;
		else if(n1 == null || n2 == null)
			return false;
		else if(n1.getState() != n2.getState())
			return false;
		else if(pairs.contains(n1, n2))
			return true;
		else {
			pairs.add(n1, n2);
			for(Character character : graph.characters)
				if(!same(n1.getBeliefs(character), n2.getBeliefs(character)))
					return false;
			return true;
		}
	}
	
	@Override
	public long hashCode(Object object) {
		long code = 0;
		if(object instanceof Node node)
			code = hashCode(node, 2);
		return code;
	}
	
	private final long hashCode(Node node, int depth) {
		if(node == null)
			return 0;
		long code = node.getState().getID();
		if(depth > 0)
			for(Character character : graph.characters)
				code = code * 31 + hashCode(node.getBeliefs(character), depth - 1);
		return code;
	}
}