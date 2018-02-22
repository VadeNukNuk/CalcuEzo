package app;

public class NoeudOperation {

	static double num1;
	static double num2;
	static char operation;

	public NoeudOperation(double pNum2, double pNum1, char pOperation) {
		num1 = pNum1;
		num2 = pNum2;
		operation = pOperation;
	}

	public static String operation(NoeudOperation noeud) {
		switch (operation) {
		case '+':
			return ""+(num1 + num2);
		case '-':
			return ""+ (num1 - num2);
		case '*':
			return ""+ (num1 * num2);
		case '/':
			return ""+(num1 / num2);
		case '%':
			return ""+(num1 % num2);
		case '^':
			return ""+(Math.pow(num1, num2));
		case 's':
			return ""+(Math.sqrt(num1));
		default:
			return ""+0;
		}
	}

}
