package edu.uky.cs.nil.sg;

import java.util.Arrays;

/**
 * An object for parsing an array of string configuration arguments to a {@link
 * StoryGraphTool story graph tool}, such as the command line arguments passed
 * to a Java main method.
 * 
 * @author Stephen G. Ware
 */
public class ToolArguments {
	
	/** The original array of strings */
	private final String[] args;
	
	/** Tracks which arguments have been used */
	private final boolean[] used;
	
	/**
	 * Constructs a new list of command line arguments from the array of string
	 * passed to a Java main method.
	 * 
	 * @param args an array of strings
	 */
	public ToolArguments(String[] args) {
		this.args = args == null ? new String[0] : args;
		this.used = new boolean[this.args.length];
	}
	
	@Override
	public String toString() {
		return Arrays.toString(args);
	}
	
	/**
	 * Returns the number of arguments in this list.
	 * 
	 * @return the number of arguments
	 */
	public int size() {
		return args.length;
	}
	
	/**
	 * Checks whether a given argument exists in this list of arguments. If the
	 * argument is found, it is marked {@link #checkUnused() used}.
	 * 
	 * @param argument the argument in question
	 * @return true if the argument appears in this list, false otherwise
	 */
	public boolean contains(String argument) {
		return indexOf(argument) != -1;
	}
	
	/**
	 * Checks whether a given {@link Option#key option's key} exists in this
	 * list of arguments. An option's key only exists in this list if it is
	 * preceded by a dash. For example, if the option's key is {@code "o"} then
	 * this method returns true only if {@code "-o"} is one of the arguments in
	 * this list. If the argument is found, it is marked {@link #checkUnused()
	 * used}.
	 * 
	 * @param option the option whose key is in question
	 * @return true if the option's key appears in this list, false otherwise
	 */
	public boolean contains(Option option) {
		return contains("-" + option.key);
	}
	
	/**
	 * Throws an exception if the given argument is not in this list. If the
	 * argument is found, it is marked {@link #checkUnused() used}.
	 * 
	 * @param argument the argument which must appear in this list
	 */
	public void require(String argument) {
		if(!contains(argument))
			throw ToolsExceptions.argumentNameRequired(argument);
	}
	
	/**
	 * Throws an exception if the given {@link Option#key option's key} is not
	 * in this list. An option's key only exists in this list if it is preceded
	 * by a dash. For example, if the option's key is {@code "o"} then this
	 * method returns true only if {@code "-o"} is one of the arguments in this
	 * list. If the argument is found, it is marked {@link #checkUnused() used}.
	 * 
	 * @param option the option whose key must appear in this list
	 */
	public void require(Option option) {
		require("-" + option.key);
	}
	
	/**
	 * Returns the index of the given argument in the original array of strings,
	 * or -1 if the argument does not exist. If the argument does exist, it is
	 * marked as {@link #checkUnused() used}.
	 * 
	 * @param argument the argument whose index is desired
	 * @return the index, or -1 if the argument is not in the list
	 */
	public int indexOf(String argument) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase(argument)) {
				used[i] = true;
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the index of the given {@link Option#key option's key} in the
	 * original array of strings, or -1 if the option's key does not exist. An
	 * option's key only exists in this list if it is preceded by a dash. For
	 * example, if the option's key is {@code "o"} then this method returns the
	 * index of {@code "-o"} in this list. If the argument does exist, it is
	 * marked as {@link #checkUnused() used}.
	 * 
	 * @param option the option whose key index is desired
	 * @return the index, or -1 if the argument is not in the list
	 */
	public int indexOf(Option option) {
		return indexOf("-" + option.key);
	}
	
	/**
	 * Returns the argument at the given index in the original array of strings,
	 * or null if there is no argument at that index. If the argument exists, it
	 * is marked {@link #checkUnused() used}.
	 * 
	 * @param index the index of the desired argument
	 * @return the argument at that index
	 * @throws IndexOutOfBoundsException if the index is negative
	 */
	public String get(int index) {
		if(index < 0)
			throw Exceptions.indexOutOfBounds(index, size());
		else if(index >= size())
			return null;
		else {
			used[index] = true;
			return args[index];
		}
	}
	
	/**
	 * Returns the argument at the given index in the original array of strings,
	 * or throws an exception no such argument exists. If the argument does
	 * exist, it is marked {@link #checkUnused() used}.
	 * 
	 * @param index the index of the argument that must exist
	 * @return the argument at that index
	 * @throws IndexOutOfBoundsException if the index is negative
	 * @throws RuntimeException if no argument exists at that index
	 */
	public String require(int index) {
		String argument = get(index);
		if(argument == null)
			throw ToolsExceptions.argumentIndexRequired(index);
		else
			return argument;
	}
	
	/**
	 * Returns the argument immediately after the given key argument, or null
	 * if the key does not exist or has nothing after it. If the key is found,
	 * the key and the argument after it are marked {@link #checkUnused() used}.
	 * 
	 * @param key the argument whose value is desired
	 * @return the argument immediately after the given key
	 */
	public String getValue(String key) {
		int index = indexOf(key);
		if(index == -1 || index == size() - 1)
			return null;
		else
			return get(index + 1);
	}
	
	/**
	 * Returns the argument immediately after the given {@link Option#key
	 * option's key}, or returns the {@link
	 * Option#getDefaultValue(CommandLineArguments) option's default value} if
	 * the option's key does not exist, or returns null if there is no default
	 * value. An option's key only exists in this list if it is preceded by a
	 * dash. For example, if the option's key is {@code "o"} then this method
	 * returns the argument after the argument {@code "-o"} in this list. If
	 * the option's key is found, the key and the argument after it are marked
	 * {@link #checkUnused() used}.
	 * 
	 * @param option the option whose key's value is desired
	 * @return the argument immediately after the option's key or the option's
	 * default value
	 */
	public String getValue(Option option) {
		String value = getValue("-" + option.key);
		if(value == null)
			value = option.getDefaultValue(this);
		return value;
	}
	
	/**
	 * Returns the argument immediately after the given key argument, or throws
	 * an exception if the key does not exist or has nothing after it. If the
	 * key is found, the key and the argument after it are marked {@link
	 * #checkUnused() used}.
	 * 
	 * @param key the argument whose value is desired
	 * @return the argument immediately after the given key
	 * @throws RuntimeException if the key does not exist or if it has nothing
	 * after it
	 */
	public String requireValue(String key) {
		int index = indexOf(key);
		if(index == -1)
			throw ToolsExceptions.argumentNameRequired(key);
		else if(index == size() - 1)
			throw ToolsExceptions.argumentValueRequired(key);
		else
			return get(index + 1);
	}
	
	/**
	 * Returns the argument immediately after the given {@link Option#key
	 * option's key}, or returns the {@link
	 * Option#getDefaultValue(CommandLineArguments) option's default value} if
	 * the option's key does not exist, or throws an exception if there is no
	 * default value. An option's key only exists in this list if it is preceded
	 * by a dash. For example, if the option's key is {@code "o"} then this
	 * method returns the argument after the argument {@code "-o"} in this list.
	 * If the option's key is found, the key and the argument after it are
	 * marked {@link #checkUnused() used}.
	 * 
	 * @param option the option whose key's value is desired
	 * @return the argument immediately after the option's key of the option's
	 * default value
	 * @throws RuntimeException if the key does not exist and the option has no
	 * default value
	 */
	public String requireValue(Option option) {
		String value = getValue("-" + option.key);
		if(value == null)
			value = option.getDefaultValue(this);
		if(value == null)
			return requireValue("-" + option.key);
		else
			return value;
	}
	
	/**
	 * Throws an exception if any arguments in this list have not been used.
	 * An argument is used any time it is interacted with, such as {@link
	 * #contains(String) checking whether it exists} or {@link #getValue(String)
	 * getting its value}.
	 * 
	 * @throws RuntimeException if any argument in this list have not been used
	 */
	public void checkUnused() {
		for(int i = 0; i < args.length; i++)
			if(!used[i])
				throw ToolsExceptions.unusedArgument(args[i]);
	}
}