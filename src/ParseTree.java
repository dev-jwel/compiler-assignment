import java.util.Vector;

public class ParseTree {
	private Symbol symbol;
	private Vector<ParseTree> childrun;

	public ParseTree(Symbol symbol) {
		this.symbol = symbol;
		this.childrun = new Vector<ParseTree>();
	}

	public ParseTree(Symbol symbol, Vector<ParseTree> childrun) {
		this.symbol = symbol;
		this.childrun = childrun;
	}

	private void print(int tab) {
		for(int i=0; i<tab; i++) {
			System.out.print("\t");
		}
		System.out.println(this.symbol.getSymbol());
		for(ParseTree child: this.childrun) {
			child.print(tab + 1);
		}
	}

	public void print() {
		this.print(0);
	}
}
