package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that discovers new {@link Explanation explanations} in a
 * {@link StoryGraph story graph}. Specifically, this task discovers
 * explanations for all characters that meet the following definition of {@link
 * #isValid(Explanation) validity}:
 * <ul>
 * <li>The {@link Explanation#character explanation's character} {@link
 * Node#getBeliefs(Character) believes} the {@link Explanation#getPlan()
 * explanation's plan} can be executed.</li>
 * <li>The explanation's character believes that, after executing the
 * explanation's plan, their {@link Node#getUtility(Character) utility} will be
 * higher.</li>
 * <li>Every action in the explanation's plan that requires the {@link
 * Action#consents(Character) consent} of other characters is {@link
 * TemporalEdge#isExplained() explained} for those characters. There is one
 * exception to this rule. If the first action in the plan requires the consent
 * of the explanation's character, the other characters who take that first
 * action (and only the first action) do not need to consent. Consider, for
 * example, a story where one character (the giver) wants to give a gift to
 * another (the receiver), but the receiver doesn't realize the give wants to
 * give the gift. The receiver will still consent to the given action, even
 * through they don't think the giver will want to take it. However, the
 * receiver will not form multi-action plans around this gift--i.e. the
 * receiver will not walk to the giver expected to be given the gift.</li>
 * <li>The explanation is minimal, meaning it is not possible to leave some of
 * the actions out of the plan and still achieve the same or higher utility via
 * only explained actions.</li>
 * </ul>
 * <p>
 * When considering a character's beliefs, if a node does not defined an {@link
 * EpistemicEdge epistemic edge} for that character, this task treats the node
 * as the character's beliefs (i.e. it acts like there is an epistemic loop edge
 * defined).
 * <p>
 * If the graph already contains explanations, this task will not remove them.
 * It may re-discover the same explanations, but it will not add duplicates of
 * explanations that are already in the graph.
 * 
 * @author Stephen G. Ware
 */
public class Explain implements Task {
	
	/** Represents no limit on the length of explanations plans that will be considered */
	protected static final int UNLIMITED_DEPTH = 0;
	
	/**
	 * Tests whether a candidate explanation is valid according to this task's
	 * {@link Explain definition of validity}.
	 * 
	 * @param explanation the explanation whose validity will be tested
	 * @return true if the explanation is valid, false otherwise
	 */
	public static boolean isValid(Explanation explanation) {
		return isValid(explanation.node, explanation.character, explanation.getPlan());
	}
	
	/**
	 * Tests whether a given plan would be a valid {@link Explanation
	 * explanation} for a given character at a given node according to this
	 * task's {@link Explain definition of validity}.
	 * 
	 * @param node the node from which the plan should be evaluated
	 * @param character the character for whom the plan should be evaluated
	 * @param plan the plan that character is evaluating from the node
	 * @return true if the explanation is valid, false otherwise
	 */
	public static boolean isValid(Node node, Character character, Sequence plan) {
		// Start in the state the character believes to be the case.
		Node start = node;
		if(character != null) {
			start = node.getBeliefs(character);
			if(start == null)
				start = node;
		}
		// Check each action in the plan.
		Node end = start;
		for(int i = 0; i < plan.size(); i++) {
			TemporalEdge edge = end.edges.temporal.out.get(plan.get(i));
			// The action must be possible.
			if(edge == null)
				return false;
			end = edge.head;
			// Only author plans may contain actions with no consenting characters.
			if(edge.label.consenting.size() == 0 && character != null)
				return false;
			// If the character consents to the first action, explanations for
			// other character are not needed for that first action.
			if(i == 0 && character != null && edge.label.consents(character))
				continue;
			// Otherwise, the action must be explained for the other consenting
			// characters who take it.
			else
				for(Character other : edge.label.consenting)
					if(other != character && !edge.isExplained(other))
						return false;
		}
		// The character must believe the plan will improve their utility.
		if(start.getUtility(character) >= end.getUtility(character))
			return false;
		// The plan cannot contain a strict subsequence of explained actions
		// that achieves the same or higher utility.
		return isMinimal(start, character, plan, end.getUtility(character));
	}
	
	private static final boolean isMinimal(Node node, Character character, Sequence plan, double goal) {
		return !findSubsequence(character, node, plan, 0, goal, false);
	}
	
	private static final boolean findSubsequence(Character character, Node current, Sequence plan, int index, double goal, boolean shorter) {
		if(index == plan.size())
			return current.getUtility(character) >= goal && shorter;
		else if(findSubsequence(character, current, plan, index + 1, goal, true))
			return true;
		else {
			TemporalEdge edge = current.edges.temporal.out.get(plan.get(index));
			if(edge == null)
				return false;
			if(index > 0 || (character != null && !plan.get(0).consents(character)))
				for(Character other : edge.label.consenting)
					if(other != character && !edge.isExplained(other))
						return false;
			return findSubsequence(character, edge.head, plan, index + 1, goal, shorter);
		}
	}
	
	/** The graph whose duplicate nodes will be removed */
	public final StoryGraph graph;
	
	/** The maximum explanation plan length that will be considered */
	public final int depth;
	
	/** Used to add new explanations to the graph */
	private final NewExplanationSet explanations;
	
	/**
	 * Constructs a generate explanations task with a limit on the max
	 * explanation plan length.
	 * 
	 * @param graph the story graph where explanations will be generated
	 * @param depth the maximum explanation plan length to consider
	 */
	public Explain(StoryGraph graph, int depth) {
		this.graph = graph;
		this.depth = depth;
		this.explanations = new NewExplanationSet(graph);
	}
	
	/**
	 * Constructs a generate explanations task with no limit on the length of
	 * explanation plans.
	 * 
	 * @param graph the story graph where explanations will be generated
	 */
	public Explain(StoryGraph graph) {
		this(graph, UNLIMITED_DEPTH);
	}
	
	@Override
	public void run(Status status) throws Exception {
		int round = 1;
		boolean repeat;
		do {
			repeat = false;
			status.set("Finding explanations, round " + (round++), graph.nodes.size());
			for(Node node : graph.nodes) {
				if(explain(node))
					repeat = true;
				status.increment();
			}
		} while(repeat);
		status.setMessage("Found " + explanations.size() + " new explanations");	}
	
	private final boolean explain(Node node) {
		boolean result = explain(node, null);
		for(Character character : graph.characters)
			result = explain(node, character) || result;
		return result;
	}
	
	private final boolean explain(Node node, Character character) {
		// Get the character's beliefs.
		Node beliefs = node;
		if(character != null) {
			beliefs = node.getBeliefs(character);
			if(beliefs == null)
				beliefs = node;
		}
		// Consider every action the character believes is possible.
		boolean result = false;
		for(TemporalEdge edge : beliefs.edges.temporal.out) {
			// Consider a 1-action explanation.
			result = explain(node, character, new TailSequence(edge.label)) || result;
			// Consider prepending each action to an existing explanation.
			for(Explanation explanation : edge.head.explanations.get(character))
				if(explanation.size() < depth || depth == UNLIMITED_DEPTH)
					result = explain(node, character, new TailSequence(edge.label, explanation)) || result;
		}
		return result;
	}
	
	private final boolean explain(Node node, Character character, Sequence plan) {
		return isValid(node, character, plan) && explanations.add(node, character, plan);
	}
}