package edu.uky.cs.nil.sg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A parent class for a {@link StoryGraphTool story graph tool} that reads a
 * story graph from file, performs an operation on it, and writes the resulting
 * graph to file.
 * 
 * @author Stephen G. Ware
 */
public abstract class SimpleStoryGraphTool extends StoryGraphTool {
	
	/** An argument specifying the input story graph file or directory */
	protected static final Argument<File> INPUT = new Argument.Index<>(0, Argument.FILE, "the story graph");
	
	/** An argument specifying the output story graph file or directory */
	protected static final Argument<File> OUTPUT = new Argument.Key<>("out", Argument.FILE, "output file (default: same as input)");
	
	/**
	 * Constructs a simple story graph tool.
	 */
	public SimpleStoryGraphTool() {
		// default constructor
	}
	
	@Override
	protected List<Argument<?>> getRequiredArguments() {
		List<Argument<?>> arguments = new ArrayList<>();
		arguments.add(INPUT);
		for(Argument<?> other : super.getRequiredArguments())
			arguments.add(other);
		return arguments;
	}
	
	@Override
	protected List<Argument<?>> getOptionalArguments() {
		List<Argument<?>> arguments = super.getOptionalArguments();
		arguments.add(OUTPUT);
		return arguments;
	}
	
	/**
	 * Configures this tool according to its {@link Arguments arguments}, reads
	 * a story graph from file, {@link #run(Arguments, StoryGraph, Status)
	 * performs an operation} on the graph, writes the result to file, and
	 * prints a summary of how the story graph {@link StoryGraphChange changed}.
	 * 
	 * @param arguments the argument that configure this tool's behavior
	 * @throws Exception if a problem occurs while configuring the tool,
	 * performing this tool's operation, or reading and writing the graph
	 */
	public void run(Arguments arguments) throws Exception {
		if(arguments.size() == 0 || arguments.get(HELP)) {
			System.out.println(getDocumentation());
			return;
		}
		for(Argument<?> required : getRequiredArguments())
			arguments.require(required);
		for(Argument<?> optional : getOptionalArguments())
			arguments.get(optional);
		arguments.checkUnused();
		final File input = arguments.require(INPUT);
		final File output;
		if(arguments.get(OUTPUT) == null)
			output = input;
		else
			output = arguments.get(OUTPUT);
		StoryGraphSummary[] summaries = new StoryGraphSummary[2];
		Task.run(status -> {
			StoryGraph graph = StoryGraph.from(input, status);
			String version = graph.meta.getString(MetaData.VERSION);
			if(!version.equals(Settings.VERSION_STRING))
				System.err.println("\rWarning: This tool uses version " + Settings.VERSION_STRING + " of the story graph library, but the graph was created with version " + version + ".");
			summaries[0] = new StoryGraphSummary(graph);
			graph = run(arguments, graph, status);
			String message = status.getMessage();
			summaries[1] = new StoryGraphSummary(graph);
			graph.write(output, status);
			status.setMessage(message);
		});
		StoryGraphChange change = new StoryGraphChange(summaries[0], summaries[1]);
		System.out.println(change);
	}
	
	/**
	 * Performs this tool's operation on the given story graph as defined by the
	 * given arguments. Before this method is called, the story graph will have
	 * been read from file. The story graph returned can be either a new graph
	 * or the same graph given as input.
	 * 
	 * @param arguments the arguments that configure this tool's behavior
	 * @param graph the story graph on which to perform the operation
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @return the story graph that results from the operation (which may be the
	 * same graph given as input)
	 * @throws Exception if a problem occurs while the operation is running
	 */
	protected abstract StoryGraph run(Arguments arguments, StoryGraph graph, Status status) throws Exception;
}
