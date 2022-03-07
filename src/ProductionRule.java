import java.util.Vector;

public class ProductionRule {
	private Symbol from;
	private Vector<Symbol> to;

	public ProductionRule(Symbol from, Vector<Symbol> to) {
		this.from = from;
		this.to = to;
	}

	public Symbol getFrom() {
		return this.from;
	}

	public Vector<Symbol> getTo() {
		return this.to;
	}

	public String toString() {
		String ret = "";
		ret += this.from.getSymbol();
		ret += " ==>";
		for(Symbol t: this.to) {
			ret += " ";
			ret += t.getSymbol();
		}

		return ret;
	}

	public void print() {
		System.out.println(this.toString());
	}
}
