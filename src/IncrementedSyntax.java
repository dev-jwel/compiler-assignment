import java.util.Vector;

public class IncrementedSyntax extends Syntax {
	private Symbol oldStartSymbol;
	private Symbol newStartSymbol;

	public IncrementedSyntax(Syntax syntax) {
		this.symbols = syntax.symbols;
		this.productionRules = syntax.productionRules;
		this.dollar = syntax.dollar;

		this.oldStartSymbol = syntax.startSymbol;
		this.newStartSymbol = new Symbol(syntax.startSymbol.getSymbol() + "`");
		this.symbols.add(0, newStartSymbol);

		Vector<Symbol> to = new Vector<Symbol>();
		to.add(this.oldStartSymbol);

		ProductionRule newRule = new ProductionRule(this.newStartSymbol, to);
		this.productionRules.add(0, newRule);

		this.startSymbol = this.newStartSymbol;
	}

	public ProductionRule getIncrementedRule() {
		return this.productionRules.get(0);
	}
}
