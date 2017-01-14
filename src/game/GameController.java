package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import game.entities.City;
import game.entities.Forest;
import game.entities.MapLocation;
import game.entities.PlayerMovement;
import game.entities.Silo;

public class GameController {

	private static final Integer NUM_ROUNDS = 6; // Numero de turnos por
													// partida.
	public static final Integer NUM_SILOS = 6; // Numero de silos por jugador.
	public static final Integer NUM_CITIES = 6; // Numero de ciudades por
												// jugador.
	public static final Integer MAP_WIDTH = 6; // Ancho del mapa por cada
												// jugador.
	public static final Integer MAP_HEIGHT = 6; // Altura del mapa por cada
												// jugador.

	public static final double MAX_DISTANCE = Math.sqrt(Math.pow((MAP_WIDTH * 2), 2) + Math.pow((MAP_HEIGHT * 2), 2));

	private static final Integer MISSILE_PER_ROUND = 3;
	private static final double FAIL_MOD = 5;
	private static final Integer MAX_FAIL_PROB = 60;
	public static final Integer MAX_POPULATION_PLAYER = (NUM_SILOS * Silo.SILO_POPULATION)
			+ (NUM_CITIES * City.CITY_POPULATION)
			+ (((MAP_WIDTH * MAP_HEIGHT) - (NUM_SILOS + NUM_CITIES)) * Forest.FOREST_POPULATION);
	public static final Integer MAX_MISSILE_PLAYER = NUM_SILOS * Silo.MAX_MISSILES;

	PlayerController player1, player2;
	HashMap<PlayerController, List<PlayerMovement>> movementList = new HashMap<>();

	double[] player1Weights, player2Weights;

	/**
	 * Constructor.
	 *
	 * En este constructor se deberan incorporar los descriptores de cada
	 * jugador.
	 */
	public GameController(double[] player1Weights, double[] player2Weights) {
		this.player1Weights = player1Weights;
		this.player2Weights = player2Weights;
		MapLocation[] player1Map = this.generateMap();
		MapLocation[] player2Map = this.reflectMap(player1Map);
		this.player1 = new PlayerController(player1Weights, player1Map, this);
		this.player2 = new PlayerController(player2Weights, player2Map, this);
		movementList.put(player1, new ArrayList<>());
		movementList.put(player2, new ArrayList<>());
	}

	/**
	 * Metodo encargado de empezar el juego y realizar las iteraciones.
	 */
	public void startGame() {
		for (int i = 0; i < NUM_ROUNDS; i++) {
			this.resolveMovements(this.movementList.get(player1));
			this.player1.recharge(MISSILE_PER_ROUND, this.player2.getPlayerLocations());
			this.movementList.get(player1).addAll(this.player1.resolveMovement(this.player2.getPlayerLocations()));

			this.resolveMovements(this.movementList.get(player2));
			this.player2.recharge(MISSILE_PER_ROUND, this.player1.getPlayerLocations());
			this.movementList.get(player2).addAll(this.player2.resolveMovement(this.player1.getPlayerLocations()));
		}
	}

	public boolean underAttack(PlayerController player) {
		return this.movementList.get(player).size() > 0;
	}

	public double getFailProb(MapLocation silo, MapLocation obj) {
		double distance = silo.distance(obj);
		double failProb = distance * FAIL_MOD;
		return (failProb > MAX_FAIL_PROB) ? MAX_FAIL_PROB : failProb;
	}

	public void resolveMovements(List<PlayerMovement> movs) {
		Random rand = new Random();
		for (int i = 0; i < movs.size(); i++) {
			double failProb = this.getFailProb(movs.get(i).getFrom(), movs.get(i).getTo());
			double num = rand.nextInt(100);
			if (num > failProb) {
				movs.get(i).getTo().destroyLocation();
			}
			movs.remove(i);
		}
	}

	/**
	 * Metodo que se encargara de devolver los resultados del juego.
	 *
	 * @return Integer (por poner algo) en un futuro se podra poner una relacion
	 *         descriptor - puntuacion
	 */
	public Integer player1Result() {
		Integer totalScore = 0;
		totalScore += this.player1.getPopulation() / 100;
		totalScore += this.player1.getNumMissiles();
		totalScore += this.player1.getNumSilos();
		totalScore += this.player1.getNumCities();
		return totalScore;
	}

	public Integer player2Result() {
		Integer totalScore = 0;
		totalScore += this.player2.getPopulation() / 100;
		totalScore += this.player2.getNumMissiles();
		totalScore += this.player2.getNumSilos();
		totalScore += this.player2.getNumCities();
		return totalScore;
	}

	private MapLocation[] generateMap() {
		Random rand = new Random();
		MapLocation[][] map = new MapLocation[MAP_WIDTH][MAP_HEIGHT];
		for (int i = 0; i < NUM_SILOS; i++) {
			int x = 0;
			int y = 0;
			do {
				x = rand.nextInt(MAP_WIDTH);
				y = rand.nextInt(MAP_HEIGHT);
			} while (map[x][y] != null);
			map[x][y] = new Silo(x, y, this.player1Weights);
		}

		for (int i = 0; i < NUM_CITIES; i++) {
			int x = 0;
			int y = 0;
			do {
				x = rand.nextInt(MAP_WIDTH);
				y = rand.nextInt(MAP_HEIGHT);
			} while (map[x][y] != null);
			map[x][y] = new City(x, y);
		}

		MapLocation[] returnMap = new MapLocation[MAP_HEIGHT * MAP_WIDTH];
		int count = 0;
		for (int x = 0; x < MAP_WIDTH; x++) {
			for (int y = 0; y < MAP_HEIGHT; y++) {
				if (map[x][y] == null) {
					map[x][y] = new Forest(x, y);
				}
				returnMap[count] = map[x][y];
				count++;
			}
		}
		return returnMap;
	}

	private MapLocation[] reflectMap(MapLocation[] map) {
		MapLocation[] returnMap = new MapLocation[map.length];
		for (int i = 0; i < map.length; i++) {
			if (map[i].getClass().equals(Silo.class)) {
				returnMap[i] = new Silo((MAP_WIDTH * 2) - map[i].getX(), map[i].getY(), this.player2Weights);
			} else if (map[i].getClass().equals(City.class)) {
				returnMap[i] = new City((MAP_WIDTH * 2) - map[i].getX(), map[i].getY());
			} else {
				returnMap[i] = new Forest((MAP_WIDTH * 2) - map[i].getX(), map[i].getY());
			}
		}
		return returnMap;
	}

}
