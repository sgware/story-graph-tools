package edu.uky.cs.nil.sg;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A collection of numeric statistics about the sizes of collections in a {@link
 * StoryGraph story graph}, such as the number of characters, number of nodes,
 * etc. This object is primarily used to {@link #toString() print} a summary of
 * a story graph.
 * 
 * @author Stephen G. Ware
 */
public class StoryGraphSummary implements Iterable<Map.Entry<String, Long>> {
	
	/**
	 * Prints an integer with commas after each third digit and no digits after
	 * the decimal
	 */
	static final DecimalFormat NUMBER = new DecimalFormat("#,###");
	
	/**
	 * The {@link StoryGraph#getTitle() title} of the story graph summarized
	 */
	public final String title;
	
	/** A set of numeric statistics communicating the size of the story graph */
	private final LinkedHashMap<String, Long> statistics = new LinkedHashMap<>();
	
	/**
	 * Constructs a story graph summary for a given story graph. The sizes of
	 * important graph elements, like the number of character, number of nodes,
	 * etc., will be automatically {@link #set(String, long) set}.
	 * 
	 * @param graph the story graph to summarize
	 */
	public StoryGraphSummary(StoryGraph graph) {
		this.title = graph.getTitle();
		add(graph.characters);
		add(graph.fluents);
		add(graph.values);
		add(graph.states);
		add(graph.actions);
		add(graph.plans);
		add(graph.nodes);
		add(graph.edges.temporal);
		add(graph.edges.epistemic);
		add(graph.explanations);
	}
	
	private void add(StoryGraphList<?> list) {
		if(list instanceof SymbolList symbols)
			set(list.getPlural(), (long) symbols.size());
		else if(list instanceof NumberedList numbered)
			set(list.getPlural(), numbered.size());
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * A story graph summary prints all of the statistics tracked by this
	 * summary and their values in an easy-to-read string.
	 */
	@Override
	public String toString() {
		String string = "Story Graph";
		if(title != null)
			string += " \"" + title + "\"";
		string += ":\n";
		Object[][] table = new Object[size()][2];
		int row = 0;
		for(String key : keys()) {
			table[row][0] = key + ":";
			table[row][1] = NUMBER.format(get(key));
			row++;
		}
		string += toString(table);
		return string;
	}
	
	/**
	 * Converts a table of values to a string. Numeric values will be
	 * right-aligned in their column while all others will be left-aligned in
	 * their column.
	 * 
	 * @param table an array of objects that form the rows and columns of a
	 * table
	 * @return a string representation of the table suitable for printing
	 */
	static final String toString(Object[][] table) {
		String[][] cells = new String[table.length][];
		int[] pad = new int[0];
		for(Object[] row : table)
			if(row.length > pad.length)
				pad = new int[row.length];
		for(int row = 0; row < table.length; row++) {
			cells[row] = new String[pad.length];
			for(int col = 0; col < pad.length; col++) {
				cells[row][col] = col < table[row].length && table[row][col] != null ? table[row][col].toString() : "";
				pad[col] = Math.max(pad[col], cells[row][col].length());
			}
		}
		String string = "";
		for(int row = 0; row < cells.length; row++) {
			if(row > 0)
				string += "\n";
			for(int col = 0; col < pad.length; col++) {
				if(pad[col] > 0) {
					String cell = cells[row][col];
					if(isNumber(cell))
						string += String.format("%" + pad[col] + "s", cell);
					else
						string += String.format("%-" + pad[col] + "s", cell);
					if(col < pad.length - 1)
						string += " ";
				}
			}
		}
		return string;
	}
	
	private static final boolean isNumber(String string) {
		string = string.trim();
		if(string.isEmpty())
			return false;
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		ParsePosition position = new ParsePosition(0);
		numberFormat.parse(string, position);
		return position.getIndex() == string.length();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns a set of key/value pairs for each statistic in this summary.
	 */
	@Override
	public Iterator<Entry<String, Long>> iterator() {
		return statistics.entrySet().iterator();
	}
	
	/**
	 * Returns the number of statistics in this summary.
	 * 
	 * @return the number of statistics
	 */
	public int size() {
		return statistics.size();
	}
	
	/**
	 * Returns the value of the statistic with the given name, or null if no
	 * such statistic exists in this summary.
	 * 
	 * @param key the name of the desired statistic
	 * @return the value of that statistic, or null if it does not exist
	 */
	public Long get(String key) {
		return statistics.get(key);
	}
	
	/**
	 * Sets the given statistic to the given value.
	 * 
	 * @param key the name of the statistic
	 * @param value the value of the statistic
	 */
	public void set(String key, long value) {
		statistics.put(key, value);
	}
	
	/**
	 * Returns the names of the statistics in this summary.
	 * 
	 * @return the names of the statistics
	 */
	public Iterable<String> keys() {
		return statistics.keySet();
	}
	
	/**
	 * Removes a statistic from this summary.
	 * 
	 * @param key the statistic to remove
	 */
	public void remove(String key) {
		statistics.remove(key);
	}
}