package game.entities;

/**
 * Descriptor de la localizacion de cada ciudad
 */
public class City extends MapLocation {
	private final static Integer CITY_POPULATION = 50000; // Poblacion inicial
															// de la ciudad

	/**
	 * Constructor.
	 */
	public City(Integer x, Integer y) {
		super(CITY_POPULATION, x, y);
	}

}
