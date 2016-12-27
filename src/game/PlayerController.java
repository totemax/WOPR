package game;

import game.entities.MapLocation;
import game.entities.PlayerMovement;

public class PlayerController {

	private MapLocation[] playerLocations; // Lista de las localizaciones del
											// mapa del jugador

	private MapLocation[] opponentLocations; // Lista de localizaciones del mapa
												// del oponente.

	/**
	 * Constructor
	 */
	public PlayerController(MapLocation[] playerLocations, MapLocation[] opponentLocations) {
		this.playerLocations = playerLocations;
		this.opponentLocations = opponentLocations;
	}

	/**
	 * Funcion de refresco de los mapas antes de cada turno
	 * 
	 * @param playerLocations
	 * @param opponentLocations
	 */
	public void refreshMaps(MapLocation[] playerLocations, MapLocation[] opponentLocations) {
		this.playerLocations = playerLocations;
		this.opponentLocations = opponentLocations;
	}

	/**
	 * Resuelve el turno
	 * 
	 * @return Array de movimientos que realizará el jugador
	 */
	public PlayerMovement[] resolveRound() {
		return null;
	}
}
