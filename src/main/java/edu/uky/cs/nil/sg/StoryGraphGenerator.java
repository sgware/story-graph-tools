package edu.uky.cs.nil.sg;

import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.graph.StateGraph;
import edu.uky.cs.nil.sabre.graph.StateNode;

/**
 * A tool for creating a {@link StoryGraph story graph} from a {@link
 * CompiledProblem compiled Sabre problem}. The story graph will have all the
 * characters, fluents, and actions defined by the Sabre problem. Node 0 will
 * be the problem's initial state. The graph will be generated using a complete
 * breadth-first expansion (optionally {@link #limit limited} to a certain
 * depth). For every node, a {@link TemporalEdge temporal edge} will be created
 * for every action whose precondition is satisfied in that node's state (unless
 * taking the action would exceed the {@link #limit depth limit}). Every node
 * which can be reached via any number of {@link EpistemicEdge epistemic edges}
 * will be generated.
 * 
 * @author Stephen G. Ware
 */
public class StoryGraphGenerator implements Task {
	
	/** A value representing no {@link #limit limit} on the generation depth */
	public static final int UNLIMITED_DEPTH = 0;
	
	/** The Sabre problem for which the story graph will be generated */
	public final CompiledProblem problem;
	
	/** The Sabre state graph corresponding to the story graph */
	public final StateGraph stateGraph;
	
	/** The story graph being generated */
	public final StoryGraph storyGraph;
	
	/**
	 * The limit on the depth of temporal generation; a limit of 3 means that
	 * all temporal paths of up to 3 actions will be generated; a limit of 4
	 * means that all temporal paths of up to 4 actions will be generated, etc.
	 */
	public final int limit;
	
	/**
	 * Maps {@link StateNode state graph nodes} (via their {@link StateNode#id
	 * ID numbers} to {@link Node story graph nodes}
	 */
	private final BigArrayList<Node> nodes = new BigArrayList<>();
	
	/**
	 * Maps {@link StateNode state graph nodes} to their corresponding {@link
	 * State story graph state objects}
	 */
	private final StateMapping states;
	
	/** The queue of state nodes waiting to be expanded */
	private final BigQueue<StateNode> queue = new BigQueue<>();
	
	/**
	 * Constructs a new story graph generator for a given Sabre problem and with
	 * a given depth limit. This constructor will also create all of the {@link
	 * Character character}, {@link Fluent fluent}, and {@link Action action}
	 * symbols in the story graph.
	 * 
	 * @param problem the Sabre problem used to generate the story graph
	 * @param limit the temporal limit on the generation
	 * @param status a status object that will be updated while this constructor
	 * runs to reflect its current progress
	 */
	public StoryGraphGenerator(CompiledProblem problem, int limit, Status status) {
		status.setMessage("Creating character and fluent symbols");
		this.problem = problem;
		this.stateGraph = new StateGraph(problem);
		this.storyGraph = new StoryGraph();
		this.limit = limit;
		this.states = new StateMapping(stateGraph, storyGraph);
		status.setMessage("Creating action tree");
		problem.actions.buildTree(new edu.uky.cs.nil.sabre.util.Worker.Status());
		status.set("Creating action symbols", (long) problem.actions.size());
		for(edu.uky.cs.nil.sabre.Action problemAction : problem.actions) {
			Action storyAction = storyGraph.actions.add(problemAction.toString());
			for(edu.uky.cs.nil.sabre.logic.Parameter consenting : problemAction.consenting)
				storyGraph.actions.add(storyAction, storyGraph.characters.get(consenting.toString()));
		}
	}
	
	@Override
	public void run(Status status) throws Exception {
		int depth = 0;
		status.setMessage("Generating story graph, depth " + depth);
		push(stateGraph.root);
		while(queue.size() > 0) {
			long size = queue.size();
			status.set("Generating story graph, depth " + (++depth), size);
			for(long i = 0; i < size; i++) {
				StateNode stateNode = queue.pop();
				Node tail = getNode(stateNode);
				for(edu.uky.cs.nil.sabre.Action action : problem.actions.getEvery(stateNode)) {
					StateNode after = stateNode.getAfter(action).getAfterTriggers();
					Node head = getNode(after);
					if(head == null && (depth < limit || limit == UNLIMITED_DEPTH)) {
						push(after);
						head = addNode(after);
					}
					if(head != null)
						storyGraph.edges.temporal.add(tail, storyGraph.actions.require(action.toString()), head);
				}
				status.increment();
			}
		}
	}
	
	private final void push(StateNode stateNode) {
		Node storyNode = getNode(stateNode);
		if(storyNode != null)
			return;
		BigQueue<StateNode> temporary = new BigQueue<>();
		temporary.push(stateNode);
		while(temporary.size() > 0) {
			stateNode = temporary.pop();
			queue.push(stateNode);
			Node tail = addNode(stateNode);
			for(edu.uky.cs.nil.sabre.Character character : stateNode.graph.characters) {
				StateNode beliefs = stateNode.getBeliefs(character);
				Node head = getNode(beliefs);
				if(head == null) {
					temporary.push(beliefs);
					head = addNode(beliefs);
				}
				storyGraph.edges.epistemic.add(tail, storyGraph.characters.require(character.toString()), head);
			}
		}
	}
	
	private final Node getNode(StateNode stateNode) {
		if(stateNode.id < nodes.size())
			return nodes.get(stateNode.id);
		else
			return null;
	}
	
	private final Node addNode(StateNode stateNode) {
		Node storyNode = getNode(stateNode);
		if(storyNode == null) {
			storyNode = storyGraph.nodes.add(states.get(stateNode));
			nodes.set(stateNode.id, storyNode);
		}
		return storyNode;
	}
}