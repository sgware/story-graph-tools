package edu.uky.cs.nil.sg;

import java.io.File;
import java.util.Scanner;

/**
 * A {@link StoryGraphTool story graph tool} that that prints a human-readable
 * description of a story graph node to {@link System#out standard output} and
 * accepts directions from {@link System#in standard input} that allow the user
 * to visit other nodes and explore a {@link StoryGraph story graph}.
 * <p>
 * This tool is primarily an interface to a {@link StoryGraphExplorer}.
 * 
 * @author Stephen G. Ware
 */
public class Explore extends StoryGraphTool {
	
	/** The command to stop exploring */
	public static final String QUIT = "quit";
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new Explore(args).run();
	}
	
	/**
	 * Constructs a new story graph exploration tool.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public Explore(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new story graph exploration tool.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public Explore(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new story graph exploration tool with the default
	 * configuration.
	 */
	public Explore() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Explore Story Graph";
	}
	
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String getAuthors() {
		return "Stephen G. Ware";
	}
	
	@Override
	public String getDescription() {
		return "Allows for interactive exploration of story graphs in the console. The first argument must be a story graph file.";
	}
	
	@Override
	public void run(Status status) throws Exception {
		StoryGraph graph = new StoryGraph();
		Task.run(s -> graph.read(new File(arguments.require(0)), s), status, true);
		status.setMessage("Exploring story graph");
		run(graph);
	}
	
	/**
	 * This method repeatedly prints a description of the current {@link Node
	 * node} to {@link System#out standard output}, waits for an instruction to
	 * be given via {@link System#in standard input}, and then parses that
	 * instruction. It runs until the {@link #QUIT} instruction is given.
	 * 
	 * @param graph the story graph to explore
	 */
	public void run(StoryGraph graph) {
		StoryGraphExplorer explorer = new StoryGraphExplorer(graph);
		System.out.println("Type the number of a choice, or type \"" + StoryGraphExplorer.NODE + " N\" to go directly to node N, or type \"" + QUIT + "\" to quit.");
		try(Scanner input = new Scanner(System.in)) {
			boolean run = true;
			while(run) {
				System.out.print(explorer.describe() + "\n> ");
				try {
					String choice = input.nextLine();
					if(choice.equalsIgnoreCase(QUIT))
						return;
					else
						explorer.choose(choice);
				}
				catch(Throwable throwable) {
					System.err.println("Error: " + throwable.getMessage());
				}
			}
		}
	}
}