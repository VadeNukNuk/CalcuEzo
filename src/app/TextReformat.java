package app;

import java.util.ArrayDeque;
import java.util.Deque;

public class TextReformat {
	/**
	 * Le String reçu est analysé et épuré en considérant un traitement
	 * mathématique futur. Des simplifications d'opérateurs et une vérification
	 * des parenthèses est faite.
	 *
	 * @param inputUtilisateur
	 *            - le string texte qui doit être nettoyé
	 * @return un string vide si la syntaxe mathématique est erroné, ou un
	 *         string simplifié prêt pour un traitement mathématique
	 */
	public static String traitementTexte(String inputUtilisateur) {
		String cleanUp = "";

		// j'ai récupéré et simplifié mon algo d'analyse syntaxe d'un ancien TP
		// Si notre syntaxe est bonne, on passera en mode nettoyage
		if (syntaxeOk(inputUtilisateur)) {
			// changement de sqrt en char operateur
			cleanUp = inputUtilisateur.replaceAll("sqrt", "s");
			// on simplifie les expressions qui peuvent l'être
			cleanUp = cleanUp.replaceAll("--", "+");
			cleanUp = cleanUp.replaceAll("\\+-", "-");
			cleanUp = cleanUp.replaceAll("-\\+", "-");
			//on retire les espaces vides
			cleanUp = cleanUp.replaceAll(" ", "");
			// la virugule mal aimée par java est aussi nettoyée
			// j'assume ici qu'aucun séparateur de miliers,
			// autre que des espaces, n'est utilisé
			cleanUp = cleanUp.replaceAll(",", "\\.");
		}
		return cleanUp;
	}

	/**
	 * Méthode qui s'assure que la syntaxe des parenthèses dans notre texte et
	 * correcte et que chaque parenthèse ouvrante rencontre sa fermante, ainsi
	 * que l'ordre dans lequel elles sont soit correct.
	 *
	 * @param s
	 *            - String que l'on analyse
	 * @return false si il y a erreur dans la syntaxe des parenthèses, true dans
	 *         les autres cas
	 */
	public static boolean syntaxeOk(String s) {

		Deque<Character> syntaxStack = new ArrayDeque<Character>();

		// on passe par tous les caractères du string mais seule les parenthèses
		// seront réellement traitées.
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			// on met les ouvrantes sur un stack
			if (c == '{' || c == '(' || c == '[') {
				syntaxStack.push(c);
			}

			// si on croise un fermante, on vérifie le stack
			if (c == '}' || c == ')' || c == ']') {
				if (syntaxStack.isEmpty()) {
					// le stack est vide, donc syntaxe erronée! on retourne
					// false et on sort de la méthode
					return false;
				}
				// l'ouvrante est toujours 1 ou 2 de moins que la fermante en
				// ASCII
				if (syntaxStack.peek() == c - 2 || syntaxStack.peek() == c - 1) {
					// si les deux sont égale, on peut sortir celle qui est dans
					// le stack et notre "current" sera aussi débarquée lors de
					// notre prochaine itération
					syntaxStack.pop();
				}

			}
		}
		// si notre stack est vide à la fin de l'itération, on a true.
		// sinon false.
		return syntaxStack.isEmpty();
	}

}
