package edu.uky.cs.nil.sg;

/**
 * A {@link Task task} that removes the {@link TemporalEdge temporal edges} from
 * a {@link StoryGraph story graph} if the action is not {@link
 * TemporalEdge#isExplained(Character) explained} for its {@link
 * Action#consenting consenting characters}.
 * <p>
 * This task can reason separately about {@link Character#isPlayer() player} and
 * non-player characters. By default, this task removes temporal edges that
 * require the consent of at least one non-player character but are not
 * explained for that character. It can also be configured to remove player
 * edges or edges for all characters.
 * 
 * @author Stephen G. Ware
 */
public class RemoveUnexplained implements Task {
	
	/** The graph whose duplicate nodes will be removed */
	public final StoryGraph graph;
	
	/** Whether unexplained non-player character actions should be removed */
	public final boolean npc;
	
	/** Whether unexplained player actions should be removed */
	public final boolean player;
	
	/**
	 * Constructs a remove unexplained actions task.
	 * 
	 * @param graph the story graph whose unexplained actions will be removed
	 * @param npc whether unexplained non-player character actions should be
	 * removed
	 * @param player whether unexplained player actions should be removed
	 */
	public RemoveUnexplained(StoryGraph graph, boolean npc, boolean player) {
		this.graph = graph;
		this.npc = npc;
		this.player = player;
	}
	
	/**
	 * Constructs a remove unexplained actions task that removed only
	 * unexplained actions that at least one non-player character consents to.
	 * 
	 * @param graph the story graph whose unexplained actions will be removed
	 */
	public RemoveUnexplained(StoryGraph graph) {
		this(graph, true, false);
	}
	
	@Override
	public void run(Status status) throws Exception {
		long before = graph.edges.temporal.size();
		graph.edges.temporal.remove(edge -> {
			boolean prune = false;
			for(Character character : edge.label.consenting) {
				if(npc && !character.isPlayer() && !edge.isExplained(character))
					prune = true;
				else if(player && character.isPlayer() && !edge.isExplained(character))
					prune = true;
			}
			return prune;
		});
		status.setMessage("Removed " + (before - graph.edges.temporal.size()) + " unexplained temporal edges");
	}
}