package game.entities;

/**
 * Clase abstracta de cada localizacion del mapa del juego.
 */
public abstract class MapLocation {

	private Integer population; // Poblacion actual en esta localizacion
	private Integer x; // X coords
	private Integer y; // Y coords
	protected Boolean destroyed = false; // Localizacion destruida

	/**
	 * Constructor.
	 * 
	 * @param population
	 *            poblacion inicial de esa localizacion.
	 */
	public MapLocation(Integer population, Integer x, Integer y) {
		this.x = x;
		this.y = y;
		this.population = population;
	}

	/**
	 * Destruccion de la localizacion
	 */
	public void destroyLocation() {
		this.population = 0;
		this.destroyed = true;
	}

	/**
	 * Calculo de la distancia entre dos localizaciones.
	 * 
	 * @param otherLocation
	 *            la otra localizacion
	 * @return distancia entre los dos puntos
	 */
	public double distance(MapLocation otherLocation) {
		int xDist = Math.abs(this.x - otherLocation.x);
		int yDist = Math.abs(this.y - otherLocation.y);

		return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
	}

	public double getAverageDistanceToSilos(MapLocation[] locations) {
		double distances = 0;
		int numSilos = 0;
		for (MapLocation loc : locations) {
			if (loc.getClass().equals(Silo.class) && !((Silo) loc).isDestroyed()) {
				distances += this.distance(loc);
				numSilos++;
			}
		}
		if (numSilos > 0) {
			return distances / numSilos;
		} else {
			return Double.MAX_VALUE; // En caso de que no haya silos.
		}
	}

	public static Integer getMapPopulation(MapLocation[] map) {
		Integer population = 0;
		for (MapLocation loc : map) {
			if (!loc.destroyed) {
				population += loc.getPopulation();
			}
		}
		return population;
	}

	/**
	 * Indica si la localizacion ha sido destruida
	 * 
	 * @return true / false
	 */
	public Boolean isDestroyed() {
		return this.destroyed;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getPopulation() {
		return this.population;
	}

}
