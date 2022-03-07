public class StackElement {
	private int state;
	private Symbol symbol;

	public StackElement(int state) {
		this.state = state;
		this.symbol = null;
	}

	public StackElement(Symbol symbol) {
		this.state = -1;
		this.symbol = symbol;
	}

	public int getState() {
		return this.state;
	}

	public Symbol getSymbol() {
		return this.symbol;
	}
}
