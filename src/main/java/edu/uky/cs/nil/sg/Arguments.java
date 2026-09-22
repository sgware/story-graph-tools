package edu.uky.cs.nil.sg;

/**
 * A wrapper for the string arguments passed to a Java program's
 * <code>main</code> method that provides ways to check which arguments exist,
 * what their values are, and which have been used.
 * <p>
 * The {@link Argument} class provides a way to get and parse specific arguments
 * and to document their purpose.
 * 
 * @author Stephen G. Ware
 */
public class Arguments {
	
	/** The array of string arguments passed to a Java program */
	private final String[] args;
	
	/** Which arguments have been used */
	private final boolean[] used;
	
	/**
	 * Constructs a collection of arguments from an array of strings, such as
	 * those passed to a Java <code>main</code> method.
	 * 
	 * @param args an array of arguments
	 */
	public Arguments(String[] args) {
		this.args = args;
		this.used = new boolean[args.length];
	}
	
	/**
	 * Returns the number of arguments in this collection.
	 * 
	 * @return the number of arguments
	 */
	public int size() {
		return args.length;
	}
	
	/**
	 * Returns the argument at the given index (where the first argument is at
	 * index 0), or null if there is no such argument. This method marks the
	 * argument at that index as {@link #checkUnused() used}.
	 * 
	 * @param index the index of the desired argument
	 * @return the argument at that index, or null if that index does not exist
	 */
	public String get(int index) {
		if(index < args.length) {
			used[index] = true;
			return args[index];
		}
		else
			return null;
	}
	
	/**
	 * Returns the argument at the given index (where the first argument is at
	 * index 0), or throws an exception if there is no such argument. This
	 * method marks the argument at that index as {@link #checkUnused() used}.
	 * 
	 * @param index the index of the desired argument
	 * @return the argument at that index
	 * @throws IllegalArgumentException if the index does not exist
	 */
	public String require(int index) {
		String result = get(index);
		if(result == null)
			throw new IllegalArgumentException("Argument " + (index + 1) + " is required.");
		else
			return result;
	}
	
	/**
	 * Returns true if the given key appears in this collection of arguments.
	 * A key is an argument that begins with a dash. The string given to this
	 * method may include the dash or not. For example, if this method is called
	 * with the string <code>"example"</code>, it will return true if if the
	 * string <code>"-example"</code> appear in this collection of arguments.
	 * The string given to this method can be either <code>"example"</code> or
	 * <code>"-example"</code>. This method marks the key as {@link
	 * #checkUnused() used} if it is found.
	 * 
	 * @param key the key to search for
	 * @return true if these arguments contains the given key
	 */
	public boolean contains(String key) {
		int index = indexOf(key);
		if(index == -1)
			return false;
		else {
			used[index] = true;
			return true;
		}
	}
	
	/**
	 * Returns the index of the given key (adding a dash to the start if it is
	 * missing) or -1 if it does not exist.
	 * 
	 * @param key the key to search for
	 * @return the index of the key, or -1 if it is not found
	 */
	private int indexOf(String key) {
		if(!key.startsWith("-"))
			key = "-" + key;
		for(int i = 0; i < args.length; i++)
			if(args[i].equalsIgnoreCase(key))
				return i;
		return -1;
	}
	
	/**
	 * Returns the value that appears after a key, or null if the key or value
	 * does not exist. A key is an argument that begins with a dash, and a value
	 * must not begin with a dash. The string given to this method may include
	 * the dash or not. For example, if this collection of arguments contains
	 * <code>"-key val"</code>, then this method will return <code>"val"</code>
	 * when called with the string <code>"key"</code> or <code>"-key"</code>.
	 * This method marks the key and value as {@link #checkUnused() used} if
	 * they are found.
	 * 
	 * @param key the key to search for
	 * @return the value immediately after the key, if the key was found an has
	 * a value after it, or null if the key or value does not exist
	 */
	public String get(String key) {
		int index = indexOf(key);
		if(index != -1 && index + 1 < args.length && !args[index + 1].startsWith("-")) {
			used[index] = true;
			used[index + 1] = true;
			return args[index + 1];
		}
		return null;
	}
	
	/**
	 * Returns the value that appears after a key, or throws an exception if the
	 * key or value does not exist. A key is an argument that begins with a dash,
	 * and a value must not begin with a dash. The string given to this method
	 * may include the dash or not. For example, if this collection of arguments
	 * contains <code>"-key val"</code>, then this method will return
	 * <code>"val"</code> when called with the string <code>"key"</code> or
	 * <code>"-key"</code>. This method marks the key and value as {@link
	 * #checkUnused() used} if they are found.
	 * 
	 * @param key the key to search for
	 * @return the value immediately after the key
	 * @throws IllegalArgumentException if the key was not found or if there was
	 * no value after it
	 */
	public String require(String key) {
		if(!key.startsWith("-"))
			key = "-" + key;
		if(!contains(key))
			throw new IllegalArgumentException("The key \"" + key + "\" is required.");
		String value = get(key);
		if(value == null)
			throw new IllegalArgumentException("The key \"" + key + "\" must have a value.");
		else
			return value;
	}
	
	/**
	 * Returns the value of a defined {@link Argument argument} or null if it
	 * does not exist. This method marks any arguments involved as {@link
	 * #checkUnused() used}.
	 * 
	 * @param <T> the type of value the argument has
	 * @param argument a defined argument
	 * @return the value of that argument, or null if the argument or value does
	 * not exist
	 */
	public <T> T get(Argument<T> argument) {
		return argument.get(this);
	}
	
	/**
	 * Returns the value of a defined {@link Argument argument} or throws an
	 * exception if it does not exist. This method marks any arguments involved
	 * as {@link #checkUnused() used}.
	 * 
	 * @param <T> the type of value the argument has
	 * @param argument a defined argument
	 * @return the value of that argument
	 * @throws IllegalArgumentException if the argument or its value does not
	 * exist
	 */
	public <T> T require(Argument<T> argument) {
		T value = argument.get(this);
		if(value == null)
			throw new IllegalArgumentException("Argument \"" + argument + "\" is required.");
		else
			return value;
	}
	
	/**
	 * Throws an exception if any arguments in this collection have not been
	 * accessed by the other methods in this object.
	 * 
	 * @throws IllegalArgumentException if any arguments in this collection have
	 * not been marked as used by other methods
	 */
	public void checkUnused() {
		for(int i = 0; i < used.length; i++)
			if(!used[i])
				throw new IllegalArgumentException("Argument \"" + args[i] + "\" was not recognized.");
	}
}