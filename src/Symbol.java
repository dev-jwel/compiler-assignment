import java.util.Vector;

public class Symbol {
	private String symbol;
	private Vector<Symbol> firsts;
	private Vector<Symbol> follows;

	public Symbol(String symbol) {
		this.symbol = symbol;
		this.firsts = new Vector<Symbol>();
		this.follows = new Vector<Symbol>();
	}

	public boolean equals(Symbol symbol2) {
		return this.symbol.equals(symbol2.symbol);
	}

	public String getSymbol() {
		return this.symbol;
	}

	public Vector<Symbol> getFirst() {
		return this.firsts;
	}

	public Vector<Symbol> getFollow() {
		return this.follows;
	}

	public boolean isEpsilon() {
		return this.symbol.equals("__");
	}

	public boolean isNonterminal() {
		return Character.isUpperCase(this.symbol.charAt(0));
	}

	public boolean isTerminal() {
		return !(this.isEpsilon() || this.isNonterminal());
	}

	public boolean appendFirst(Symbol symbol) {
		if(!this.firsts.contains(symbol)) {
			this.firsts.add(symbol);
			return true;
		}
		return false;
	}

	public boolean appendFollow(Symbol symbol) {
		if(!this.follows.contains(symbol)) {
			this.follows.add(symbol);
			return true;
		}
		return false;
	}
}
