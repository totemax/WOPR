package game.entities;

/**
 * Clase descriptora del silo.
 */
public class Silo extends MapLocation {

	private static final Integer SILO_POPULATION = 50; // Población inicial del
														// silo.

	private Integer missiles; // Misiles del silo

	private Boolean destroyed = false; // Silo destruido

	public Silo(Integer missiles, Integer x, Integer y) {
		super(SILO_POPULATION, x, y);
		this.missiles = missiles;
	}

	/**
	 * Añade un misil al silo.
	 */
	public void addMissile() {
		this.missiles++;
	}

	/**
	 * Quita un misil del silo.
	 */
	public void launchMissile() {
		this.missiles--;
	}

	public void destroyLocation() {
		super.destroyLocation();
		this.missiles = 0;
		this.destroyed = true; // Queda inutilizado
	}

	/**
	 * Indica si el silo ha sido destruido.
	 * 
	 * @return true / false
	 */
	public Boolean isDestroyed() {
		return this.destroyed;
	}
}
