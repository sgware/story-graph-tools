package edu.uky.cs.nil.sg;

import java.util.ArrayList;
import java.util.List;

/**
 * A parent class for {@link StoryGraph story graph} tools that provides some
 * basic utilities for parsing {@link ToolArguments command line arguments} and
 * printing {@link #getHelp() help text}.
 * 
 * @author Stephen G. Ware
 */
public abstract class StoryGraphTool implements Task {
	
	/** An option that causes a story graph tool to print its help text */
	protected static final Option HELP = new Option("h", "print this message and terminate");
	
	/** The arguments used to configure this tool */
	protected final ToolArguments arguments;
	
	/**
	 * Constructs a new story graph tool from a list of arguments.
	 * 
	 * @param arguments the argument used to configure this tool
	 */
	public StoryGraphTool(ToolArguments arguments) {
		this.arguments = arguments;
	}
	
	/**
	 * Constructs a new story graph tool from an array of string arguments.
	 * 
	 * @param args the arguments used to configure this tool
	 */
	public StoryGraphTool(String[] args) {
		this(new ToolArguments(args));
	}
	
	/**
	 * Constructs a new story graph tool with no configuration arguments.
	 */
	public StoryGraphTool() {
		this(new String[0]);
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
	
	/**
	 * Returns the name of this tool.
	 * 
	 * @return the tool's name
	 */
	public abstract String getName();
	
	/**
	 * Returns a string that expresses the version number of this tool or null
	 * if there is no meaningful version number.
	 * 
	 * @return a version number as a string or null
	 */
	public String getVersion() {
		return null;
	}
	
	/**
	 * Returns the names of the creators of this tool, or null if the authors
	 * are not known.
	 * 
	 * @return the names of the tool authors
	 */
	public String getAuthors() {
		return null;
	}
	
	/**
	 * Returns a string that includes the {@link #getName() name}, {@link
	 * #getVersion() version} (if any), and {@link #getAuthors() authors} (if
	 * any) of this tool.
	 * 
	 * @return the name, version, and authors of this tool
	 */
	public String getTitle() {
		String string = getName();
		if(getVersion() != null)
			string += " v" + getVersion();
		if(getAuthors() != null)
			string += " by " + getAuthors();
		return string;
	}
	
	/**
	 * Returns a short description of this tool's purpose and use. This
	 * description is used in the {@link #getHelp() automatically generated
	 * help text} for this tool.
	 * 
	 * @return a description of this tool
	 */
	public String getDescription() {
		return null;
	}
	
	/**
	 * Returns a list of {@link Option options} that can be used to configure
	 * this tool. The keys, values, and descriptions of each option are included
	 * in the {@link #getHelp() automatically generated help text} for this
	 * tool.
	 * 
	 * @return a list of available options that can be used to configure this
	 * tool
	 */
	public List<Option> getOptions() {
		List<Option> list = new ArrayList<>();
		list.add(HELP);
		return list;
	}
	
	/**
	 * Returns some automatically generated text that explains how to use this
	 * tool. The text should include the {@link #getName() name}, {@link
	 * #getVersion() version number} (if any), {@link #getAuthors() authors} (if
	 * any), {@link #getDescription() description}, and details on how to use
	 * this tool's {@link #getOptions() configuration options}.
	 * 
	 * @return the help text
	 */
	public String getHelp() {
		String string = getTitle();
		string += " (using Story Graph Library v" + Settings.VERSION_STRING + ")";
		if(getDescription() != null)
			string += "\n" + getDescription();
		List<Option> options = getOptions();
		if(options != null && options.size() > 0) {
			int pad = 0;
			for(Option option : options)
				pad = Math.max(pad, usage(option).length());
			for(Option option : options)
				string += "\n" + String.format("%-" + pad + "s  %s", usage(option), option.description);
		}
		return string;
	}
	
	private static final String usage(Option option) {
		return "-" + option.key + (option.value == null ? "" : " " + option.value);
	}
	
	/**
	 * Compares the {@link Settings#VERSION_STRING version number} of the story
	 * graph library being used by this tool to the {@link
	 * StoryGraph#getVersion() version number} of the library used to create the
	 * given story graph, and if they do not match, prints a warning to the
	 * console. Note that this method does not check the {@link #getVersion()
	 * version number of this tool}, only the version number of the story graph
	 * library used to read, write, and modify the story graph.
	 * 
	 * @param graph the story graph whose version will be checked
	 */
	public static void checkVersion(StoryGraph graph) {
		String version = graph.getVersion();
		if(version == null)
			System.out.println("Warning: The story graph " + (graph.getTitle() == null ? "" : "\"" + graph.getTitle() + "\" ") + " does not specify what version of the story graph library was used to create it.");
		else if(!version.equals(Settings.VERSION_STRING))
			System.out.println("Warning: The story graph " + (graph.getTitle() == null ? "" : "\"" + graph.getTitle() + "\" ") + " was created with version " + version + " of the story graph library, but this tool is using version " + Settings.VERSION_STRING + "; they may not be compatible.");
	}
	
	/**
	 * This method runs this tool as a {@link Task story graph task}.
	 * <p>
	 * This method is likely to be overridden, but by default it:
	 * <ul>
	 * <li>Checks if there are no arguments or if the {@link #HELP help option}
	 * is present, and if so, prints the {@link #getHelp() help text} and
	 * terminates.</li>
	 * <li>Prints the {@link #getTitle() title} of the tool.</li>
	 * <li>Runs this tools as a {@link Task#run(Status) task}.</li>
	 * <li>Catches anything {@link Throwable throwable} and, if one is caught,
	 * prints its {@link Throwable#getMessage() message}.</li>
	 * </ul>
	 */
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
			Task.run(this, new Status(), true);
		}
		catch(Throwable throwable) {
			System.err.println("Error: " + throwable.getMessage());
		}
	}
}