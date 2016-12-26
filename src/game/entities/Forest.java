package game.entities;

/**
 * Descripci�n de la ubicaci�n de mapa de bosque.
 */
public class Forest extends MapLocation {

	private static final Integer FOREST_POPULATION = 5000; // Poblaci�n inicial
															// del bosque

	/**
	 * Constructor.
	 */
	public Forest(Integer x, Integer y) {
		super(FOREST_POPULATION, x, y);
	}
}
