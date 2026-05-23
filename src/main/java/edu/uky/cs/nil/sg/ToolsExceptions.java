package edu.uky.cs.nil.sg;

import java.util.NoSuchElementException;

/**
 * Static methods for creating story graph tool exceptions.
 * 
 * @author Stephen G. Ware
 */
class ToolsExceptions {
	
	/**
	 * Creates a {@link NoSuchElementException} when an element is removed from
	 * an empty {@link BigQueue queue}.
	 * 
	 * @return a {@link NoSuchElementException}
	 */
	public static NoSuchElementException queueEmpty() {
		return new NoSuchElementException("The queue is empty.");
	}
	
	/**
	 * Creates a {@link RuntimeException} when {@link ToolArguments a set
	 * of command line arguments} does not contain an argument at a specific
	 * index.
	 * 
	 * @param index the index of the argument which is missing
	 * @return a RuntimeException
	 */
	public static RuntimeException argumentIndexRequired(int index) {
		return new RuntimeException("Argument " + (index + 1) + " is required.");
	}
	
	/**
	 * Creates a {@link RuntimeException} when {@link ToolArguments a set
	 * of command line arguments} does not contain an argument with a specific
	 * name.
	 * 
	 * @param name the name of the argument which is missing
	 * @return a RuntimeException
	 */
	public static RuntimeException argumentNameRequired(String name) {
		return new RuntimeException("Argument " + name + " is required.");
	}
	
	/**
	 * Creates a {@link RuntimeException} when {@link ToolArguments a set
	 * of command line arguments} does not contain a value for a given key.
	 * 
	 * @param key the key after which a value was expected
	 * @return a RuntimeException
	 */
	public static RuntimeException argumentValueRequired(String key) {
		return new RuntimeException("A value for the key " + key + " is required.");
	}
	
	/**
	 * Creates a {@link RuntimeException} when a command line argument is not
	 * used by a program.
	 * 
	 * @param argument the unused argument
	 * @return a RuntimeException
	 */
	public static RuntimeException unusedArgument(String argument) {
		return new RuntimeException("The argument \"" + argument + "\" is not recognized or not used in this context.");
	}
	
	/**
	 * Creates an {@link IllegalStateException} when a story graph should have
	 * at least one node but does not.
	 * 
	 * @param graph the story graph with no nodes
	 * @return an IllegalStateException
	 */
	public static IllegalStateException emptyStoryGraph(StoryGraph graph) {
		return new IllegalStateException("The story graph" + (graph.getTitle() == null ? "" : " \"" + graph.getTitle() + "\"") + " has no nodes.");
	}
	
	/**
	 * Creates an {@link IllegalArgumentException} when a {@link
	 * StoryGraphExplorer story graph explorer} cannot parse user input.
	 * 
	 * @param option the option which cannot be parsed
	 * @return an IllegalArgumentException
	 */
	public static IllegalArgumentException cannotParseOption(Object option) {
		return new IllegalArgumentException("The option \"" + option + "\" could not be parsed.");
	}
}