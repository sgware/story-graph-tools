package edu.uky.cs.nil.sg;

import java.io.File;
import java.util.List;

/**
 * A parent class for {@link StoryGraphTool story graph tools} that reads a
 * {@link StoryGraph story graph} from file, {@link #run(StoryGraph, Status)
 * performs an operation} on that graph, and then write the graph back out to
 * file.
 * 
 * @author Stephen G. Ware
 */
public abstract class SimpleStoryGraphTool extends StoryGraphTool {
	
	/**
	 * An option that specifies to where the output story graph will be written,
	 * defaulting to the same source from which it was read
	 */
	protected static final Option OUTPUT = new Option("o", "FILE", "output file or directory (default: same as input)") {
		
		@Override
		public String getDefaultValue(ToolArguments arguments) {
			return arguments.require(0);
		}
	};
	
	/**
	 * Constructs a new simple story graph tool from a list of arguments.
	 * 
	 * @param arguments the argument used to configure this tool
	 */
	public SimpleStoryGraphTool(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new simple story graph tool from an array of string
	 * arguments.
	 * 
	 * @param args the arguments used to configure this tool
	 */
	public SimpleStoryGraphTool(String[] args) {
		this(new ToolArguments(args));
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option> options = super.getOptions();
		options.add(OUTPUT);
		return options;
	}
	
	/**
	 * This method:
	 * <ul>
	 * <li>Checks if there are no arguments or if the {@link #HELP help option}
	 * is present, and if so, prints the {@link #getHelp() help text} and
	 * terminates.</li>
	 * <li>Checks for {@link ToolArguments#checkUnused() unused arguments}.</li>
	 * <li>Prints the {@link #getTitle() title} of the tool.</li>
	 * <li>{@link StoryGraph#read(GraphReader, Status) Reads} the story graph
	 * from the input source.</li>
	 * <li>{@link #checkVersion(StoryGraph) Checks the version number} of the
	 * story graph.</li>
	 * <li>Prints a {@link GraphSnapshot snapshot} of the graph.</li>
	 * <li>Runs this tool's {@link #run(StoryGraph, Status) main method} as a
	 * {@link Task#run(Status) task}.</li>
	 * <li>Prints a {@link GraphSnapshot snapshot} of the graph after the main
	 * method.</li>
	 * <li>{@link StoryGraph#write(GraphWriter, Status) Writes} the story graph
	 * to the output destination.</li>
	 * <li>Catches anything {@link Throwable throwable} and, if one is caught,
	 * prints its {@link Throwable#getMessage() message}.</li>
	 * </ul>
	 */
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
			StoryGraph graph = new StoryGraph();
			Task.run(status -> readStoryGraph(graph, status), new Status(), true);
			checkVersion(graph);
			GraphSnapshot before = new GraphSnapshot(graph);
			System.out.println(before);
			Task.run(status -> run(graph, status), new Status(), true);
			GraphSnapshot after = new GraphSnapshot(graph);
			System.out.println(GraphSnapshot.toString(before, after));
			Task.run(status -> writeStoryGraph(graph, status), new Status(), true);
		}
		catch(Throwable throwable) {
			System.err.println("Error: " + throwable.getMessage());
		}
	}
	
	/**
	 * This method:
	 * <ul>
	 * <li>{@link StoryGraph#read(GraphReader, Status) Reads} the story graph
	 * from the input source.</li>
	 * <li>Runs this tool's {@link #run(StoryGraph, Status) main method}.</li>
	 * <li>{@link StoryGraph#write(GraphWriter, Status) Writes} the story graph
	 * to the output destination.</li>
	 * </ul>
	 */
	@Override
	public void run(Status status) throws Exception {
		StoryGraph graph = new StoryGraph();
		readStoryGraph(graph, status);
		run(graph, status);
		writeStoryGraph(graph, status);
	}
	
	private void readStoryGraph(StoryGraph graph, Status status) throws Exception {
		graph.read(new File(arguments.require(0)), status);
	}
	
	/**
	 * Performs this tool's operation on the given story graph.
	 * 
	 * @param graph the story graph on which to perform the operation
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws Exception if an exception occurs while this method is running
	 */
	protected abstract void run(StoryGraph graph, Status status) throws Exception;
	
	private void writeStoryGraph(StoryGraph graph, Status status) throws Exception {
		graph.write(new File(arguments.requireValue(OUTPUT)), status);
	}
}