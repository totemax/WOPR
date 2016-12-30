package game.entities;

/**
 * Clase abstracta de cada localizaci�n del mapa del juego.
 */
public abstract class MapLocation {

	public Integer population; // Poblaci�n actual en esta localizaci�n
	public Boolean tainted = false; // Poblaci�n contaminada
	private Integer x; // X coords
	private Integer y; // Y coords

	/**
	 * Constructor.
	 * 
	 * @param population
	 *            poblaci�n inicial de esa localizaci�n.
	 */
	public MapLocation(Integer population, Integer x, Integer y) {
		this.x = x;
		this.y = y;
		this.population = population;
	}

	/**
	 * Destrucci�n de la localizaci�n
	 */
	public void destroyLocation() {
		this.population = 0;
	}
	
	/**
	 * C�lculo de la distancia entre dos localizaciones.
	 * @param otherLocation la otra localizacion
	 * @return distancia entre los dos puntos
	 */
	public double distance(MapLocation otherLocation){
		int xDist = Math.abs(this.x - otherLocation.x);
		int yDist = Math.abs(this.y - otherLocation.y);
		
		return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
	}

}
