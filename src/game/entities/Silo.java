package game.entities;

/**
 * Clase descriptora del silo.
 */
public class Silo extends MapLocation {

	private static final Integer SILO_POPULATION = 50; // Poblacion inicial del silo.
	private static final Integer MAX_MISSILES = 6;

	private Integer missiles = 0; // Misiles del silo

	public Silo(Integer x, Integer y) {
		super(SILO_POPULATION, x, y);
	}
	
	public boolean getCharge(MapLocation[] rivalLocations){
		double avgDist = this.getAverageDistanceToSilos(rivalLocations);
		// Aqui ira el controlador borroso
		//Voy a hacer una prueba con random values
		if (missiles < MAX_MISSILES && Math.random() > 0.5){
			return true;
		}
		return false;
	}
	
	
	public PlayerMovement getDisparo(MapLocation[] rivalLocations){
		// Aqui ira el controlador borroso de disparo
		if (this.missiles == 0){
			return null;
		}
		
		// Lo hago aleatorio para probar
		for (MapLocation loc : rivalLocations){
			double seed = Math.random();
			if (!loc.destroyed && seed > 0.8){
				return new PlayerMovement(this, loc);
			}
		}
		
		return null; // No se ejecuta movimiento
	}

	public void destroyLocation() {
		super.destroyLocation();
		this.missiles = 0;
		this.destroyed = true; // Queda inutilizado
	}
	
	public void recharge(){
		this.missiles++;
	}
	
	public Integer getNumMissiles(){
		return this.missiles;
	}
}
