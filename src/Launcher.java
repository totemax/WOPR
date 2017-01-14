import game.GameController;

public class Launcher {

	public static void main(String[] args) {
		double[] pesos1 = new double[27];
		double[] pesos2 = new double[27];
		for (int i = 0; i < 27; i++){
			pesos1[i] = Math.random();
			pesos2[i] = Math.random();
		}
		GameController cont = new GameController(pesos1, pesos2);
		cont.startGame();
		System.out.println(cont.player1Result());
		System.out.println(cont.player2Result());

	}

}
