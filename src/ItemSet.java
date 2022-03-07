import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;

public class ItemSet implements Iterable<Item> {
	private Vector<Item> items;
	private HashMap<Symbol, Integer> gotoMap;

	public ItemSet() {
		this.items = new Vector<Item>();
		this.gotoMap = new HashMap<Symbol, Integer>();
	}

	public boolean equals(ItemSet i2) {
		if(this.size() != i2.size()) {
			return false;
		}

		for(Item item: this.items) {
			boolean tmp = false;
			for(Item item2: i2) {
				if(item.equals(item2)) {
					tmp = true;
					break;
				}
			}
			if(tmp == false) {
				return false;
			}
		}

		return true;
	}

	public boolean contains(Item item) {
		for(Item i: this.items) {
			if(i.isCoreEquals(item)) {
				return true;
			}
		}
		return false;
	}

	public Item get(Item item) {
		for(Item i: this.items) {
			if(i.isCoreEquals(item)) {
				return i;
			}
		}
		return null;
	}

	public Item get(int i) {
		return this.items.get(i);
	}

	public void add(Item item) {
		this.items.add(item);
	}

	public int size() {
		return this.items.size();
	}

	public Iterator<Item> iterator() {
		return this.items.iterator();
	}

	public void linkTo(Symbol symbol, int to) {
		this.gotoMap.put(symbol, to);
	}

	public int gotoFunction(Symbol symbol) {
		return this.gotoMap.get(symbol);
	}

	public Vector<Symbol> getGotoSymbols() {
		Vector<Symbol> ret = new Vector<Symbol>();
		for(Item item: this.items) {
			Vector<Symbol> afterDot = item.getCore().getAfterDot();
			if(afterDot.size() > 0 && (!ret.contains(afterDot.get(0)))) {
				ret.add(afterDot.get(0));
			}
		}

		return ret;
	}

	public void print() {
		for(Item item: this.items) {
			item.print();
		}
	}
}
