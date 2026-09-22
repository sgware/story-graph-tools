package edu.uky.cs.nil.sg;

import java.io.File;
import java.util.List;

/**
 * Provides a way to define a specific argument in {@link Arguments a collection
 * of arguments}, including a way to parse its value and documentation of what
 * it is used for.
 * 
 * @param <T> the type of value associated with this argument
 * @author Stephen G. Ware
 */
public abstract class Argument<T> {
	
	/** A short string showing how to use the argument */
	public final String usage;
	
	/** A description of what the argument means */
	public final String description;
	
	/**
	 * Constructs a defined argument.
	 * 
	 * @param usage a short string showing how to use the argument
	 * @param description a description of what the argument means
	 */
	public Argument(String usage, String description) {
		this.usage = usage;
		this.description = description;
	}
	
	/**
	 * Returns the value associated with this argument from {@link Arguments a
	 * collection of arguments}, or null if this argument does not exist in that
	 * collection or has no value.
	 * 
	 * @param arguments the collection of arguments
	 * @return the value associated with this argument if it is present in the
	 * collection, or null if it is not
	 */
	protected abstract T get(Arguments arguments);
	
	/**
	 * A flag is a key {@link Argument argument} (a string starting with a dash)
	 * that is either present or not in {@link Arguments a collection of
	 * arguments} and does not have a value after it. A flag's value is true if
	 * it is present and false if it is missing.
	 * 
	 * @author Stephen G. Ware
	 */
	public static class Flag extends Argument<Boolean> {
		
		/** The key for this argument, without its leading dash */
		public final String key;
		
		/** Other flags which imply this flag is present */
		public final List<Flag> aliases;
		
		/**
		 * Constructs a new flag from a key.
		 * 
		 * @param key the name of the flag without its leading dash
		 * @param description a description of what this flag means when it is
		 * present
		 * @param aliases other flags which imply this flag is present
		 */
		public Flag(String key, String description, Flag...aliases) {
			super("-" + key, description);
			this.key = key;
			this.aliases = List.of(aliases);
		}
		
		@Override
		public String toString() {
			return "-" + key;
		}
		
		/**
		 * {@inheritDoc}
		 * <p>
		 * A flag returns true if it (or one of its {@link #aliases aliases} is
		 * present in a collection of arguments and false if it is not present.
		 */
		@Override
		protected Boolean get(Arguments arguments) {
			if(arguments.contains(key))
				return true;
			for(Flag alias : aliases)
				if(arguments.get(alias))
					return true;
			return false;
		}
	}
	
	/**
	 * A parser is a function which converts a string value from {@link
	 * Arguments a collection of arguments} into an object of a certain type.
	 * 
	 * @param <T> the type of object the string will be converted to
	 * @author Stephen G. Ware
	 */
	@FunctionalInterface
	public interface Parser<T> {
		
		/**
		 * Converts a string value from a collection of arguments into an object
		 * of this parser's type.
		 * 
		 * @param value the string to be converted to a value
		 * @param arguments the list of arguments from which this value was
		 * taken
		 * @return the object the string has been converted to
		 */
		public T parse(String value, Arguments arguments);
	}
	
	/**
	 * A parsed {@link Argument} is associated in some way with a string value
	 * that can be converted into another kind of object via a {@link Parser}.
	 * 
	 * @param <T> the type of value the argument is associated with
	 * @author Stephen G. Ware
	 */
	public static abstract class Parsed<T> extends Argument<T> {
		
		/** The function used to convert this argument's value into an object */
		public final Parser<T> parser;
		
		/** A value to use when this argument does not exist */
		public final T defaultValue;
		
		/**
		 * Constructs a new parsed argument.
		 * 
		 * @param usage a short string showing how to use the argument
		 * @param parser the function used to convert this argument's value into
		 * an object
		 * @param description a description of what the argument means
		 * @param defaultValue a value to use when this argument does not exist
		 */
		public Parsed(String usage, Parser<T> parser, String description, T defaultValue) {
			super(usage, description);
			this.parser = parser;
			this.defaultValue = defaultValue;
		}
	}
	
	/**
	 * An index is an {@link Argument} at a specific position in a {@link
	 * Arguments collection of arguments}.
	 * 
	 * @param <T> the type of value at the index position
	 * @author Stephen G. Ware
	 */
	public static class Index<T> extends Parsed<T> {
		
		/** The position of the argument in the collection of arguments */
		public final int index;
		
		/**
		 * Constructs an index argument with a default value.
		 * 
		 * @param index the position of the argument in the collection of
		 * arguments
		 * @param parser the function used to convert this argument into an
		 * object
		 * @param description a description of what the argument means
		 * @param defaultValue a value to use when this argument does not exist
		 */
		public Index(int index, Parser<T> parser, String description, T defaultValue) {
			super(parser.toString(), parser, description, defaultValue);
			this.index = index;
		}
		
		/**
		 * Constructs an index argument with no default value.
		 * 
		 * @param index the position of the argument in the collection of
		 * arguments
		 * @param parser the function used to convert this argument into an
		 * object
		 * @param description a description of what the argument means
		 */
		public Index(int index, Parser<T> parser, String description) {
			this(index, parser, description, null);
		}
		
		@Override
		public String toString() {
			return "Argument " + (index + 1);
		}
		
		/**
		 * {@inheritDoc}
		 * <p>
		 * An index {@link Arguments#get(int) gets} the value at its position,
		 * parses it, and returns the result. If there is no argument at that
		 * position, the default value is returned.
		 */
		@Override
		protected T get(Arguments arguments) {
			String value = arguments.get(index);
			if(value == null)
				return defaultValue;
			else
				return parser.parse(value, arguments);
		}
	}
	
	/**
	 * A key is an {@link Argument} that starts with a dash and is followed by
	 * a value.
	 * 
	 * @param <T> the type of value after the key
	 * @author Stephen G. Ware
	 */
	public static class Key<T> extends Parsed<T> {
		
		/** The key for this argument, without its leading dash */
		public final String key;
		
		/**
		 * Constructs a key argument with a default value.
		 * 
		 * @param key the key for this argument, without its leading dash
		 * @param parser the function used to convert this argument's value into
		 * an object
		 * @param description a description of what the argument means
		 * @param defaultValue a value to use when this argument does not exist
		 */
		public Key(String key, Parser<T> parser, String description, T defaultValue) {
			super("-" + key + " " + parser.toString(), parser, description, defaultValue);
			this.key = key;
		}
		
		/**
		 * Constructs a key argument without a default value.
		 * 
		 * @param key the key for this argument, without its leading dash
		 * @param parser the function used to convert this argument's value into
		 * an object
		 * @param description a description of what the argument means
		 */
		public Key(String key, Parser<T> parser, String description) {
			this(key, parser, description, null);
		}
		
		@Override
		public String toString() {
			return "-" + key;
		}
		
		/**
		 * {@inheritDoc}
		 * <p>
		 * A key {@link Arguments#get(String) gets} the value after its key,
		 * parses it, and returns the result. If the key does not appear or has
		 * no value after it, the default value is returned.
		 */
		@Override
		protected T get(Arguments arguments) {
			String value = arguments.get(key);
			if(value == null)
				return defaultValue;
			else
				return parser.parse(value, arguments);
		}
	}
	
	/** A {@link Parser} that simply returns the same string it is given */
	public static final Parser<String> STRING = new Parser<String>() {
		
		@Override
		public String toString() {
			return "<string>";
		}
		
		@Override
		public String parse(String value, Arguments arguments) {
			return value;
		}
	};
	
	/**
	 * A {@link Parser} that converts a string to an integer in a defined range.
	 * 
	 * @author Stephen G. Ware
	 */
	public static final class Integer implements Parser<java.lang.Integer> {
		
		/** The minimum allowed value */
		public final int min;
		
		/** The maximum allowed value */
		public final int max;
		
		/**
		 * Constructs an integer parser with a given range.
		 * 
		 * @param min the minimum allowed value
		 * @param max the maximum allowed value
		 */
		public Integer(int min, int max) {
			this.min = min;
			this.max = max;
		}
		
		/**
		 * Constructs an integer parser with minimum but no maximum.
		 * 
		 * @param min the minimum allowed value
		 */
		public Integer(int min) {
			this(min, java.lang.Integer.MAX_VALUE);
		}
		
		/**
		 * Constructs an integer parser with no minimum or maximum.
		 */
		public Integer() {
			this(java.lang.Integer.MIN_VALUE);
		}
		
		@Override
		public String toString() {
			return "<number>";
		}
		
		@Override
		public java.lang.Integer parse(String value, Arguments arguments) {
			int result;
			try {
				result = java.lang.Integer.parseInt(value);
			}
			catch(Exception exception) {
				throw new IllegalArgumentException("\"" + value + "\" could not be parsed as an integer.");
			}
			if(result < min)
				throw new IllegalArgumentException(value + " is less than " + min + ".");
			else if(result > max)
				throw new IllegalArgumentException(value + " is greater than " + max + ".");
			else
				return result;
		}
	}
	
	/** A {@link Parser} that converts a string to a {@link File file} */
	public static final Parser<File> FILE = new Parser<File>() {
		
		@Override
		public String toString() {
			return "<file>";
		}
		
		@Override
		public File parse(String value, Arguments arguments) {
			return new File(value);
		}
	};
}