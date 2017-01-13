package game.entities;

/**
 * Descripción de la ubicación de mapa de bosque.
 */
public class Forest extends MapLocation {

	public static final Integer FOREST_POPULATION = 5000; // Poblaci�n inicial
															// del bosque

	/**
	 * Constructor.
	 */
	public Forest(Integer x, Integer y) {
		super(FOREST_POPULATION, x, y);
	}
}
