public class Action {
	private String action;
	private int num;

	public Action(String action, int num) {
		this.action = action;
		this.num = num;
	}

	public String getAction() {
		return this.action;
	}

	public int getNum() {
		return this.num;
	}

	public void print() {
		if(this.action.equals("accept")) {
			System.out.print("acc");
		} else {
			System.out.print(this.action.substring(0,1));
			System.out.print(this.num);
		}
	}

	public String toString() {
		String ret = "";
		ret += this.action;
		ret += " ";
		ret += this.num;
		return ret;
	}
}
