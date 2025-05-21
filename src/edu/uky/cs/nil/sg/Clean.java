package edu.uky.cs.nil.sg;

/**
 * A {@link SimpleStoryGraphTool story graph tool} that is a combination of four
 * other tools, which do the following tasks in this order: {@link
 * RemoveDisconnected remove disconnected nodes}, {@link RemoveUnused remove
 * unused elements}, {@link RemoveDuplicates remove duplicate elements}, and
 * {@link Sort sort all elements}.
 * 
 * @author Stephen G. Ware
 */
public class Clean extends SimpleStoryGraphTool {
	
	/**
	 * Configures and runs the tool according to its command line arguments.
	 * 
	 * @param args the command line arguments that configure the tool
	 */
	public static void main(String[] args) {
		new Clean(args).run();
	}
	
	/**
	 * Constructs a new story graph cleaning tool.
	 * 
	 * @param arguments the arguments that configure the tool
	 */
	public Clean(ToolArguments arguments) {
		super(arguments);
	}
	
	/**
	 * Constructs a new story graph cleaning tool.
	 * 
	 * @param args the arguments that configure the tool
	 */
	public Clean(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new story graph cleaning tool with the default
	 * configuration.
	 */
	public Clean() {
		this(new String[0]);
	}
	
	@Override
	public String getName() {
		return "Clean Story Graph";
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
		return "Removes disconnected nodes and unused elements. Replaces duplicate elements. Sorts all elements.";
	}
	
	@Override
	protected void run(StoryGraph graph, Status status) throws Exception {
		new RemoveDisconnected().run(graph, status);
		new RemoveUnused().run(graph, status);
		new RemoveDuplicates().run(graph, status);
		new Sort().run(graph, status);
		status.setMessage("Story graph cleaned");
	}
}