package app;

public class NoeudOperation {

	private static double num1;
	private static double num2;
	private static char operation;

	/**
	 * Objet qui contient les informations pertinante d'un calcul que l'on
	 * voudra effectuer. Le constructeur considère que l'ordre des parametres
	 * reçu est celui qu'une notation polonaise renversée lui fournirait.
	 *
	 * @param pNum2
	 *            - deuxième nombre
	 * @param pNum1
	 *            - premier nombre
	 * @param pOperation
	 *            - opérateur
	 */
	public NoeudOperation(double pNum2, double pNum1, char pOperation) {
		num1 = pNum1;
		num2 = pNum2;
		operation = pOperation;
	}

	/**
	 * Utilise les attributs du noeud qui l'appel et traite l'operation que ce
	 * noeud contient.
	 *
	 * @param noeud
	 *            - le noeud que l'on traite
	 * @return Double qui représente le résultat de l'opération
	 */
	public static Double operation(NoeudOperation noeud) {
		switch (operation) {
		case '+':
			return num1 + num2;
		case '-':
			return num1 - num2;
		case '*':
			return num1 * num2;
		case '/':
			return num1 / num2;
		case '%':
			return num1 % num2;
		case '^':
			return Math.pow(num1, num2);
		case 's':
			return Math.sqrt(num2);
		default:
			return 0.0;
		}
	}

}
