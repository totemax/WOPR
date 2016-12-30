package game;

import game.entities.MapLocation;

public class GameController {

	private static final Integer NUM_ROUNDS = 6; // N�mero de turnos por partida.
	private static final Integer NUM_SILOS = 6; // N�mero de silos por jugador.
	private static final Integer NUM_CITIES = 6; // N�mero de ciudades por jugador.
	private static final Integer MAP_WIDTH = 6; // Ancho del mapa por cada jugador.
	private static final Integer MAP_HEIGHT = 6; // Altura del mapa por cada jugador.

	private Boolean isFinished = false;

	/**
	 * Constructor.
	 * 
	 * En este constructor se deber�n incorporar los descriptores de cada
	 * jugador.
	 */
	public GameController() {

	}

	/**
	 * M�todo encargado de empezar el juego y realizar las iteraciones.
	 */
	public void startGame() {

	}

	/**
	 * M�todo que se encargar� de devolver los resultados del juego.
	 * 
	 * @return Integer (por poner algo) en un futuro se podr� poner una relaci�n
	 *         descriptor - puntuaci�n
	 */
	public Integer getReport() {
		return 0;
	}
	
	
}
