package game.entities;

/**
 * Clase abstracta de cada localización del mapa del juego.
 */
public abstract class MapLocation {

	public Integer population; // Población actual en esta localización
	public Boolean tainted = false; // Población contaminada
	private Integer x; // X coords
	private Integer y; // Y coords

	/**
	 * Constructor.
	 * 
	 * @param population
	 *            población inicial de esa localización.
	 */
	public MapLocation(Integer population, Integer x, Integer y) {
		this.x = x;
		this.y = y;
		this.population = population;
	}

	/**
	 * Destrucción de la localización
	 */
	public void destroyLocation() {
		this.population = 0;
	}
	
	/**
	 * Cálculo de la distancia entre dos localizaciones.
	 * @param otherLocation la otra localizacion
	 * @return distancia entre los dos puntos
	 */
	public double distance(MapLocation otherLocation){
		int xDist = Math.abs(this.x - otherLocation.x);
		int yDist = Math.abs(this.y - otherLocation.y);
		
		return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
	}

}
