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
 * for every action whose precondition is satisfied in that node's state. Every
 * node which can be reached via any number of {@link EpistemicEdge epistemic
 * edges} will also be generated.
 * 
 * @author Stephen G. Ware
 */
public class StoryGraphGenerator implements Task {
	
	/** A value representing no {@link #limit limit} on the generation depth */
	public static final int UNLIMITED_DEPTH = 0;
	
	/** The Sabre problem for which the story graph will be generated */
	public final CompiledProblem problem;
	
	/** The Sabre state graph corresponding to the story graph */
	public final StateGraph states;
	
	/** The story graph being generated */
	public final StoryGraph story;
	
	/**
	 * The limit on the depth of temporal generation; a limit of 3 means that
	 * all temporal paths of up to 3 actions will be generated; a limit of 4
	 * means that all temporal paths of up to 4 actions will be generated, etc.
	 */
	public final int limit;
	
	/**
	 * The queue of states waiting to be expanded that were reached via an
	 * epistemic edge
	 */
	private final BigQueue<StateNode> temporal = new BigQueue<>();
	
	/**
	 * The queue of states waiting to be expanded that were reached via a
	 * temporal edge
	 */
	private final BigQueue<StateNode> epistemic = new BigQueue<>();
	
	/**
	 * Maps {@link StateNode state graph nodes} (via their {@link StateNode#id
	 * ID numbers} to {@link Node story graph nodes}
	 */
	private final BigArrayList<Node> nodes = new BigArrayList<>();
	
	/**
	 * Maps {@link StateNode state graph nodes} to their corresponding {@link
	 * State story graph states}
	 */
	private final StateMapping map;
	
	/**
	 * Constructs a new story graph generator for a given Sabre problem and with
	 * a given depth limit.
	 * 
	 * @param problem the Sabre problem used to generate the story graph
	 * @param limit the temporal limit on the generation
	 */
	public StoryGraphGenerator(CompiledProblem problem, int limit) {
		this.problem = problem;
		this.states = new StateGraph(problem);
		this.story = new StoryGraph();
		this.limit = limit;
		this.map = new StateMapping(states, story);
		for(edu.uky.cs.nil.sabre.Action action : problem.actions) {
			this.story.actions.add(action.toString());
			for(edu.uky.cs.nil.sabre.logic.Parameter consenting : action.consenting)
				if(consenting instanceof edu.uky.cs.nil.sabre.Character character)
					this.story.actions.add(this.story.actions.require(action.toString()), this.story.characters.require(character.toString()));
		}
	}
	
	@Override
	public void run(Status status) throws Exception {
		add(states.root);
		temporal.push(states.root);
		int depth = -1;
		while(temporal.size() > 0 && (depth < limit || limit == UNLIMITED_DEPTH)) {
			long size = temporal.size();
			status.set("Generating story graph depth " + (++depth), size);
			for(long i = 0; i < size; i++) {
				visit(temporal.pop());
				while(epistemic.size() > 0)
					visit(epistemic.pop());
				status.increment();
			}
		}
		if(limit == UNLIMITED_DEPTH)
			status.setMessage("Full story graph generated");
		else
			status.setMessage("Story graph generated to depth " + limit);
	}
	
	private void visit(StateNode tail) {
		for(edu.uky.cs.nil.sabre.Action action : problem.actions.getEvery(tail)) {
			StateNode head = tail.getAfter(action).getAfterTriggers();
			if(node(head) == null) {
				temporal.push(head);
				add(head);
			}
			story.edges.temporal.add(node(tail), story.actions.get(action.toString()), node(head));
		}
		for(edu.uky.cs.nil.sabre.Character character : problem.universe.characters) {
			StateNode head = tail.getBeliefs(character);
			if(node(head) == null) {
				epistemic.push(head);
				add(head);
			}
			story.edges.epistemic.add(node(tail), story.characters.get(character.toString()), node(head));
		}
	}
	
	private final Node node(StateNode state) {
		if(state.id < nodes.size())
			return nodes.get(state.id);
		else
			return null;
	}
	
	private final Node add(StateNode state) {
		Node node = story.nodes.add(map.get(state));
		nodes.set(state.id, node);
		return node;
	}
}