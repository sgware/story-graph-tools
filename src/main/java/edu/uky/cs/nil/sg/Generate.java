package edu.uky.cs.nil.sg;

import java.io.File;
import java.util.List;

import edu.uky.cs.nil.sabre.Problem;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.comp.Grounder;
import edu.uky.cs.nil.sabre.comp.Simplifier;
import edu.uky.cs.nil.sabre.io.DefaultParser;
import edu.uky.cs.nil.sabre.io.Parser;

/**
 * A {@link StoryGraphTool tool} that generates a {@link StoryGraph story graph}
 * from a {@link CompiledProblem Sabre narrative planning problem}.
 * <p>
 * This tool is primarily an interface for {@link StoryGraphGenerator}.
 * 
 * @author Stephen G. Ware
 */
public class Generate extends StoryGraphTool {
	
	/**
	 * An option that specifies to where the output story graph will be written,
	 * defaulting to the same source from which it was read, though replacing
	 * the file extension (if any) with ".zip"
	 */
	protected static final Option OUTPUT = new Option("o", "FILE", "output file or directory (default: same as input)") {
		
		@Override
		public String getDefaultValue(ToolArguments arguments) {
			String url = new File(arguments.require(0)).getName();
			if(url.lastIndexOf(".") != -1)
				url = url.substring(0, url.lastIndexOf(".")) + ".zip";
			return url;
		}
	};
	
	/**
	 * The limit on the depth of {@link StoryGraphGenerator#limit temporal
	 * generation}
	 */
	protected static final Option DEPTH = new Option("d", "NUMBER", "temporal depth limit, or " + StoryGraphGenerator.UNLIMITED_DEPTH + " for unlimited (default: " + StoryGraphGenerator.UNLIMITED_DEPTH + ")", Integer.toString(StoryGraphGenerator.UNLIMITED_DEPTH));
	
	/**
	 * The {@link StoryGraph#getTitle() title} to assign the generated story
	 * graph
	 */
	protected static final Option TITLE = new Option("t", "STRING", "the title to assign to the story graph (default: problem name)");
	
	/**
	 * The {@link StoryGraph#getAuthors() authors} to assign the generated story
	 * graph
	 */
	protected static final Option AUTHORS = new Option("a", "STRING", "the authors to assign to the story graph");
	
	/**
	 * The name of a {@link Character character} in the story graph who will be
	 * marked as {@link Character#isPlayer() a player}
	 */
	protected static final Option PLAYER = new Option("p", "STRING", "the name of a character to mark as the player (default: none)");
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new Generate(args).run();
	}
	
	/**
	 * Constructs a new story graph generator.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public Generate(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new story graph generator.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public Generate(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new story graph generator with the default configuration.
	 */
	public Generate() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Generate Story Graph";
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
	public List<Option> getOptions() {
		List<Option> options = super.getOptions();
		options.add(OUTPUT);
		options.add(DEPTH);
		options.add(TITLE);
		options.add(AUTHORS);
		options.add(PLAYER);
		return options;
	}
	
	@Override
	public String getDescription() {
		return "Generates the nodes and edges of a story graph from a Sabre narrative planning problem, optionally stopping at a specified temporal depth. The first argument must be a Sabre problem file.";
	}
	
	@Override
	public void run() {
		if(arguments.size() == 0 || arguments.contains(HELP)) {
			System.out.println(getHelp());
			return;
		}
		try {
			arguments.get(0);
			for(Option option : getOptions())
				arguments.getValue(option);
			arguments.checkUnused();
			System.out.println(getTitle());
			CompiledProblem problem = readProblem();
			System.out.println(toString(problem));
			StoryGraph graph = generate(problem);
			System.out.println(new GraphSnapshot(graph));
			writeStoryGraph(graph);
		}
		catch(Throwable throwable) {
			System.err.println("Error: " + throwable.getMessage());
		}
	}
	
	@Override
	public void run(Status status) throws Exception {
		CompiledProblem problem = readProblem(status);
		StoryGraph graph = generate(problem, status);
		writeStoryGraph(graph, status);
	}
	
	private final CompiledProblem readProblem() throws Exception {
		CompiledProblem[] problem = new CompiledProblem[1];
		Task.run(status -> problem[0] = readProblem(status), new Status(), true);
		return problem[0];
	}
	
	private final CompiledProblem readProblem(Status status) throws Exception {
		File file = new File(arguments.require(0));
		status.setMessage("Parsing file \"" + file + "\"");
		Parser parser = new DefaultParser();
		Problem problem = parser.parse(file, Problem.class);
		status.setMessage("Compiling problem \"" + problem.name + "\"");
		CompiledProblem compiled = Grounder.compile(problem, new edu.uky.cs.nil.sabre.util.Worker.Status());
		compiled = Simplifier.compile(compiled, new edu.uky.cs.nil.sabre.util.Worker.Status());
		status.setMessage("Compiled problem \"" + compiled.name + "\"");
		return compiled;
	}
	
	private final String toString(CompiledProblem problem) {
		String string = "Problem \"" + problem.name + "\":";
		String[][] rows = new String[][] {
			new String[] { "entities", Integer.toString(problem.universe.entities.size()) },
			new String[] { "characters", Integer.toString(problem.universe.characters.size()) },
			new String[] { "fluents", Integer.toString(problem.fluents.size()) },
			new String[] { "actions", Integer.toString(problem.actions.size()) },
			new String[] { "triggers", Integer.toString(problem.triggers.size()) },
		};
		int c1pad = 0;
		int c2pad = 0;
		for(String[] row : rows) {
			row[0] = "  " + row[0] + ":";
			c1pad = Math.max(c1pad, row[0].length());
			c2pad = Math.max(c2pad, row[1].length());
		}
		for(String[] row : rows)
			string += String.format("\n%-" + c1pad + "s %" + c2pad + "s", row[0], row[1]);
		return string;
	}
	
	private final StoryGraph generate(CompiledProblem problem) throws Exception {
		StoryGraph[] graph = new StoryGraph[1];
		Task.run(status -> graph[0] = generate(problem, status), new Status(), true);
		return graph[0];
	}
	
	private StoryGraph generate(CompiledProblem problem, Status status) throws Exception {
		return generate(problem, Utilities.toInteger(arguments.getValue(DEPTH)), status);
	}
	
	private StoryGraph generate(CompiledProblem problem, int limit, Status status) throws Exception {
		StoryGraphGenerator generator = new StoryGraphGenerator(problem, limit, status);
		String title = arguments.getValue(TITLE);
		if(title == null)
			title = problem.name;
		generator.storyGraph.setTitle(title);
		String authors = arguments.getValue(AUTHORS);
		if(authors != null)
			generator.storyGraph.setAuthors(authors);
		String player = arguments.getValue(PLAYER);
		if(player != null)
			generator.storyGraph.characters.setPlayer(generator.storyGraph.characters.require(player), true);
		generator.run(status);
		return generator.storyGraph;
	}
	
	private final void writeStoryGraph(StoryGraph graph) throws Exception {
		Task.run(status -> writeStoryGraph(graph, status), new Status(), true);
	}
	
	private final void writeStoryGraph(StoryGraph graph, Status status) throws Exception {
		File file = new File(arguments.getValue(OUTPUT));
		graph.write(file, status);
	}
}