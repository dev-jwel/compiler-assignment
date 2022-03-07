import java.util.Vector;

public class Core {
	Symbol from;
	Vector<Symbol> beforeDot;
	Vector<Symbol> afterDot;

	public Core(Symbol from, Vector<Symbol> beforeDot, Vector<Symbol> afterDot) {
		this.from = from;
		this.beforeDot = beforeDot;
		this.afterDot = afterDot;

		if(this.beforeDot.size() > 0 && this.beforeDot.get(0).isEpsilon()) {
			this.beforeDot = new Vector<Symbol>();
		}
		if(this.afterDot.size() > 0 && this.afterDot.get(0).isEpsilon()) {
			this.afterDot = new Vector<Symbol>();
		}
	}

	public boolean equals(Core core2) {
		boolean isFromSame = this.from.equals(core2.from);
		boolean isBeforeSame = this.beforeDot.equals(core2.beforeDot);
		boolean isAfterSame =  this.afterDot.equals(core2.afterDot);
		return isFromSame && isBeforeSame && isAfterSame;
	}

	public Symbol getFrom() {
		return this.from;
	}

	public Vector<Symbol> getBeforeDot() {
		return this.beforeDot;
	}

	public Vector<Symbol> getAfterDot() {
		return this.afterDot;
	}

	public void print() {
		System.out.print(this.from.getSymbol());
		System.out.print(" -> ");
		for(Symbol s: this.beforeDot) {
			System.out.print(s.getSymbol());
		}
		System.out.print(" . ");
		for(Symbol s: this.afterDot) {
			System.out.print(s.getSymbol());
		}
	}
}
