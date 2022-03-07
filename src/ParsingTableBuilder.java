import java.util.Vector;
import java.util.HashMap;

public class ParsingTableBuilder {
	private IncrementedSyntax incrementedSyntax;

	public ParsingTableBuilder(IncrementedSyntax incrementedSyntax) {
		this.incrementedSyntax = incrementedSyntax;
	}

	public ParsingTable buildSLR() {
		LRSet lrSet = new LRSet(this.incrementedSyntax, 0);

		ParsingTable ret = new ParsingTable(this.incrementedSyntax.getSymbols(), this.incrementedSyntax.getRules());

		for(int itemSetIndex=0; itemSetIndex<lrSet.size(); itemSetIndex++) {
			ItemSet itemSet = lrSet.getItemSet(itemSetIndex);
			HashMap<Symbol, Action> act = new HashMap<Symbol, Action>();

			// append shifting rules
			for(Symbol gotoSymbol: itemSet.getGotoSymbols()) {
				Action action;
				if(gotoSymbol.isTerminal()) {
					action = new Action("shift", itemSet.gotoFunction(gotoSymbol));
				} else {
					action = new Action("goto", itemSet.gotoFunction(gotoSymbol));
				}
				act.put(gotoSymbol, action);
			}

			// find reducing rule
			for(Item item: itemSet) {
				Core core = item.getCore();
				if(!core.getAfterDot().isEmpty()) {
					continue;
				}

				ProductionRule productionRule = null;
				int productionRuleIndex = -1;

				// find coresponding production rule
				Vector<ProductionRule> productionRules = this.incrementedSyntax.getRules();
				for(int i=0; i<productionRules.size(); i++) {
					ProductionRule pr = productionRules.get(i);
					if(core.getFrom().equals(pr.getFrom()) && core.getBeforeDot().size() == 0 && core.getAfterDot().size() == 0 && pr.getTo().get(0).isEpsilon()) {
						productionRule = pr;
						productionRuleIndex = i;
						break;
					}
					if(core.getFrom().equals(pr.getFrom()) && core.getBeforeDot().equals(pr.getTo())) {
						productionRule = pr;
						productionRuleIndex = i;
						break;
					}
				}

				if(productionRule == null) {
					throw new IllegalArgumentException("no coresponding rule for core");
				}

				// append reducing rules
				for(Symbol follow: core.getFrom().getFollow()) {
					if(act.get(follow) != null) {
						throw new IllegalArgumentException("crash during building parsing table");
					}
					if(productionRuleIndex == 0) {
							act.put(follow, new Action("accept", productionRuleIndex));
					} else {
						act.put(follow, new Action("reduce", productionRuleIndex));
					}
				}
			}
			ret.appendAct(act);
		}

		return ret;
	}

	public ParsingTable buildCLR() {
		LRSet lrSet = new LRSet(this.incrementedSyntax, 1);

		ParsingTable ret = new ParsingTable(this.incrementedSyntax.getSymbols(), this.incrementedSyntax.getRules());

		for(int itemSetIndex=0; itemSetIndex<lrSet.size(); itemSetIndex++) {
			ItemSet itemSet = lrSet.getItemSet(itemSetIndex);
			HashMap<Symbol, Action> act = new HashMap<Symbol, Action>();

			for(Symbol gotoSymbol: itemSet.getGotoSymbols()) {
				Action action;
				if(gotoSymbol.isTerminal()) {
					action = new Action("shift", itemSet.gotoFunction(gotoSymbol));
				} else {
					action = new Action("goto", itemSet.gotoFunction(gotoSymbol));
				}
				act.put(gotoSymbol, action);
			}

			// find reducing rule
			for(Item item: itemSet) {
				Core core = item.getCore();
				Vector<Symbol> lookaheads = item.getLookahead();
				if(!core.getAfterDot().isEmpty()) {
					continue;
				}

				ProductionRule productionRule = null;
				int productionRuleIndex = -1;

				// find coresponding production rule
				Vector<ProductionRule> productionRules = this.incrementedSyntax.getRules();
				for(int i=0; i<productionRules.size(); i++) {
					ProductionRule pr = productionRules.get(i);
					if(core.getFrom().equals(pr.getFrom()) && core.getBeforeDot().size() == 0 && core.getAfterDot().size() == 0 && pr.getTo().get(0).isEpsilon()) {
						productionRule = pr;
						productionRuleIndex = i;
						break;
					}
					if(core.getFrom().equals(pr.getFrom()) && core.getBeforeDot().equals(pr.getTo())) {
						productionRule = pr;
						productionRuleIndex = i;
						break;
					}
				}
				if(productionRule == null) {
					throw new IllegalArgumentException("no coresponding rule for core");
				}

				// append reducing rules
				for(Symbol lookahead: lookaheads) {
					if(act.get(lookahead) != null) {
						throw new IllegalArgumentException("crash during building parsing table");
					}
					if(productionRuleIndex == 0) {
							act.put(lookahead, new Action("accept", productionRuleIndex));
					} else {
						act.put(lookahead, new Action("reduce", productionRuleIndex));
					}
				}
			}
			ret.appendAct(act);
		}

		return ret;
	}

	public ParsingTable buildLALR() {
		LRSet lrSet = new LRSet(this.incrementedSyntax, 0);

		// TODO : apply lookahead

		ParsingTable ret = new ParsingTable(this.incrementedSyntax.getSymbols(), this.incrementedSyntax.getRules());

		return ret;
	}

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("first argument should be SLR or CLR or LALR");
			System.out.println("second argument should be a syntax file");
			System.out.println("third argument should be a filename to store parsing table");
			System.out.println("third argument is not a requirement");
			System.out.println("if fourth argument is given as 'DEBUG' then LR SET will be printed");
			System.out.println("fourth argument is not a requirement");
			return;
		}

		if(!(args[0].equals("SLR") || args[0].equals("CLR") || args[0].equals("LALR"))) {
			System.out.println("first argument should be SLR or CLR or LALR");
			return;
		}

		if(args[0].equals("LALR")) {
			System.out.println("LALR is not implemented yet... sorry!");
			return;
		}

		Syntax syntax = new Syntax(args[1]);
		IncrementedSyntax incrementedSyntax = new IncrementedSyntax(syntax);

		incrementedSyntax.calcFirst();
		incrementedSyntax.calcFollow();

		ParsingTableBuilder builder = new ParsingTableBuilder(incrementedSyntax);

		ParsingTable table = null;
		if(args[0].equals("SLR")) {
			table = builder.buildSLR();
			if(args.length >= 4 && args[3].equals("DEBUG")) {
				incrementedSyntax.printFollows();
				System.out.println();
				(new LRSet(incrementedSyntax, 0)).print();
			}
		}
		if(args[0].equals("CLR")) {
			table = builder.buildCLR();
			if(args.length >= 4 && args[3].equals("DEBUG")) {
				(new LRSet(incrementedSyntax, 1)).print();
			}
		}
		if(args[0].equals("LALR")) {
			table = builder.buildLALR();
		}

		table.print();

		if(args.length >= 3) {
			table.storeTo(args[2]);
		}
	}
}
