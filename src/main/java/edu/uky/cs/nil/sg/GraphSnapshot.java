package edu.uky.cs.nil.sg;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An object for printing summary statistics about a {@link StoryGraph story
 * graph} before and after it has been modified.
 * 
 * @author Stephen G. Ware
 */
public class GraphSnapshot {
	
	/**
	 * Prints an integer with commas after each third digit and no digits after
	 * the decimal
	 */
	private static final DecimalFormat number = new DecimalFormat("#,###");
	
	/** The {@link StoryGraph#getTitle() title} of the story graph */
	private final String title;
	
	/** A set of numeric statistics communicating the size of the story graph */
	private final LinkedHashMap<String, Long> statistics = new LinkedHashMap<>();
	
	/**
	 * Constructs a new story graph snapshot for the given story graph.
	 * 
	 * @param graph the story graph that will be summarized
	 */
	public GraphSnapshot(StoryGraph graph) {
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
			statistics.put(list.getPlural(), (long) symbols.size());
		else if(list instanceof NumberedList numbered)
			statistics.put(list.getPlural(), numbered.size());
	}
	
	@Override
	public String toString() {
		return toString(this, null);
	}
	
	/**
	 * Returns a string showing the summary statistics for a story graph; if two
	 * snapshots are given, the statistics will be for the second snapshot and
	 * will indicate how they have changed since the first snapshot.
	 * 
	 * @param before a snapshot for a story graph before modification (cannot be
	 * null)
	 * @param after a snapshot for a story graph after modification, which can
	 * be null; if it is null, only the statistics from the first snapshot will
	 * be shown
	 * @return a string showing the summary statistics for the story graph and
	 * how they have changed (if a second snapshot was provided)
	 */
	public static String toString(GraphSnapshot before, GraphSnapshot after) {
		String string = "Story Graph";
		if(before.title != null)
			string += " \"" + before.title + "\"";
		string += ":";
		Object[][] rows = new Object[before.statistics.size()][];
		int index = 0;
		int labelPad = 0;
		int valuePad = 0;
		for(Map.Entry<String, Long> entry : before.statistics.entrySet()) {
			Object[] row = new Object[3];
			row[0] = "  " + entry.getKey() + ":";
			labelPad = Math.max(labelPad, ((String) row[0]).length());
			row[1] = entry.getValue();
			valuePad = Math.max(valuePad, number.format(row[1]).length());
			if(after != null) {
				row[2] = after.statistics.get(entry.getKey());
				valuePad = Math.max(valuePad, number.format(row[2]).length());
			}
			rows[index++] = row;
		}
		for(Object[] row : rows) {
			long value;
			if(row[2] == null)
				value = (long) row[1];
			else
				value = (long) row[2];
			string += String.format("\n%-" + labelPad + "s %" + valuePad + "s", row[0], number.format(value));
			if(row[2] != null) {
				long difference = value - ((long) row[1]);
				long percent = percent(Math.abs(difference), (long) row[1]);
				if(difference < 0)
					string += " (down " + Math.abs(difference) + ", or " + percent + "%)";
				else if(difference > 0)
					string += " (up " + difference + (row[1].equals(0L) ? "" : ", or " + percent + "%") + ")";
				else
					string += " (no change)";
			}
		}
		return string;
	}
	
	private static final long percent(long numerator, long denominator) {
		if(numerator == denominator)
			return 100;
		else
			return Math.min((long) Utilities.percent(numerator, denominator, 0), 99);
	}
}