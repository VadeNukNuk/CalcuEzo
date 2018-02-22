package app;

import java.util.ArrayDeque;
import java.util.Deque;
//import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculatrice extends TextHandling {

	private static final String REGEX_TOKENS = "(\\d+\\.\\d+|\\d+|\\D)";
	private static final Pattern PATTERN_TOKENS = Pattern.compile(REGEX_TOKENS, Pattern.MULTILINE);

	private static final String OPERATEURS = "+-*/^s";

	private static final String[] PRIORITE = { "(){}[]", "+-", "/*", "^s" };
	private static final String L_BRACKETS = "[{(";
	private static final String R_BRACKETS = "]})";

	private static Deque<String> npr = new ArrayDeque<String>();
	private static Deque<String> operationStack = new ArrayDeque<String>();

	private static Deque<String> expression = new ArrayDeque<String>();

	private static String analyzeInput(String inputUser) {
		String retour = "";

		if (!isDivByZero(inputUser)) {

			String cleanerInput = traitementTexte(inputUser);
		//	System.out.println(cleanerInput);

			// si l<input a surv/cu au nettoyage, on peut ENFIN commencer a
			// le passer a la moulinette pour se diriger vers un format
			// traitable

			if (cleanerInput.length() > 0) {
				Matcher matcher = PATTERN_TOKENS.matcher(cleanerInput);

				/*
				 * transfert vers une ArrayDeque qui tiendra les éléments String
				 * pour les rendre traitable par le Shunting Yard algorithm
				 */
				while (matcher.find()) {
					expression.add(matcher.group(0));
				}

			//	System.out.println(expression);

				/*
				 * debut du reordering avec la methode shunting yard
				 */
				while (!expression.isEmpty()) {
					String current = expression.pop();

					// gestion d'un possible symbole entier negatif
					if ((npr.isEmpty() && operationStack.isEmpty() && current.equalsIgnoreCase("-"))
							|| (!operationStack.isEmpty() && L_BRACKETS.contains(operationStack.peek())
									&& current.equalsIgnoreCase("-"))

					) {
						npr.add("0");
						npr.add(expression.pop());
						npr.add(current);
						current = expression.pop();
					}

					// SHUNTING YARD ALGO STARTS HERE

					// if the token is a number, then push it to the output
					// queue.
					if (Character.isDigit(current.charAt(0))) {
						npr.add(current);
					}

					// if the token is an operator, then:
					if (OPERATEURS.contains(current)) {
						int precedence;
						precedence = getPriorite(current);

						// while
						while (
						// (
						(
						// (there is an operator at the top of the operator
						// stack with greater precedence)
						(!operationStack.isEmpty() && getPriorite(operationStack.peek()) > precedence)
								// or
								||
								// (the operator at the top of the operator
								// stack has equal precedence //and// the
								// operator is left associative)
								(!operationStack.isEmpty() && getPriorite(operationStack.peek()) == precedence
										&& ("^").contains(operationStack.peek()))

						// )
						)

								// and
								&&

								// (the operator at the top of the stack is not
								// a left bracket)
								(!operationStack.isEmpty() && !L_BRACKETS.contains(operationStack.peek()))

						)

						{

							npr.add(operationStack.pop());
						}
						operationStack.push(current);
					}

					// if the token is a left bracket (i.e. "("), then:
					if (L_BRACKETS.contains(current)) {

						// push it onto the operator stack.
						operationStack.push(current);

					}

					// if the token is a right bracket (i.e. ")"), then:
					if (R_BRACKETS.contains(current)) {

						// while the operator at the top of the operator stack
						// is not a left bracket:
						while (!operationStack.isEmpty() && !L_BRACKETS.contains(operationStack.peek())) {
							// pop operators from the operator stack onto the
							// output queue.
							npr.add(operationStack.pop());
						}

						// pop the left bracket from the stack.
						operationStack.pop();

					}

				}

				// if there are no more tokens to read:

				// while there are still operator tokens on the stack:
				while (!operationStack.isEmpty()) {
					// pop the operator onto the output queue.
					npr.add(operationStack.pop());
				}

			//	System.out.println(npr);

				// NPR CALCUL TIME HERE MOUHAHAHA

				// TO-DO T'ES RENDU ICI
				// QUICK NOTE: DEQUE -> ADD pour "append" et PUSH pour add
				// @index 0
				// QUICK NOTE: DEQUE -> PUSH pour devant de la file
				// QUICK NOTE: DEQUE -> PEEK pour voir le premier sur le tas
				// (@index 0)
				retour += traiteCalcul(npr);
				npr.clear();
				System.out.println(inputUser + " = " + retour);
			} else {
				retour = (inputUser.length() == 0) ? "retour vide, vérifier syntaxe" : "erreur";
				return retour;
			}

		}
		return retour + "/n";
	}

	/*
	 * simplification de l'array et traitement style RPN avec shunting-yard
	 */
//	private static String charArrayHandler(String inputToEval) {
//		// I ended up doing that in the lireInput...
//		// will have to bring it back into here once done
//		return "";
//	}

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

	public static int getPriorite(String s) {
		int priorite = -1;
		for (int i = 0; i < PRIORITE.length; i++) {
			if (PRIORITE[i].contains(s)) {
				priorite = i;
			}
		}
		return priorite;
	}

	public static String traiteCalcul(Deque<String> npr2) {
		ArrayDeque<String> nombres = new ArrayDeque<String>();
		// for each token in the postfix expression:
		while (!npr2.isEmpty()) {
			String token = npr2.pop();
			// if token is an operator:
			if (OPERATEURS.contains(token)) {
				// operand_2 ← pop from the stack // operand_1 ← pop from the
				// stack
				// result ← evaluate token with operand_1 and operand_2
				String resolve = NoeudOperation.operation(new NoeudOperation(Double.parseDouble(nombres.pop()),
						Double.parseDouble(nombres.pop()), token.charAt(0)));
				// push result back onto the stack
				nombres.push(resolve);
				// else if token is an operand:
			} else {
				// push token onto the stack
				nombres.push(token);
			}
			// result ← pop from the stack

		}
		return nombres.pop();
	}

	public static void main(String[] args) {
		// Scanner sc = new Scanner(System.in);
		// String in = "";
		// System.out.println("saisir calcul:");
		// in = sc.nextLine();
		// lireInput(in);
		// Tests et Résultats attendu

		analyzeInput("1+1"); // s/b 1? I'll assume typo and they meant 2. could
								// also be a input testing (if .matches(1+1))
		analyzeInput("1+2"); // 3
		analyzeInput("1+-1");// 0
		analyzeInput("-1--1");// 0
		analyzeInput("5-4");// 1
		analyzeInput("5*2");// 10
		analyzeInput("(2+5)*3");// 21
		analyzeInput("10/2");// 5
		analyzeInput("2+2*5+5");// 17
		analyzeInput("2.8*3-1");// 7.4
		analyzeInput("2^8");// 256
		analyzeInput("2^8*5-1");// 1279
		// lireInput("sqrt(4)");// 2
		analyzeInput("1/0");// erreur

		//test perso de mon handling des int negatif
		analyzeInput("-2+(14--5)+-24");// -7

		// sc.close();
	}
}
