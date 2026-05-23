package edu.uky.cs.nil.sg;

import java.util.Comparator;

/**
 * A {@link Task task} that {@link SymbolList#sort(Comparator, Status) sorts} a
 * list of {@link SymbolList story graph symbols} alphabetically by {@link
 * Symbol#name name}.
 * 
 * @author Stephen G. Ware
 */
public class SortSymbols implements Task {
	
	/** A comparator that order story graph symbols by name */
	public static final Comparator<Symbol> SYMBOL_NAME = new Comparator<>() {
		
		@Override
		public int compare(Symbol symbol1, Symbol symbol2) {
			return symbol1.name.compareTo(symbol2.name);
		}
	};
	
	/** The list of symbols to sort */
	protected final SymbolList<?> symbols;
	
	/**
	 * Constructs a new story graph symbol sort task.
	 * 
	 * @param symbols the list of story graph symbols to sort
	 */
	public SortSymbols(SymbolList<?> symbols) {
		this.symbols = symbols;
	}
	
	@Override
	public void run(Status status) throws Exception {
		symbols.sort(SYMBOL_NAME, status);
	}
}