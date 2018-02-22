package app;

import java.util.ArrayDeque;
import java.util.Deque;

public class TextHandling {

	public static String traitementTexte(String inputUtilisateur) {
		String cleanUp = "";

		// j'ai récupéré et simplifié mon algo d'analyse syntaxe d'un ancien TP
		// Si notre syntaxe est bonne, on passera en mode nettoyage
		if (syntaxeOk(inputUtilisateur)) {
			// changement de sqrt en char operateur
			cleanUp = inputUtilisateur.replaceAll("sqrt", "s");
			// je simplifie ensuite les expressions qui peuvent l'être
			cleanUp = cleanUp.replaceAll("--", "+");
			cleanUp = cleanUp.replaceAll("\\+-", "-");
			cleanUp = cleanUp.replaceAll("-\\+", "-");
			// maudite virugule mal aimée
			cleanUp = cleanUp.replaceAll(",", "\\.");
		}

		return cleanUp;
	}

	public static boolean syntaxeOk(String s) {

		Deque<Character> syntaxStack = new ArrayDeque<Character>();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == '{' || c == '(' || c == '[') {
				syntaxStack.push(c);
			}

			if (c == '}' || c == ')' || c == ']') {
				if (syntaxStack.isEmpty()) {
					return false;
				}

				if (syntaxStack.peek() == c - 2 || syntaxStack.peek() == c - 1) {
					syntaxStack.pop();
				}

			}
		}

		return syntaxStack.isEmpty();
	}

	public static void main(String[] args) {
		traitementTexte("sqrt--4");
		traitementTexte("sqrt(-4^2)+4");
		traitementTexte("sqrt(-4,2^2)+4");
		traitementTexte("sqrt(-4.2^2)+4");
	}

}
