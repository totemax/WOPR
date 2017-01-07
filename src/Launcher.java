import game.GameController;

public class Launcher {

	public static void main(String[] args) {
		Integer[] pesos1 = new Integer[5];
		GameController cont = new GameController(pesos1, pesos1);
		cont.startGame();
		System.out.println(cont.player1Result());
		System.out.println(cont.player2Result());

	}

}
