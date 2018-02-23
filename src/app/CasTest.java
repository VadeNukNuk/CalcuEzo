package app;

public class CasTest {

	public static void main(String[] args) {

		// s/b 1? I'll assume typo and they meant 2.
		Calculatrice.analyzeInput("1+1");
		// could also be a input testing (if .matches(1+1))

		Calculatrice.analyzeInput("1+2"); // 3
		Calculatrice.analyzeInput("1+-1");// 0
		Calculatrice.analyzeInput("-1--1");// 0
		Calculatrice.analyzeInput("5-4");// 1
		Calculatrice.analyzeInput("5*2");// 10
		Calculatrice.analyzeInput("(2+5)*3");// 21
		Calculatrice.analyzeInput("10/2");// 5
		Calculatrice.analyzeInput("2+2*5+5");// 17
		Calculatrice.analyzeInput("2.8*3-1");// 7.4
		Calculatrice.analyzeInput("2^8");// 256
		Calculatrice.analyzeInput("2^8*5-1");// 1279

		// test perso de mon handling des int negatif
		Calculatrice.analyzeInput("-2+(14--5)+-24");// -7

		//Cas dont l'implémentation restait à faire
		// lireInput("sqrt(4)");// 2

		Calculatrice.analyzeInput("1/0");// erreur


	}

}
