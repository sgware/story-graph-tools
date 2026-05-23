package edu.uky.cs.nil.sg;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A tool for tracking, describing, and changing the current {@link Node node}
 * in a {@link StoryGraph story graph}.
 * 
 * @author Stephen G. Ware
 */
public class StoryGraphExplorer {
	
	/** The command to transition to a specific node */
	public static final String NODE = "node";
	
	/** The command to return to the previous node */
	public static final String BACK = "back";
	
	/** The story graph being explored */
	protected final StoryGraph graph;
	
	/** A stack of the previous nodes visited */
	private final Stack<Node> history = new Stack<>();
	
	/** A list of the current options the user can choose from */
	private List<Object> options = new ArrayList<>();
	
	/**
	 * Constructs a new story graph explorer that begins at node 0 of a given
	 * story graph.
	 * 
	 * @param graph the story graph to explore
	 */
	public StoryGraphExplorer(StoryGraph graph) {
		if(graph.nodes.size() == 0)
			throw ToolsExceptions.emptyStoryGraph(graph);
		this.graph = graph;
		setCurrentNode(graph.nodes.get(0));
	}
	
	@Override
	public String toString() {
		return "[Story Graph Explorer: node " + getCurrentNode().getID() + "]";
	}
	
	/**
	 * Returns a string that describes the {@link #getCurrentNode() current
	 * node} in the story graph and the options the user can choose from for
	 * visiting the next node.
	 * 
	 * @return a string describing the current node and current options
	 */
	public String describe() {
		Node current = getCurrentNode();
		String string = "= Node " + current.getID() + " =";
		if(current.getComment() != null)
			string += "\n" + current.getComment();
		string += "\nFluents:";
		for(Fluent fluent : graph.fluents)
			string += "\n  " + fluent + " = " + current.getValue(fluent);
		string += "\nUtilities:\n  utility() = " + current.getUtility();
		for(Character character : graph.characters)
			string += "\n  utility(" + character + ") = " + current.getUtility(character);
		string += "\nOptions:";
		for(int i = 0; i < options.size(); i++)
			string += "\n[" + (i + 1) + "] " + describe(options.get(i));
		return string;
	}
	
	private String describe(Object option) {
		if(option instanceof EpistemicEdge epistemic)
			return describe(epistemic);
		else if(option instanceof TemporalEdge temporal)
			return describe(temporal);
		else if(option == BACK) {
			String string = BACK;
			if(history.size() > 1) {
				Node current = history.pop();
				string += " (to node " + history.peek().getID() + ")";
				history.push(current);
			}
			return string;
		}
		else
			return option.toString();
	}
	
	private String describe(EpistemicEdge edge) {
		String string = edge.label + " beliefs (to node " + edge.head.getID() + ")";
		if(edge.getComment() != null)
			string += "\n      " + edge.getComment();
		return string;
	}
	
	private String describe(TemporalEdge edge) {
		String string = edge.label + " (to node " + edge.head.getID() + ")";
		for(Explanation explanation : edge.explanations) {
			string += "\n      explained for " + (explanation.character == null ? "author" : explanation.character);
			if(explanation.size() > 1) {
				string += ":";
				for(int i = 1; i < explanation.size(); i++)
					string += " " + explanation.get(i);
			}
			if(explanation.getComment() != null)
				string += "\n        " + explanation.getComment();
		}
		return string;
	}
	
	/**
	 * Returns the current node being explored.
	 * 
	 * @return the current node
	 */
	public Node getCurrentNode() {
		return history.peek();
	}
	
	/**
	 * Sets the current node being explored.
	 * 
	 * @param node the new current node
	 */
	public void setCurrentNode(Node node) {
		history.push(node);
		options.clear();
		for(EpistemicEdge edge : node.edges.epistemic.out)
			options.add(edge);
		for(TemporalEdge edge : node.edges.temporal.out)
			options.add(edge);
		if(history.size() > 1)
			options.add(BACK);
	}
	
	/**
	 * Attempts to parse several kinds of objects and to change the {@link
	 * #getCurrentNode() current node} accordingly.
	 * <p>
	 * Objects are parsed as follows:
	 * <ul>
	 * <li>If the object is a {@link Node node}, it {@link #setCurrentNode(Node)
	 * becomes the new current node}.</li>
	 * <li>If the object is an {@link Edge edge}, its {@link Edge#head head}
	 * becomes the new current node.</li>
	 * <li>If the object is an {@link Integer} or a string that can be parsed as
	 * an {@link Integer}, the corresponding option from the {@link #describe()
	 * current description} will be chosen, where the first option is option 1,
	 * the second is option 2, etc.</li>
	 * <li>If the object is a string starting with {@link #NODE}, followed by a
	 * space, followed by a string which can be parsed as a {@link Long}, or if
	 * the object is a {@link Long}, the current node will be set to the node
	 * with that {@link Node#getID() ID number}.</li>
	 * <li>If the object is the string {@link #BACK}, the previous visited node
	 * will be set as the current node. If there is no previous node, no change
	 * will happen.</li>
	 * </ul>
	 * 
	 * @param option the object to parse
	 * @throws IllegalArgumentException if the object cannot be parsed according
	 * to the criteria above
	 */
	public void choose(Object option) {
		if(option instanceof Node node)
			setCurrentNode(node);
		else if(option instanceof Edge edge)
			setCurrentNode(edge.head);
		else if(option instanceof Integer choice) {
			if(choice <= options.size())
				choose(options.get(choice - 1));
			else
				throw ToolsExceptions.cannotParseOption(option);
		}
		else if(option instanceof Long node)
			setCurrentNode(graph.nodes.get(node));
		else if(option instanceof String string) {
			if(string.toLowerCase().startsWith((NODE + " ").toLowerCase())) {
				string = string.substring(NODE.length() + 1);
				try {
					choose(Long.parseLong(string));
				}
				catch(NumberFormatException exception) {
					throw ToolsExceptions.cannotParseOption(option);
				}
			}
			else if(string.equalsIgnoreCase(BACK)) {
				if(history.size() > 1)
					history.pop();
				Node node = history.pop();
				setCurrentNode(node);
			}
			else {
				try {
					choose(Integer.parseInt(string));
				}
				catch(NumberFormatException exception) {
					throw ToolsExceptions.cannotParseOption(option);
				}
			}
		}
		else
			throw ToolsExceptions.cannotParseOption(option);
	}
}