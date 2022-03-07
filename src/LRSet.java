import java.util.Vector;

public class LRSet {
	private IncrementedSyntax incrementedSyntax;
	private Vector<ItemSet> itemSets;
	private int lookaheadLevel;

	public LRSet(IncrementedSyntax syntax, int level) {
		this.incrementedSyntax = syntax;
		this.itemSets = new Vector<ItemSet>();
		this.lookaheadLevel = level;

		ItemSet itemset = new ItemSet();

		if(level != 0 && level != 1) {
			throw new IllegalArgumentException("level must be zero or one");
		}

		ProductionRule incrementedRule = this.incrementedSyntax.getIncrementedRule();

		Symbol from = incrementedRule.getFrom();
		Vector<Symbol> beforeDot = new Vector<Symbol>();
		Vector<Symbol> afterDot = new Vector<Symbol>(incrementedRule.getTo());
		Core baseCore = new Core(from, beforeDot, afterDot);
		Item baseItem;

		if(level == 0) {
		 	baseItem = new Item(baseCore);
		} else {
			Vector<Symbol> dollar = new Vector<Symbol>();
			dollar.add(syntax.getDollar());
			baseItem = new Item(baseCore, dollar);
		}

		Vector<Item> baseItems = new Vector<Item>();
		baseItems.add(baseItem);

		ItemSet itemSet0 = this.closure(baseItems);

		this.appendItemSet(itemSet0);

		// main loop
		for(int cur=0; cur<this.size(); cur++) {
			ItemSet curItemSet = this.getItemSet(cur);
			Vector<Symbol> gotoSymbols = curItemSet.getGotoSymbols();

			for(Symbol gotoSymbol: gotoSymbols) {
				ItemSet newItemSet = this.gotoFunction(curItemSet, gotoSymbol);
				int next;

				if(this.contains(newItemSet)) {
					next = this.getIndex(newItemSet);
					newItemSet = this.getItemSet(next);
				} else {
					next = this.appendItemSet(newItemSet);
				}

				this.setMapping(cur, gotoSymbol, next);
			}
		}
	}

	public ItemSet closure(Vector<Item> items) {
		ItemSet ret = new ItemSet();
		boolean hasModified = true;
		Vector<ProductionRule> productionRules = incrementedSyntax.getRules();

		for(Item item: items) {
			ret.add(item);
		}

		while(hasModified) {
			hasModified = false;

			for(int i=0; i<ret.size(); i++) {
				Item item = ret.get(i);
				Core core = item.getCore();
				Vector<Symbol> afterDot = core.getAfterDot();
				if(afterDot.size() > 0 && afterDot.get(0).isNonterminal()) {
					for(ProductionRule rule: productionRules) {
						if(rule.getFrom().equals(afterDot.get(0))) {
							Vector<Symbol> empty;
							Vector<Symbol> newAfterDot;
							Core newCore;
							Item newItem;

							// append new item
							switch(this.lookaheadLevel) {
							case 0:
								empty = new Vector<Symbol>();
								newAfterDot = new Vector<Symbol>(rule.getTo());
								newCore = new Core(rule.getFrom(), empty, newAfterDot);
								newItem = new Item(newCore);
								if(!ret.contains(newItem)) {
									ret.add(newItem);
									hasModified = true;
								}
							break;
							case 1:
								for(int j=0; j<item.getLookahead().size(); j++) {
									Symbol a = item.getLookahead().get(j);
									empty = new Vector<Symbol>();
									newAfterDot = new Vector<Symbol>(rule.getTo());
									newCore = new Core(rule.getFrom(), empty, newAfterDot);

									Vector<Symbol> betaA = new Vector<Symbol>(afterDot);
									betaA.remove(0); // beta
									betaA.add(a);
									Vector<Symbol> b = this.incrementedSyntax.first(betaA);

									newItem = new Item(newCore, b);
									if(ret.contains(newItem)) {
										Item originalItem = ret.get(newItem);
										for(Symbol l: newItem.getLookahead()) {
											if(!originalItem.contains(l)) {
												originalItem.appendLookahead(l);
												hasModified = true;
											}
										}
									} else {
										ret.add(newItem);
										hasModified = true;
									}
								}
							break;
							}

						}
					}
				}
			}
		}

		return ret;
	}

	public ItemSet gotoFunction(ItemSet itemSet, Symbol symbol) {
		Vector<Item> applied = new Vector<Item>();

		for(Item item: itemSet) {
			Core oldCore = item.getCore();
			Vector<Symbol> oldBeforeDot = oldCore.getBeforeDot();
			Vector<Symbol> oldAfterDot = oldCore.getAfterDot();
			if(oldAfterDot.size() > 0 && oldAfterDot.get(0).equals(symbol)) {
				Vector<Symbol> newBeforeDot = new Vector<Symbol>(oldBeforeDot);
				Vector<Symbol> newAfterDot = new Vector<Symbol>(oldAfterDot);
				newBeforeDot.add(newAfterDot.get(0));
				newAfterDot.remove(0);

				Core newCore = new Core(oldCore.getFrom(), newBeforeDot, newAfterDot);
				applied.add(new Item(newCore, new Vector<Symbol>(item.getLookahead())));
			}
		}

		return this.closure(applied);
	}

	public int size() {
		return this.itemSets.size();
	}

	public boolean contains(ItemSet itemSet) {
		for(ItemSet is: this.itemSets) {
			if(is.equals(itemSet)) {
				return true;
			}
		}
		return false;
	}

	public int getIndex(ItemSet itemSet) {
		for(int i=0; i<this.itemSets.size(); i++) {
			if(this.itemSets.get(i).equals(itemSet)) {
				return i;
			}
		}
		return -1;
	}

	public ItemSet getItemSet(int i) {
		return this.itemSets.get(i);
	}

	public int appendItemSet(ItemSet itemSet) {
		this.itemSets.add(itemSet);
		return this.itemSets.size()-1;
	}

	public void setMapping(int from, Symbol by, int to) {
		this.itemSets.get(from).linkTo(by, to);
	}

	public void print() {
		for(int i=0; i<this.itemSets.size(); i++) {
			System.out.print("I");
			System.out.println(i);
			this.itemSets.get(i).print();
			System.out.println();
		}
	}

	// debug code to check equality
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("first argument should be syntax file");
			return;
		}

		Syntax syntax = new Syntax(args[0]);
		IncrementedSyntax incrementedSyntax = new IncrementedSyntax(syntax);

		incrementedSyntax.calcFirst();
		incrementedSyntax.calcFollow();

		LRSet lrSet = new LRSet(incrementedSyntax, 0);

		ProductionRule incrementedRule = incrementedSyntax.getIncrementedRule();

		Symbol from = incrementedRule.getFrom();
		Vector<Symbol> beforeDot = new Vector<Symbol>();
		Vector<Symbol> afterDot = new Vector<Symbol>(incrementedRule.getTo());
		Core baseCore = new Core(from, beforeDot, afterDot);
		Item baseItem = new Item(baseCore);

		Vector<Item> baseItems = new Vector<Item>();
		baseItems.add(baseItem);

		ItemSet itemSet0 = lrSet.closure(baseItems);
		ItemSet itemSet1 = lrSet.closure(baseItems);
		//lrSet.appendItemSet(itemSet0);

		/*
		if(lrSet.contains(itemSet1)) {
			System.out.println("equality OK");
		} else {
			System.out.println("equality error");
		}
		*/

		lrSet.print();
	}
}
