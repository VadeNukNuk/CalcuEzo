package app;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculatrice {

	public static final String REGEX_TOKENS = "(\\d+\\.\\d+|\\d+|\\D)";
	public static final Pattern PATTERN_TOKENS = Pattern.compile(REGEX_TOKENS, Pattern.MULTILINE);
	public static final String OPERATEURS = "+-*/^s";
	public static final String[] PRIORITE = { "(){}[]", "+-", "/*", "^s" };
	public static final String L_BRACKETS = "[{(";
	public static final String R_BRACKETS = "]})";

	private static Deque<String> fileNPR = new ArrayDeque<String>();
	private static Deque<String> expression;

	/**
	 * Traite un string de calcul et imprime en console la réponse dudit calcul
	 *
	 * @param inputUser
	 *            - saisie contenant le calcul qui doit être traité
	 */
	public static void analyzeInput(String inputUser) {
		expression = new ArrayDeque<String>();
		String retour = "";

		// nettoyage pour préparer le string vers le format qu'on
		// veut pouvoir interpréter pour le traitment du calcul.
		String cleanerInput = TextReformat.traitementTexte(inputUser);

		// si l'input a survécu au nettoyage, on peut commencer
		// le traitement de l'information qu'il contient
		if (cleanerInput.length() > 0) {

			// segmentation de notre input vers une collection
			expression = convertToExpressionDeque(cleanerInput);

			// reorganisation pour atteindre la logique de traitement
			fileNPR = shuntingYardReordering(expression);

			// notre fileNPR est maintenant prête à être traitée
			retour += traiteCalcul(fileNPR);

			System.out.println(inputUser + " = " + retour);
		}
	}

	/**
	 * Divise en segments de texte, par le biais d'un regex, avec un critère qui
	 * permet de regrouper des chars qui représentait un nombre float. Testera
	 * ensuite qu'il n'y a pas de division par 0 explicite avant de retourner la
	 * Deque demandée.
	 *
	 * @param input
	 *            - string devant être divisé
	 * @return Deque qui contient les groupes trouvés
	 */
	private static Deque<String> convertToExpressionDeque(String input) {
		Deque<String> expr = new ArrayDeque<String>();
		Matcher matcher = PATTERN_TOKENS.matcher(input);

		while (matcher.find()) {
			expr.add(matcher.group(0));
		}

		// Je teste la / par 0 ici, me permettant d'accepter
		// les valeurs < 1 mais > 0.
		if (expr.contains("/")) {
			Iterator<String> it = expr.iterator();
			while (it.hasNext()) {
				String testZero = it.next();
				if (testZero.equals("/")) {
					testZero = it.next();
					if (testZero.contains("0")) {
						if (Double.parseDouble(testZero) == 0) {
							System.out.println(input + " = erreur");
							System.exit(0);
						}
					} else {
						continue;
					}
				}
			}
		}
		return expr;
	}

	/**
	 * Réorganise une expression mathématique afin de la représenter en notation
	 * polonaise renversée.
	 *
	 * @param pExpression
	 *            - array de "mots" composant l'expression mathématique
	 * @return Deque contenant son équivalent en notation polonaise renversée
	 */
	public static Deque<String> shuntingYardReordering(Deque<String> pExpression) {
		Deque<String> npr = new ArrayDeque<String>();
		Deque<String> operationStack = new ArrayDeque<String>();
		// DEBUT DU reordering vers une NPR par methode "SHUNTING YARD"
		while (!pExpression.isEmpty()) {
			String current = pExpression.pop();

			// gestion d'un possible entier negatif
			// seul le cas d'un calcul, ou d'une parenthèse, qui
			// commence par un entier négatif demande un traitement
			// spécifique pour la solution que j'ai adopté
			if (npr.isEmpty() && operationStack.isEmpty() && current.equalsIgnoreCase("-")) {
				npr.add("0");
				npr.add(pExpression.pop());
				npr.add(current);
				current = pExpression.pop();
			}

			// si le token est une parenthèse ouvrante
			if (L_BRACKETS.contains(current)) {
				// on la met sur le stack d'opérations
				operationStack.push(current);

				// on vérifie si un entier négatif suivra
				if (pExpression.peek().contains("-")) {
					// sortir son "opérateur"
					String negatif = pExpression.pop();
					// gestion comme hors parenthèses
					npr.add("0");
					npr.add(pExpression.pop());
					npr.add(negatif);
					current = pExpression.pop();
				}
			}

			// L'algo SHUNTING YARD classique commence ici
			// si notre token est un nombre, on l'ajoute à la
			// file de traitement
			if (Character.isDigit(current.charAt(0))) {
				npr.add(current);
			}

			// si le token est un opérateur:
			if (OPERATEURS.contains(current)) {

				// obtenir son niveau de priorité
				int precedence = getPriorite(current);

				// tant que
				// l'opérateur sur le stack d'opérateur à précédence ou
				// l'opérateur sur le stack d'opérateur est de priorité
				// égale et qu'il est associé vers la gauche (exposant)
				// ET
				// l'opérateur sur le stack n'est pas
				// une parenthèse ouvrante
				while (!operationStack.isEmpty() && (getPriorite(operationStack.peek()) > precedence
						|| (getPriorite(operationStack.peek()) == precedence && ("^").contains(operationStack.peek())))
						&& (!L_BRACKETS.contains(operationStack.peek()))) {
					npr.add(operationStack.pop());
				}

				// autrement, il est mit sur le stack
				operationStack.push(current);
			}

			// si c'est une parenthèse fermante
			if (R_BRACKETS.contains(current)) {

				// tant que le dessus de la pile d'opération n'est pas
				// une parenthèse ouvrante
				while (!operationStack.isEmpty() && !L_BRACKETS.contains(operationStack.peek())) {
					// on pop les opérateurs et les ajoutes
					// à la file de notation polonaise renversée
					npr.add(operationStack.pop());
				}

				// sinon, on sort cette parenthèse du stack
				operationStack.pop();
			}
		}

		// lorsqu'il n'y a plus de token dans notre expression
		// tant que le stack d'opération n'est pas vide
		while (!operationStack.isEmpty())

		{
			// on pop les opérateurs et les ajoutes à la file NPR
			npr.add(operationStack.pop());
		}
		// FIN DU SHUNTING YARD
		return npr;
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
		String reponse = new DecimalFormat("#.##").format(Double.parseDouble(nombres.pop()));

		return reponse;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String in = "";
		System.out.println("saisir calcul:");
		in = sc.nextLine();
		analyzeInput(in);
		sc.close();
	}
}
