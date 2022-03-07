import java.util.Vector;
import java.util.List;

public class Parser {
	private ParsingTable parsingTable;

	public Parser(ParsingTable parsingTable) {
		this.parsingTable = parsingTable;
	}

	public ParseTree parse(Sentence sentence, boolean debug) {
		Vector<StackElement> stack = new Vector<StackElement>();
		Vector<ParseTree> treeStack = new Vector<ParseTree>();

		stack.add(new StackElement(0));
		treeStack.add(null);

		int ip = 0;

		do {
			if(debug) {
				for(StackElement stackElement: stack) {
					if(stackElement.getSymbol() == null) {
						System.out.print(stackElement.getState());
					} else {
						System.out.print(stackElement.getSymbol().getSymbol());
					}
					System.out.print(" ");
				}
				System.out.println();

				for(String token: sentence.subList(ip)) {
					System.out.print(token);
					System.out.print(" ");
				}
				System.out.println();

				System.out.println();
			}

			StackElement s = stack.get(stack.size()-1);
			Symbol a = this.parsingTable.findSymbol(sentence.get(ip));

			if(a == null) {
				if(debug) {
					System.out.println(sentence.get(ip) + " is undefined symbol");
				}
				break;
			}

			Action action = this.parsingTable.getAction(s.getState(), a);

			if(action == null) {
				if(debug) {
					System.out.print("action [");
					System.out.print(s.getState());
					System.out.println(", " + a.getSymbol() + "] is not defined");
				}
				break;
			}

			if(action.getAction().equals("shift")) {
				stack.add(new StackElement(a));
				treeStack.add(new ParseTree(a));
				stack.add(new StackElement(action.getNum()));
				treeStack.add(null);
				ip += 1;
			} else if(action.getAction().equals("reduce")) {
				// TODO
				ProductionRule productionRule = parsingTable.getRule(action.getNum());
				Vector<ParseTree> childrun = new Vector<ParseTree>();

				// popping elements out from stack
				for(Symbol symbol: productionRule.getTo()) {
					stack.remove(stack.size()-1);
					treeStack.remove(treeStack.size()-1);
					stack.remove(stack.size()-1);
					childrun.add(0, treeStack.get(treeStack.size()-1));
					treeStack.remove(treeStack.size()-1);
				}

				Symbol A = productionRule.getFrom();
				stack.add(new StackElement(A));
				treeStack.add(new ParseTree(A, childrun));

				StackElement s2 = stack.get(stack.size()-2);
				Action gotoAction = this.parsingTable.getAction(s2.getState(), A);

				if(gotoAction == null) {
					if(debug) {
						System.out.print("GOTO[");
						System.out.print(s2.getState());
						System.out.println(", " + A.getSymbol() + "] is not defined");
					}
					break;
				}

				stack.add(new StackElement(gotoAction.getNum()));
				treeStack.add(null);

			} else if(action.getAction().equals("accept")) {
				return treeStack.get(treeStack.size()-2);
			} else {
				if(debug) {
					System.out.println(action.getAction() + " is undefined action");
				}
				break;
			}

		} while(ip < sentence.size() && stack.size() != 0);

		return null;
	}

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("first argument should a parsing table file");
			System.out.println("second argument should be a sentence file");
			System.out.println("if third argument is given as 'DEBUG' then stack state will be printed");
			System.out.println("third argument is not a requirement");
			return;
		}

		Parser parser = new Parser(new ParsingTable(args[0]));
		Sentence sentence = new Sentence(args[1]);

		ParseTree parseTree = null;
		if(args.length >= 3) {
			parseTree = parser.parse(sentence, true);
		} else {
			parseTree = parser.parse(sentence, false);
		}
		if(parseTree != null) {
			parseTree.print();
		}
	}
}
