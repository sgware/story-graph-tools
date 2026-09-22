package edu.uky.cs.nil.sg;

import java.util.ArrayList;
import java.util.List;

/**
 * A parent class for {@link StoryGraph story graph} tools which provides
 * meta-data and methods for automatically generating {@link
 * #getDocumentation() documentation}.
 * 
 * @author Stephen G. Ware
 */
public abstract class StoryGraphTool {
	
	/** An argument that causes the tool to display its documentation */
	protected static final Argument.Flag HELP = new Argument.Flag("help", "display documentation and terminate");
	
	/**
	 * Constructs a new story graph tool.
	 */
	public StoryGraphTool() {
		// default constructor
	}
	
	/**
	 * Returns the name of this tool.
	 * 
	 * @return the name of this tool
	 */
	public abstract String getName();
	
	/**
	 * Returns the version number of this tool as a string. It is recommended
	 * that tools use three part semantic versioning.
	 * 
	 * @return the version number of this tool
	 */
	public abstract String getVersion();
	
	/**
	 * Returns a list of people who made this tool.
	 * 
	 * @return the names of this tool's authors
	 */
	public abstract String getAuthors();
	
	/**
	 * Returns the {@link #getName() name}, {@link #getVersion() version}, and
	 * {@link #getAuthors() authors} of this tool as a string.
	 * 
	 * @return the name, version, and authors of this tool
	 */
	public String getTitle() {
		return getName() + " v" + getVersion() + " by " + getAuthors();
	}
	
	/**
	 * Returns a short description of what this tool does.
	 * 
	 * @return a description of this tool
	 */
	public abstract String getDescription();
	
	/**
	 * Returns a list of {@link Argument arguments} that must be provided
	 * to configure this tool.
	 * 
	 * @return a list of required arguments
	 */
	protected List<Argument<?>> getRequiredArguments() {
		return new ArrayList<>();
	}
	
	/**
	 * Returns a list of optional {@link Argument arguments} that may be
	 * provided to configure this tool.
	 * 
	 * @return a list of optional arguments
	 */
	protected List<Argument<?>> getOptionalArguments() {
		List<Argument<?>> arguments = new ArrayList<>();
		arguments.add(HELP);
		return arguments;
	}
	
	/**
	 * Returns a short string demonstrating how to call this tool from the
	 * terminal.
	 * 
	 * @return a short string demonstrating how to call this tool
	 */
	public String getUsage() {
		String usage = "java ";
		String command = System.getProperty("sun.java.command").split(" ")[0];
		if(command.toLowerCase().endsWith(".jar"))
			usage += "-jar ";
		usage += command;
		for(Argument<?> argument : getRequiredArguments())
			usage += " " + argument.usage;
		if(getOptionalArguments().size() > 0)
			usage += " [OPTIONS]";
		return usage;
	}
	
	/**
	 * Returns documentation for this tool which can be written to the terminal.
	 * The documentation includes the too's meta-data (such as its name,
	 * version, and authors) as well as a list of arguments and how to use them.
	 * 
	 * @return documentation for this tool which can be written to the terminal
	 */
	public String getDocumentation() {
		String doc = getTitle();
		doc += "\n(using Story Graph library v" + Settings.VERSION_STRING + ")";
		doc += "\n\nDescription:\n  " + getDescription();
		doc += "\nUsage:\n  " + getUsage();
		List<Argument<?>> required = getRequiredArguments();
		List<Argument<?>> optional = getOptionalArguments();
		int ulength = 0;
		for(Argument<?> argument : required)
			ulength = Math.max(ulength, argument.usage.length());
		for(Argument<?> argument : optional)
			ulength = Math.max(ulength, argument.usage.length());
		if(required.size() > 0) {
			doc += "\nArguments:";
			for(Argument<?> argument : required)
				doc += String.format("\n  %-" + ulength + "s  %s", argument.usage, argument.description);
		}
		if(optional.size() > 0) {
			doc += "\nOptions:";
			for(Argument<?> argument : optional)
				doc += String.format("\n  %-" + ulength + "s  %s", argument.usage, argument.description);
		}
		return doc;
	}
}