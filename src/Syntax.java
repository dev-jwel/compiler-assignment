import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.Arrays;
import java.util.List;

public class Syntax {
	protected Symbol startSymbol;
	protected Symbol dollar;
	protected Vector<Symbol> symbols;
	protected Vector<ProductionRule> productionRules;

	protected Symbol getSymbol(String symbol) {
		for(Symbol s: this.symbols) {
			if(s.getSymbol().equals(symbol)) {
				return s;
			}
		}
		return null;
	}

	public Vector<Symbol> getSymbols() {
		return this.symbols;
	}

	public Vector<ProductionRule> getRules() {
		return this.productionRules;
	}

	public Symbol getDollar() {
		return this.dollar;
	}

	private Symbol getAndAddSymbol(String symbol) {
		Symbol s = this.getSymbol(symbol);
		if(s == null) {
			s = new Symbol(symbol);
			this.symbols.add(s);
		}
		return s;
	}

	protected Syntax() {}

	public Syntax(String filename) {
		this.symbols = new Vector<Symbol>();
		this.productionRules = new Vector<ProductionRule>();

		BufferedReader reader;

		try {
			String line;
			Symbol prevSymbol = null;
			reader = new BufferedReader(new FileReader(filename));

			line = reader.readLine();
			while(line != null) {
				if(line.charAt(0) == '\t') {
					if(prevSymbol == null) {
						throw new IllegalArgumentException("tab is not allowed at first line");
					}

					// delete tab charactor and split by space
					line = line.substring(1);
					String[] spiltLine = line.split(" ");

					if(!spiltLine[0].equals("||")) {
						throw new IllegalArgumentException("'||' is required after tab");
					}

					// remove '||' at first
					spiltLine = Arrays.copyOfRange(spiltLine, 1, spiltLine.length);

					// check '||' string and '$' string
					Vector<Integer> orPositions = new Vector<Integer>();
					for(int i=0; i<spiltLine.length; i++) {
						if(spiltLine[i].equals("$")) {
							throw new IllegalArgumentException("'$' is not allowed");
						}

						if(spiltLine[i].equals("||")) {
							orPositions.add(i);
						}
					}

					// consider as there is one more '||' symbol at end
					orPositions.add(spiltLine.length);

					// parse spiltLine into productionRules
					int start = 0;
					for(int orPos: orPositions) {
						Vector<Symbol> to = new Vector<Symbol>();

						for(int i=start; i<orPos; i++) {
							String s = spiltLine[i];
							if(s.equals("\\|\\|")) {
								s = "||";
							}
							to.add(this.getAndAddSymbol(s));
						}

						this.productionRules.add(new ProductionRule(prevSymbol, to));
						start = orPos + 1;
					}

				} else {
					// split by space end check ==> notation and check '$' string
					String[] spiltLine = line.split(" ");
					if(spiltLine[0].equals("$")) {
						throw new IllegalArgumentException("'$' is not allowed");
					}
					if(!spiltLine[1].equals("==>")) {
						throw new IllegalArgumentException("second notation must be '==>'");
					}

					// check '||' string and '$' string
					Vector<Integer> orPositions = new Vector<Integer>();
					for(int i=2; i<spiltLine.length; i++) {
						if(spiltLine[i].equals("$")) {
							throw new IllegalArgumentException("'$' is not allowed");
						}
						if(spiltLine[i].equals("||")) {
							orPositions.add(i);
						}
					}

					// consider as there is one more '||' symbol at end
					orPositions.add(spiltLine.length);

					// parse spiltLine into productionRules
					Symbol from = this.getAndAddSymbol(spiltLine[0]);
					prevSymbol = from;
					int start = 2;
					for(int orPos: orPositions) {
						Vector<Symbol> to = new Vector<Symbol>();

						for(int i=start; i<orPos; i++) {
							String s = spiltLine[i];
							if(s.equals("\\|\\|")) {
								s = "||";
							}
							to.add(this.getAndAddSymbol(s));
						}

						this.productionRules.add(new ProductionRule(from, to));
						start = orPos + 1;
					}
				}

				line = reader.readLine();
			}

		} catch(IOException e) {
			e.printStackTrace();
		}

		this.startSymbol = this.symbols.get(0);
		this.dollar = new Symbol("$");
		this.symbols.add(dollar);
	}

	public void printSymbols() {
		for(Symbol s: this.symbols) {
			System.out.println(s.getSymbol());
		}
	}

	public void printRules() {
		for(ProductionRule pr: this.productionRules) {
			pr.print();
		}
	}

	// this method should be called after first is calculated
	private boolean canDeriveEpsilon(Symbol symbol) {
		if(symbol.isTerminal()) {
			return false;
		} else if (symbol.isEpsilon()) {
			return true;
		} else {
			Symbol epsilon = null;
			for(Symbol s: this.symbols) {
				if(s.getSymbol().equals("__")) {
					epsilon = s;
					break;
				}
			}
			if(epsilon == null) {
				return false;
			} else {
				return symbol.getFirst().contains(epsilon);
			}
		}
	}

	// this method should be called after first is calculated
	private boolean canDeriveEpsilon(Vector<Symbol> symbols) {
		for(Symbol symbol: symbols) {
			if(!canDeriveEpsilon(symbol)) {
				return false;
			}
		}
		return true;
	}

	// calculate first with ringsum
	public Vector<Symbol> first(Vector<Symbol> symbols) {
		Vector<Symbol> ret = new Vector<Symbol>();

		for(Symbol symbol:symbols) {
			if(symbol.isEpsilon()) {
				throw new IllegalArgumentException("internel error");
			}
			if(symbol.isTerminal()) {
				ret.add(symbol);
				break;
			}
			if(symbol.isNonterminal()) {
				boolean containEpsilon = false;
				for(Symbol f: symbol.getFirst()) {
					if(f.isEpsilon()) {
						containEpsilon = true;
						continue;
					}
					if(!ret.contains(f)) {
						ret.add(f);
					}
				}
				if(!containEpsilon) {
					break;
				}
			}
		}

		return ret;
	}

	public void calcFirst() {
		for(Symbol symbol: this.symbols) {
			// rule 1
			if(symbol.isTerminal() || symbol.isEpsilon()) {
				symbol.appendFirst(symbol);
			}
		}

		// rule 2 and rule 3
		for(Symbol symbol: this.symbols) {
			if(symbol.isNonterminal()) {
				for(ProductionRule pr: this.productionRules) {
					if(pr.getFrom() == symbol) {
						Symbol firstSymbol = pr.getTo().get(0);
						// rule 2 and rule 3
						if(firstSymbol.isTerminal() || firstSymbol.isEpsilon()) {
							symbol.appendFirst(firstSymbol);
						}

					}
				}
			}
		}

		// rule 4
		boolean isUpdated = false;
		do {
			isUpdated = false;

			// for all symbol and for production rule which starts from symbol
			for(Symbol symbol: this.symbols) {
				for(ProductionRule pr: this.productionRules) {
					if(pr.getFrom() == symbol) {

						// append all first if right hand side starts with nonterminal symbol
						Symbol firstSymbol = pr.getTo().get(0);
						if(firstSymbol.isNonterminal()) {
							for(Symbol fs: this.first(pr.getTo())) {
								if(symbol.appendFirst(fs)) {
									isUpdated = true;
								}
							}
						}

					}
				}
			}
		} while(isUpdated);
	}

	public void calcFollow() {
		// rule 1
		this.startSymbol.appendFollow(this.dollar);

		// rule 2
		for(ProductionRule pr: this.productionRules) {
			Vector<Symbol> rightHandSide = pr.getTo();
			for(int i=0; i<rightHandSide.size()-1; i++) {
				Symbol A = rightHandSide.get(i);
				if(A.isNonterminal()) {
					List<Symbol> betaList = rightHandSide.subList(i+1, rightHandSide.size());
					Vector<Symbol> beta = new Vector<Symbol>(betaList);

					for(Symbol first: this.first(beta)) {
						if(first.isNonterminal()) {
							throw new IllegalArgumentException(
							"internel error: first is nonterminal"
							);
						}
						if(!first.isEpsilon()) {
							A.appendFollow(first);
						}
					}
				}
			}
		}

		// rule 3
		boolean isUpdated = false;
		do {
			isUpdated = false;

			for (ProductionRule pr: this.productionRules) {
				Symbol B = pr.getFrom();
				Vector<Symbol> rightHandSide = pr.getTo();
				for(int i=0; i<rightHandSide.size(); i++) {
					Symbol A = rightHandSide.get(i);

					// if B ==> alpha A
					if(i == rightHandSide.size()-1) {
						for(Symbol f: B.getFollow()) {
							if(A.appendFollow(f)) {
								isUpdated = true;
							}
						}
					}

					// else if B ==> alpha A beta and beta =*=> epsilon
					else {
						List<Symbol> betaList = rightHandSide.subList(i+1, rightHandSide.size());
						Vector<Symbol> beta = new Vector<Symbol>(betaList);
						if(this.canDeriveEpsilon(beta)) {
							for(Symbol f: B.getFollow()) {
								if(A.appendFollow(f)) {
									isUpdated = true;
								}
							}
						}
					}
				}
			}

		} while(isUpdated);
	}

	public void printFirsts() {
		for(Symbol symbol: this.symbols) {
			if(symbol.isNonterminal()) {
				System.out.print("FIRST(");
				System.out.print(symbol.getSymbol());
				System.out.print(") = {");

				Vector<Symbol> firsts = symbol.getFirst();
				for(int i=0; i<firsts.size()-1; i++) {
					System.out.print(firsts.get(i).getSymbol());
					System.out.print(", ");
				}
				if(!firsts.isEmpty()) {
					System.out.print(firsts.lastElement().getSymbol());
				}

				System.out.println("}");
			}
 		}
	}

	public void printFollows() {
		for(Symbol symbol: this.symbols) {
			if(symbol.isNonterminal()) {
				System.out.print("FOLLOW(");
				System.out.print(symbol.getSymbol());
				System.out.print(") = {");

				Vector<Symbol> follows = symbol.getFollow();
				for(int i=0; i<follows.size()-1; i++) {
					System.out.print(follows.get(i).getSymbol());
					System.out.print(", ");
				}
				if(!follows.isEmpty()) {
					System.out.print(follows.lastElement().getSymbol());
				}

				System.out.println("}");
			}
		}
	}

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("you should give me a syntax file as a argument");
			return;
		}

		Syntax syntax = new Syntax(args[0]);

		if(args.length >= 2 && args[1].equals("DEBUG")) {
			syntax.printSymbols();
			System.out.println();
			syntax.printRules();
			System.out.println();
		}

		syntax.calcFirst();
		syntax.printFirsts();
		System.out.println();
		syntax.calcFollow();
		syntax.printFollows();
	}
}
