package edu.uky.cs.nil.sg;

/**
 * Represents a {@link StoryGraph story graph} before and after some operation.
 * This object is primarily used to {@link #toString() print} a summary of the
 * story graph after the operation showing its relative change from before.
 * 
 * @author Stephen G. Ware
 */
public class StoryGraphChange {
	
	/** The summary of the graph before the operation */
	public final StoryGraphSummary before;
	
	/** The summary of the graph after the operation */
	public final StoryGraphSummary after;
	
	/**
	 * Constructs a story graph change object.
	 * 
	 * @param before the summary of the graph before the operation
	 * @param after the summary of the graph after the operation
	 */
	public StoryGraphChange(StoryGraphSummary before, StoryGraphSummary after) {
		this.before = before;
		this.after = after;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * A story graph change prints the statistics tracked by the {@link #after}
	 * summary and, if the value is different in the {@link before} summary, it
	 * prints the relative change.
	 */
	@Override
	public String toString() {
		String string = "Story Graph";
		if(after.title != null)
			string += " \"" + after.title + "\"";
		string += ":\n";
		Object[][] table = new Object[after.size()][3];
		int row = 0;
		for(String key : after.keys()) {
			table[row][0] = key + ":";
			table[row][1] = StoryGraphSummary.NUMBER.format(after.get(key));
			Long before = this.before.get(key);
			if(before != null) {
				long after = this.after.get(key);
				long change = after - before;
				if(change != 0) {
					String cell = "(";
					cell += (change > 0 ? "+" : "") + StoryGraphSummary.NUMBER.format(change);
					if(before != 0)
						cell += "; " + (change > 0 ? "+" : "-") + percent(Math.abs(change), before) + "%";
					cell += ")";
					table[row][2] = cell;
				}
			}
			row++;
		}
		string += StoryGraphSummary.toString(table);
		return string;
	}
	
	private static final long percent(long numerator, long denominator) {
		if(numerator == denominator)
			return 100;
		else {
			long value = (long) Utilities.percent(numerator, denominator, 0);
			if(value == 0)
				return 1;
			else if(value == 100)
				return 99;
			else
				return value;
		}
	}
}