import java.util.Vector;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class Sentence implements Iterable<String> {
	private Vector<String> tokens;

	public Sentence(String filename) {
		this.tokens = new Vector<String>();
		String temp = null;

		try {
			byte[] encoded = Files.readAllBytes(Paths.get(filename));
			temp = new String(encoded, StandardCharsets.UTF_8);
		} catch(IOException e) {
			e.printStackTrace();
		}

		temp = temp.replaceAll("\n", " ").replaceAll("\t", " ");

		while(temp.indexOf("  ") != -1) {
			temp = temp.replaceAll("  ", " ");
		}

		Collections.addAll(tokens, temp.split(" "));

		this.tokens.add("$");
	}

	public Iterator<String> iterator() {
		return this.tokens.iterator();
	}

	public String get(int i) {
		return this.tokens.get(i);
	}

	public int size() {
		return this.tokens.size();
	}

	public List<String> subList(int fromIndex) {
		return this.tokens.subList(fromIndex, this.tokens.size());
	}


	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("you should give me a Sentence file as a argument");
			return;
		}

		Sentence sentence = new Sentence(args[0]);

		for(String token: sentence) {
			System.out.print(token);
			System.out.print(" ");
		}
		System.out.println();
	}
}
