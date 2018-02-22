package app;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
//import java.util.Scanner;
import java.util.regex.Matcher;

public class Calculatrice extends TextReformat implements EnumReferences {

	private static Deque<String> fileNPR = new ArrayDeque<String>();
	private static Deque<String> operationStack = new ArrayDeque<String>();
	private static Deque<String> expression = new ArrayDeque<String>();

	/**
	 * Traite un string de calcul et imprime en console la réponse dudit calcul
	 *
	 * @param inputUser
	 *            - saisie contenant le calcul qui doit être traité
	 * @return String contenant le calcul et sa réponse
	 */
	public static String analyzeInput(String inputUser) {
		String retour = "";

		// vérification rapide qu'il ne s'agit pas d'un calcul illégal
		if (!isDivByZero(inputUser)) {

			// nettoyage pour préparer le string vers le format qu'on
			// veut pouvoir interpréter pour le traitment du calcul.
			String cleanerInput = traitementTexte(inputUser);

			// si l'input a survécu au nettoyage, on peut ENFIN commencer a
			// le passer a la moulinette pour se diriger vers un format
			// traitable en Shunting Yard -> NPR.
			if (cleanerInput.length() > 0) {

				// segmentation de notre input vers une collection
				convertToExpressionDeque(cleanerInput, expression);

				// DEBUT DU reordering vers une NPR par methode "SHUNTING YARD"
				while (!expression.isEmpty()) {
					String current = expression.pop();

					// gestion d'un possible entier negatif
					// seul le cas d'un calcul, ou d'une parenthèse, qui
					// commence par un entier négatif demande un traitement
					// spécifique pour la solution que j'ai adopté
					if ((fileNPR.isEmpty() && operationStack.isEmpty() && current.equalsIgnoreCase("-"))
							|| (!operationStack.isEmpty() && L_BRACKETS.contains(operationStack.peek())
									&& current.equalsIgnoreCase("-"))) {
						fileNPR.add("0");
						fileNPR.add(expression.pop());
						fileNPR.add(current);
						current = expression.pop();
					}

					// L'algo SHUNTING YARD classique commence ici
					// si notre token est un nombre, on l'ajoute à la
					// file de traitement
					if (Character.isDigit(current.charAt(0))) {
						fileNPR.add(current);
					}

					// si le token est un opérateur:
					if (OPERATEURS.contains(current)) {

						// obtenir son niveau de priorité
						int precedence;
						precedence = getPriorite(current);

						// tant que
						// l'opérateur sur le stack d'opérateur à précédence ou
						// l'opérateur sur le stack d'opérateur est de priorité
						// égale et qu'il est associé vers la gauche (exposant)
						// ET
						// l'opérateur sur le stack n'est pas
						// une parenthèse ouvrante
						while (!operationStack.isEmpty()
								&& (getPriorite(operationStack.peek()) > precedence
										|| (getPriorite(operationStack.peek()) == precedence
												&& ("^").contains(operationStack.peek())))
								&& (!L_BRACKETS.contains(operationStack.peek()))) {
							fileNPR.add(operationStack.pop());
						}

						// autrement, il est mit sur le stack
						operationStack.push(current);
					}

					// si le token est une parenthèse ouvrante
					if (L_BRACKETS.contains(current)) {

						// on la met sur le stack d'opérations
						operationStack.push(current);
					}

					// si c'est une parenthèse fermante
					if (R_BRACKETS.contains(current)) {

						// tant que le dessus de la pile d'opération n'est pas
						// une ouvrante
						while (!operationStack.isEmpty() && !L_BRACKETS.contains(operationStack.peek())) {
							// on pop les opérateurs et les ajoutes à la file
							// NPR
							fileNPR.add(operationStack.pop());
						}

						// sinon, on sort cette parenthèse du stack
						operationStack.pop();
					}
				}

				// lorsqu'il n'y a plus de token dans notre expression
				// tant que le stack d'opération n'est pas vide
				while (!operationStack.isEmpty()) {
					// on pop les opérateurs et les ajoutes à la file NPR
					fileNPR.add(operationStack.pop());
				}

				// FIN DU SHUNTING YARD
				// notre fileNPR est maintenant prête à être traitée
				retour += traiteCalcul(fileNPR);

				// comme mon traitement de la file ne la vide pas
				// je le fait ici
				fileNPR.clear();

				System.out.println(inputUser + " = " + retour);
			} else {
				retour = (inputUser.length() == 0) ? "retour vide, vérifier syntaxe" : "erreur";
				return retour;
			}

		}
		return retour + "/n";
	}

	/**
	 * Divise en segments de texte, par le biais d'un regex, avec un critère qui
	 * permet de regrouper des chars qui représentait un nombre float. Les
	 * groupes sont ensuite ajoutés à la Deque demandée.
	 *
	 * @param input
	 *            - string devant être divisé
	 * @param pExpression
	 *            - Deque qui devra contenir les groupes
	 */
	// Idéalement cette méthode serait capable de recevoir
	// comme deuxième paramètre tout type de Collection
	private static void convertToExpressionDeque(String input, Deque<String> pExpression) {
		Matcher matcher = PATTERN_TOKENS.matcher(input);

		while (matcher.find()) {
			pExpression.add(matcher.group(0));
		}
	}

	/**
	 * Valide que le texte ne demande pas une division illégale.
	 *
	 * @param input
	 *            - string qui sera validé
	 * @return true si une division par 0 explicite est trouvé (ou si le string
	 *         est vide), false autrement
	 */
	private static boolean isDivByZero(String input) {
		boolean isDivBy0 = false;
		if (input.length() > 0)
			if (input.matches(".*\\/.*")) {
				for (int i = 0; i <= input.lastIndexOf('/'); i++) {
					if (input.charAt(i) == '/' && input.charAt(i + 1) == '0') {
						isDivBy0 = true;
						System.out.println(input + " division par 0!");
						break;
					}
				}
			}
		return isDivBy0;
	}

	/**
	 * Identifie le degré de priorité d'un symbole d'opération
	 *
	 * @param s
	 *            - le symbole à évaluer
	 * @return son niveau de priorité
	 */
	public static int getPriorite(String s) {
		int priorite = -1;
		for (int i = 0; i < PRIORITE.length; i++) {
			if (PRIORITE[i].contains(s)) {
				priorite = i;
			}
		}
		return priorite;
	}

	/**
	 * La file est analysée et traitée en segment (noeud) d'opération. La
	 * réponse finale est ensuite arrondie à deux nombre après le point décimal,
	 * et retournée sous forme de String
	 *
	 * @param pNpr
	 *            - file contenant une notation polonaise renversée
	 * @return String résultant du calcul demandé
	 */
	public static String traiteCalcul(Deque<String> pNpr) {
		ArrayDeque<String> nombres = new ArrayDeque<String>();
		while (!pNpr.isEmpty()) {
			String token = pNpr.pop();
			// les tokens sont sortis et mis dans un stack de nombre
			// jusqu'à ce qu'on trouve un opérateur
			// on prend ensuite les deux premier nombres qui se trouve dans le
			// stack et on traite ce noeud
			if (OPERATEURS.contains(token)) {
				String resolvedKnot = NoeudOperation.operation(new NoeudOperation(Double.parseDouble(nombres.pop()),
						Double.parseDouble(nombres.pop()), token.charAt(0))).toString();
				nombres.push(resolvedKnot);
			} else {
				nombres.push(token);
			}
		}
		// lorsque la file NPR est vide, le seul item dans notre stack de nombre
		// est la solution de notre calcul

		String reponse = new BigDecimal(nombres.pop()).setScale(1, BigDecimal.ROUND_HALF_EVEN).toPlainString();
		//reponse = (reponse.charAt(reponse.length()) == 0) ? reponse : reponse .substring(0, reponse.length() - 2);
		return reponse;
	}

	public static void main(String[] args) {
		// Scanner sc = new Scanner(System.in);
		// String in = "";
		// System.out.println("saisir calcul:");
		// in = sc.nextLine();
		// lireInput(in);
		// sc.close();
	}
}
