package game;



import java.util.ArrayList;
import java.util.List;

import game.entities.City;
import game.entities.MapLocation;
import game.entities.PlayerMovement;
import game.entities.Silo;

public class PlayerController {

	private MapLocation[] playerLocations; // Mapa del jugador
	Integer[] weights;

	/**
	 * Constructor
	 */
	public PlayerController(Integer[] weights, MapLocation[] playerLocations) {
		this.playerLocations = playerLocations;
		this.weights = weights;
	}

	public MapLocation[] getPlayerLocations() {
		return this.playerLocations;
	}

	public void recharge(int numMissiles, MapLocation[] opponentLocations) {
		for (MapLocation loc : this.playerLocations) {
			if (!loc.isDestroyed() && loc.getClass().equals(Silo.class) && ((Silo) loc).getCharge(opponentLocations)) {
				// Aqui ira el controlador borroso de carga general
				// Lo hago aleatorio para probar
				double rand = Math.random();
				if (rand > 0.7)
					((Silo) loc).recharge();
			}
		}
	}

	public List<PlayerMovement> resolveMovement(MapLocation[] opponentLocations) {
		List<PlayerMovement> movements = new ArrayList<>();
		for(MapLocation loc : this.playerLocations){
			if (loc.getClass().equals(Silo.class)){
				Silo siloLoc = (Silo)loc;
				// Aqui deberá ir el controlador borroso que seleccionara quienes disparan
				PlayerMovement mov = siloLoc.getDisparo(opponentLocations);
				if (mov != null){
					double rand = Math.random();
					if (rand > 0.8){
						movements.add(mov);
					}
				}
			}
		}
		return movements;
	}
	
	public Integer getPopulation(){
		Integer pop = 0;
		for(MapLocation loc : this.getPlayerLocations()){
			pop += loc.population;
		}
		return pop;
	}
	
	public Integer getNumSilos(){
		Integer silos = 0;
		for (MapLocation loc : this.playerLocations){
			if (loc.getClass().equals(Silo.class) && !loc.isDestroyed()){
				silos++;
			}
		}
		return silos;
	}
	
	public Integer getNumMissiles(){
		Integer missiles = 0;
		for (MapLocation loc: this.playerLocations){
			if (loc.getClass().equals(Silo.class)){
				missiles += ((Silo)loc).getNumMissiles();
			}
		}
		return missiles;
	}
	
	public Integer getNumCities(){
		Integer numCities = 0;
		for (MapLocation loc : this.playerLocations){
			if (loc.getClass().equals(City.class) && !loc.isDestroyed()){
				numCities++;
			}
		}
		return numCities;
	}

}
