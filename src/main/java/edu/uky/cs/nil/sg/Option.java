package edu.uky.cs.nil.sg;

/**
 * An option that can be used to configure a {@link StoryGraphTool story graph
 * tool} when passed as an {@link ToolArguments argument}. This object describes
 * the option's key, its value (if any), its description, and its default value.
 * These features are used to automatically generate {@link
 * StoryGraphTool#getHelp() help text} for a story graph tool.
 * 
 * @author Stephen G. Ware
 */
public class Option {
	
	/** The option's key */
	public final String key;
	
	/** The type of value given after the key, or null if no value is given */
	public final String value;
	
	/** A description of how this option affects a story graph tool */
	public final String description;
	
	/** A default value to return if the option does not appear */
	private final String defaultValue;
	
	/**
	 * Constructs a new option from its key, optional value type, description,
	 * and optional default value.
	 * 
	 * @param key the key used for this option (without a dash)
	 * @param value the type of value given to this option or null if no value
	 * is given to this option
	 * @param description a description of how this option affects a story graph
	 * tool
	 * @param defaultValue a default value to return when the option is not used
	 * or null
	 */
	public Option(String key, String value, String description, String defaultValue) {
		this.key = key;
		this.value = value;
		this.description = description;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Constructs a new option from its key, optional value type, and
	 * description.
	 * 
	 * @param key the key used for this option (without a dash)
	 * @param value the type of value given to this option or null if no value
	 * is given to this option
	 * @param description a description of how this option affects a story graph
	 * tool
	 */
	public Option(String key, String value, String description) {
		this(key, value, description, null);
	}
	
	/**
	 * Constructs a new option from its key and descroption.
	 * 
	 * @param key the key used for this option (without a dash)
	 * @param description a description of how this option affects a story graph
	 * tool
	 */
	public Option(String key, String description) {
		this(key, null, description);
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	/**
	 * Returns the default value for this option if none was explicitly given,
	 * or null if there is no reasonable default value.
	 * 
	 * @param arguments the list of arguments used to configure a story graph
	 * tool
	 * @return the default value for this option or null
	 */
	public String getDefaultValue(ToolArguments arguments) {
		return defaultValue;
	}
}