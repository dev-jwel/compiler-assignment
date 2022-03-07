import java.util.Vector;

public class Item {
	private Core core;
	private Vector<Symbol> lookahead;

	public Item(Core core) {
		this(core, new Vector<Symbol>());
	}

	public Item(Core core, Vector<Symbol> lookahead) {
		this.core = core;
		this.lookahead = lookahead;
	}

	public boolean contains(Symbol lookahead) {
		return this.lookahead.contains(lookahead);
	}

	public boolean isLookaheadEquals(Item item2) {
		if(this.lookahead.size() != item2.lookahead.size()) {
			return false;
		}
		for(Symbol l: this.lookahead) {
			if(!item2.lookahead.contains(l)) {
				return false;
			}
		}
		return true;
	}

	public boolean isCoreEquals(Item item2) {
		return this.core.equals(item2.core);
	}

	public boolean equals(Item item2) {
		boolean isCoreSame = this.core.equals(item2.core);
		boolean isLookaheadSame = this.lookahead.equals(item2.lookahead);
		return isCoreSame && isLookaheadSame;
	}

	public Core getCore() {
		return this.core;
	}

	public Vector<Symbol> getLookahead() {
		return this.lookahead;
	}

	public void appendLookahead(Symbol l) {
		this.lookahead.add(l);
	}

	public void print() {
		this.core.print();
		System.out.print(" , ");
		for(Symbol s: this.lookahead) {
			System.out.print(s.getSymbol());
			System.out.print(" ");
		}
		System.out.println();
	}
}
