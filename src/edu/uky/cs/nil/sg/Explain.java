package edu.uky.cs.nil.sg;

import java.util.List;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that generates {@link
 * Explanation explanations} that will improve the {@link Node#getUtility()
 * author utility} and that {@link Character characters} {@link
 * Node#getBeliefs(Character) believe} will improve {@link
 * Node#getUtility(Character) their utility}.
 * <p>
 * After running this tool, the graph will contain all author explanations
 * (where the {@link Explanation#character character} is null) for plans that
 * can improve the story's utility.
 * <p>
 * It will also contain all character explanations which these properties:
 * <ul>
 * <li>The {@link Explanation#getPlan() plan} is possible from the node the
 * character {@link Node#getBeliefs(Character) believes} the current state to
 * be (even if the plan is not actually possible).</li>
 * <li>The plan will improve the character's utility.</li>
 * <li>If the plan has more than one action, then for all actions after the
 * first action, the explanation's character will believe those actions are
 * {@link TemporalEdge#isExplained() explained} for all of their consenting
 * characters</li>
 * </ul>
 * <p>
 * All explanations, for the author or characters, are minimal, meaning the
 * explanation does not contain a strict subsequence of actions which also meets
 * the requirements above and achieves the same or better utility.
 * <p>
 * Explanation generation can be limited to only plans up to a {@link #limit set
 * length}.
 * <p>
 * Note that the first action in an explanation's plan may not be explained for
 * all (or even any) of its consenting characters. This models the idea that, in
 * the moment, characters want actions to happen which can lead to improving
 * their utility, but they will not anticipate future action that they don't
 * believe will be taken.
 * <p>
 * For example, consider one character purchasing an item they want from a
 * merchant who wants money. The merchant does not know that the character wants
 * the item, so the merchant does not expect the character to consent to buying
 * it, but the merchant will still be happy to consent to the buy action because
 * they will get the money they want. However, the merchant will not form a
 * multi-step plan (such as traveling to the character to sell the item) if they
 * cannot anticipate that the character will buy it.
 * 
 * @author Stephen G. Ware
 */
public class Explain extends SimpleStoryGraphTool {
	
	/**
	 * A value representing no {@link #limit limit} on the explanation plan
	 * length
	 */
	public static final int UNLIMITED_LENGTH = 0;
	
	/** An option to {@link #limit limit} the explanation plan length */
	protected static final Option LENGTH = new Option("l", "NUMBER", "plan length limit, or " + UNLIMITED_LENGTH + " for unlimited (default: " + UNLIMITED_LENGTH + ")", Integer.toString(UNLIMITED_LENGTH));
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new Explain(args).run();
	}
	
	/**
	 * The maximum length of an {@link Explanation#getPlan() explanation's plan}
	 * that will be considered during generation
	 */
	public final int limit;
	
	/**
	 * Constructs a new explanation generator with a given {@link #limit
	 * explanation plan length limit}.
	 * 
	 * @param arguments the arguments that configure the tool
	 * @param limit the max length of plans that will be considered for
	 * explanations
	 */
	public Explain(ToolArguments arguments, int limit) {
		super(arguments);
		this.limit = limit;
	}
	
	/**
	 * Constructs a new explanation generator.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public Explain(ToolArguments arguments) {
		this(arguments, Integer.parseInt(arguments.getValue(LENGTH)));
	}
	
	/**
	 * Constructs a new explanation generator.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public Explain(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new explanation generator with the default configuration.
	 */
	public Explain() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Find Explanations";
	}
	
	@Override
	public String getVersion() {
		return "0.9";
	}
	
	@Override
	public String getAuthors() {
		return "Stephen G. Ware";
	}
	
	@Override
	public String getDescription() {
		return "Finds explanations to improve author utility and to improve character utility based on what they believe is possible.";
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option> options = super.getOptions();
		options.add(LENGTH);
		return options;
	}
	
	@Override
	protected void run(StoryGraph graph, Status status) throws Exception {
		status.set("Finding explanations of length 1", graph.edges.temporal.size());
		long before = graph.explanations.size();
		PlanTree.Root root = new PlanTree.Root(graph);
		ExplanationPriorityQueue queue = new ExplanationPriorityQueue();		
		for(TemporalEdge edge : graph.edges.temporal) {
			if(edge.tail.getUtility() < edge.head.getUtility())
				queue.push(new ExplanationTree.Root(edge.head, null, root).prepend(edge));
			for(Character character : graph.characters)
				if(edge.tail.getUtility(character) < edge.head.getUtility(character))
					queue.push(new ExplanationTree.Root(edge.head, character, root).prepend(edge));
			status.increment();
		}
		int round = 0;
		boolean loop = true;
		while(loop) {
			loop = false;
			status.set("Finding explanations, round " + (++round), queue.size());
			ExplanationPriorityQueue next = new ExplanationPriorityQueue();
			while(queue.size() > 0) {
				ExplanationTree explanation = queue.pop();
				if(canExtend(explanation)) {
					if(explanation.getCharacter() == null)
						graph.explanations.add(explanation.getStart(), explanation.getPlan());
					else
						for(EpistemicEdge edge : explanation.getStart().edges.epistemic.in)
							if(edge.label == explanation.getCharacter())
								graph.explanations.add(edge.tail, explanation.getCharacter(), explanation.getPlan());
					if(explanation.size() < limit || limit == UNLIMITED_LENGTH) {
						for(TemporalEdge edge : explanation.getStart().edges.temporal.in) {
							if(explanation.getCharacter() == null || edge.label.consenting.size() > 0) {
								ExplanationTree extended = explanation.prepend(edge);
								if(isMinimal(extended)) {
									next.push(extended);
									loop = true;
								}
							}
						}
					}
				}
				else
					next.push(explanation);
				status.increment();
			}
			queue = next;
		}
		status.setMessage("Generated " + (graph.explanations.size() - before) + " explanations");
	}
	
	private static final boolean canExtend(ExplanationTree explanation) {
		if(explanation.size() < 2)
			return true;
		else
			return isExplainedForOthers(explanation.get(1), explanation.getCharacter());
	}
	
	private static final boolean isMinimal(ExplanationTree explanation) {
		return !findSubsequence(explanation, 0, explanation.getStart(), false);
	}
	
	private static final boolean findSubsequence(ExplanationTree explanation, int index, Node current, boolean shorter) {
		if(index == explanation.size())
			return (current.getUtility(explanation.getCharacter()) >= explanation.getEnd().getUtility(explanation.getCharacter()) && shorter);
		else if(findSubsequence(explanation, index + 1, current, true))
			return true;
		else {
			TemporalEdge edge = current.edges.temporal.out.get(explanation.get(index).label);
			return edge != null && (index == 0 || isExplainedForOthers(edge, explanation.getCharacter())) && findSubsequence(explanation, index + 1, edge.head, shorter);
		}
	}
	
	private static final boolean isExplainedForOthers(TemporalEdge edge, Character character) {
		for(Character other : edge.label.consenting)
			if(other != character && !edge.isExplained(other))
				return false;
		return true;
	}
}