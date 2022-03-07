import java.util.Vector;
import java.util.HashMap;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;

public class ParsingTable {
	private Vector<Symbol> symbols;
	private Vector<ProductionRule> productionRules;
	private Vector<HashMap<Symbol, Action>> table;

	public ParsingTable(Vector<Symbol> symbols, Vector<ProductionRule> productionRules) {
		this.symbols = symbols;
		this.productionRules = productionRules;
		this.table = new Vector<HashMap<Symbol, Action>>();
	}

	public void appendAct(HashMap<Symbol, Action> act) {
		this.table.add(act);
	}

	public ProductionRule getRule(int i) {
		return this.productionRules.get(i);
	}

	public Action getAction(int state, Symbol symbol) {
		return this.table.get(state).get(symbol);
	}

	public Symbol findSymbol(String symbol) {
		for(Symbol s: this.symbols) {
			if(s.getSymbol().equals(symbol)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * file structure
	 *
	 * number of symbol
	 * symbols ...
	 * number of production rule
	 * productin rules...
	 * number of statement
	 * actions ...
	 */

	public ParsingTable(String filename) {
		this.symbols = new Vector<Symbol>();
		this.productionRules = new Vector<ProductionRule>();
		this.table = new Vector<HashMap<Symbol, Action>>();

		try {
			BufferedReader file = new BufferedReader(new FileReader(filename));

			// add symbols
			int numSymbol = Integer.parseInt(file.readLine());
			for(int i=0; i<numSymbol; i++) {
				Symbol symbol = new Symbol(file.readLine());
				this.symbols.add(symbol);
			}

			//for(Symbol symbol: this.symbols) {
			//	System.out.println(symbol.getSymbol());
			//}

			// add production rules
			int numRule = Integer.parseInt(file.readLine());
			for(int i=0; i<numRule; i++) {
				String line = file.readLine();
				String[] spiltLine = line.split(" ");

				if(spiltLine.length < 3) {
					throw new IllegalArgumentException("wrong production rule");
				}
				if(!spiltLine[1].equals("==>")) {
					throw new IllegalArgumentException("wrong production rule symbol");
				}

				Symbol from = this.findSymbol(spiltLine[0]);
				if(from == null) {
					throw new IllegalArgumentException("no symbol " + spiltLine[0] + " in production rule");
				}

				Vector<Symbol> to = new Vector<Symbol>();
				for(int j=2; j<spiltLine.length; j++) {
					Symbol t = this.findSymbol(spiltLine[j]);
					if(t == null) {
						throw new IllegalArgumentException("no symbol " + spiltLine[j] + " in production rule");
					}
					to.add(t);
				}

				ProductionRule productionRule = new ProductionRule(from, to);
				this.productionRules.add(productionRule);
			}

			// add table
			int tableLength = Integer.parseInt(file.readLine());
			for(int i=0; i<tableLength; i++) {
				HashMap<Symbol, Action> act = new HashMap<Symbol, Action>();

				for(Symbol symbol: this.symbols) {
					String line = file.readLine();
					if(line.equals("")) {
						continue;
					}

					String[] spiltLine = line.split(" ");
					if(spiltLine.length != 2) {
						throw new IllegalArgumentException("wrong action value");
					}

					Action action = new Action(spiltLine[0], Integer.parseInt(spiltLine[1]));
					act.put(symbol, action);
				}

				this.table.add(act);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void storeTo(String filename) {
		try {
			PrintStream file = new PrintStream(new FileOutputStream(filename));

			file.println(this.symbols.size());
			for(Symbol symbol: this.symbols) {
				file.println(symbol.getSymbol());
			}

			file.println(this.productionRules.size());
			for(ProductionRule productionRule: this.productionRules) {
				file.println(productionRule.toString());
			}

			file.println(this.table.size());
			for(HashMap<Symbol, Action> act: table) {
				for(Symbol symbol: this.symbols) {
					Action action = act.get(symbol);
					if(action == null) {
						file.println();
					} else {
						file.println(action.toString());
					}
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void print() {
		for(Symbol symbol: this.symbols) {
			System.out.print("\t");
			System.out.print(symbol.getSymbol());
		}
		System.out.println();

		for(int i=0; i<this.table.size(); i++) {
			System.out.print(i);
			for(Symbol symbol: this.symbols) {
				System.out.print("\t");
				Action action = this.table.get(i).get(symbol);
				if(action != null) {
					action.print();
				}
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("you should give me a table file as a argument");
			return;
		}

		ParsingTable table = new ParsingTable(args[0]);

		table.print();
	}
}
