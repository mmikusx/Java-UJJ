import java.util.Map;

public class Test {
	public static void main(String[] args) {
		Decrypter d = new Decrypter();
		
		String input = "|1żaVle śVa1iV, SW@g5D5>VV V zD*5g>l@1iV y@5W5]lDń, |1żaVle   śVa1iV, \t SW@g5D5>VV V zD*5g>l@1iV y@5W5]lDńv y@5W5]lDńv :gli8]";

		d.setInputText(input);

		for (Map.Entry<Character, Character> entry : d.getCode().entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue().toString());
		}

		System.out.println("--------");

		for (Map.Entry<Character, Character> entry : d.getDecode().entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue().toString());
		}
	}
}