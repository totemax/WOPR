package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import game.entities.City;
import game.entities.Forest;
import game.entities.MapLocation;
import game.entities.PlayerMovement;
import game.entities.Silo;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionPieceWiseLinear;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionSingleton;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionTriangular;
import net.sourceforge.jFuzzyLogic.membership.Value;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.ruleAccumulationMethod.RuleAccumulationMethodMax;
import net.sourceforge.jFuzzyLogic.ruleActivationMethod.RuleActivationMethodMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;

public class PlayerController {

	private MapLocation[] playerLocations; // Mapa del jugador
	double[] weights;
	private GameController game;

	/**
	 * Constructor
	 */
	public PlayerController(double[] weights, MapLocation[] playerLocations, GameController game) {
		this.playerLocations = playerLocations;
		this.weights = weights;
		this.game = game;
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
		for (MapLocation loc : this.playerLocations) {
			if (loc.getClass().equals(Silo.class)) {
				Silo siloLoc = (Silo) loc;
				// Aqui deberá ir el controlador borroso que seleccionara
				// quienes disparan
				PlayerMovement mov = siloLoc.getDisparo(opponentLocations);
				if (mov != null) {
					if (this.CBDJ(mov, MapLocation.getMapPopulation(opponentLocations), Silo.getSilosInMap(opponentLocations))){
						movements.add(mov);
					}
				}
			}
		}
		return movements;
	}

	public Integer getPopulation() {
		return MapLocation.getMapPopulation(this.playerLocations);
	}

	public Integer getNumSilos() {
		return Silo.getSilosInMap(this.playerLocations);
	}

	public Integer getNumMissiles() {
		Integer missiles = 0;
		for (MapLocation loc : this.playerLocations) {
			if (loc.getClass().equals(Silo.class)) {
				missiles += ((Silo) loc).getNumMissiles();
			}
		}
		return missiles;
	}

	public Integer getNumCities() {
		Integer numCities = 0;
		for (MapLocation loc : this.playerLocations) {
			if (loc.getClass().equals(City.class) && !loc.isDestroyed()) {
				numCities++;
			}
		}
		return numCities;
	}

	public boolean CBDJ(PlayerMovement mov, Integer opponentPopulation, Integer opponentSilos) // Metodo del controlador borroso de
	{

		FIS fis = new FIS();

		FunctionBlock functionBlock = new FunctionBlock(fis);
		fis.addFunctionBlock("CBDJ", functionBlock);

		Variable bajoAtaque = new Variable("bajoAtaque"); // si el jugador se
															// encuentra o no
															// bajo ataque
		Variable miPoblacion = new Variable("miPoblacion"); // Poblacion que
															// tengo
		Variable poblacionObj = new Variable("poblacionObj"); // Poblacion del
																// enemigo
		Variable numSilosObj = new Variable("numSilosObj"); // numero de silos
															// del objetivo
		Variable miNumSilos = new Variable("miNumSilos"); // Numero de silos que
															// tengo
		Variable numMisiles = new Variable("numMisiles"); // Numero de misiles
															// que tengo
		Variable scoreDisparo = new Variable("score");

		Variable efectuarDisp = new Variable("efectuarDisp"); // si se efectua o
																// no el disparo

		/*
		 * Miembros funcionales de bajoAtaque
		 */

		MembershipFunction meAtacan = new MembershipFunctionSingleton(new Value(1));

		MembershipFunction noAtacan = new MembershipFunctionSingleton(new Value(0));

		LinguisticTerm ltmAtacan = new LinguisticTerm("Ataque", meAtacan);
		LinguisticTerm ltnAtacan = new LinguisticTerm("No Ataque", noAtacan);
		bajoAtaque.add(ltnAtacan);
		bajoAtaque.add(ltmAtacan);

		/*
		 * Miembros funcionales de miPoblacion
		 */

		Value[] popAltaX = { new Value(2 * GameController.MAX_POPULATION_PLAYER / 3),
				new Value(GameController.MAX_POPULATION_PLAYER) };
		Value[] popAltaY = { new Value(0), new Value(1) };
		MembershipFunction pmAlta = new MembershipFunctionPieceWiseLinear(popAltaX, popAltaY);
		MembershipFunction pmMedia = new MembershipFunctionTriangular(
				new Value(GameController.MAX_POPULATION_PLAYER / 3),
				new Value(GameController.MAX_POPULATION_PLAYER / 2),
				new Value((2 * GameController.MAX_POPULATION_PLAYER / 3) + 10));

		Value[] popBajaX = { new Value(GameController.MAX_POPULATION_PLAYER / 3 + 10), new Value(0) };
		Value[] popBajaY = { new Value(0), new Value(1) };
		MembershipFunction pmBaja = new MembershipFunctionPieceWiseLinear(popBajaX, popBajaY);

		LinguisticTerm ltpmAlta = new LinguisticTerm("Alta", pmAlta);
		LinguisticTerm ltpmMedia = new LinguisticTerm("Media", pmMedia);
		LinguisticTerm ltpmBaja = new LinguisticTerm("Baja", pmBaja);
		miPoblacion.add(ltpmAlta);
		miPoblacion.add(ltpmMedia);
		miPoblacion.add(ltpmBaja);

		/*
		 * Miembros funcionales de poblacionObj
		 */
		MembershipFunction pObjAlta = new MembershipFunctionSingleton(new Value(City.CITY_POPULATION));
		MembershipFunction pObjMedia = new MembershipFunctionSingleton(new Value(Forest.FOREST_POPULATION));
		MembershipFunction pObjBaja = new MembershipFunctionSingleton(new Value(Silo.SILO_POPULATION));

		LinguisticTerm ltpObjAlta = new LinguisticTerm("Alta", pObjAlta);
		LinguisticTerm ltpObjMedia = new LinguisticTerm("Media", pObjMedia);
		LinguisticTerm ltpObjBaja = new LinguisticTerm("Baja", pObjBaja);
		poblacionObj.add(ltpObjAlta);
		poblacionObj.add(ltpObjMedia);
		poblacionObj.add(ltpObjBaja);

		/*
		 * Miembros funcionales de numSilosObj
		 */
		
		Value[] silosAltosX = { new Value(2 * GameController.NUM_SILOS / 3), new Value(GameController.NUM_SILOS) };
		Value[] silosAltosY = { new Value(0), new Value(1) };

		MembershipFunction nmObjAlto = new MembershipFunctionPieceWiseLinear(silosAltosX, silosAltosY);
		MembershipFunction nmObjMedio = new MembershipFunctionTriangular(new Value(GameController.NUM_SILOS / 3),
				new Value(GameController.NUM_SILOS / 2), new Value((2 * GameController.NUM_SILOS / 3) + 1));
		
		Value[] silosBajosX = {new Value(0),new Value((GameController.NUM_SILOS / 3) + 1)};
		Value[] silosBajosY = {new Value(1), new Value(0)};
		MembershipFunction nmObjBajo = new MembershipFunctionPieceWiseLinear(silosBajosX, silosBajosY);

		LinguisticTerm ltnmObjAlto = new LinguisticTerm("Alto", nmObjAlto);
		LinguisticTerm ltnmObjMedio = new LinguisticTerm("Medio", nmObjMedio);
		LinguisticTerm ltnmObjBajo = new LinguisticTerm("Bajo", nmObjBajo);
		numSilosObj.add(ltnmObjAlto);
		numSilosObj.add(ltnmObjMedio);
		numSilosObj.add(ltnmObjBajo);

		/*
		 * Miembros funcionales de miNumSilos
		 */

		MembershipFunction mnSilosAlto = new MembershipFunctionPieceWiseLinear(silosAltosX, silosAltosY);
		MembershipFunction mnSilosMedio = new MembershipFunctionTriangular(new Value(GameController.NUM_SILOS / 3),
				new Value(GameController.NUM_SILOS / 2), new Value((2 * GameController.NUM_SILOS / 3) + 1));
		MembershipFunction mnSilosBajo = new MembershipFunctionPieceWiseLinear(silosBajosX, silosBajosY);

		LinguisticTerm ltmnSilosAlto = new LinguisticTerm("Alto", mnSilosAlto);
		LinguisticTerm ltmnSilosMedio = new LinguisticTerm("Medio", mnSilosMedio);
		LinguisticTerm ltmnSilosBajo = new LinguisticTerm("Bajo", mnSilosBajo);
		miNumSilos.add(ltmnSilosAlto);
		miNumSilos.add(ltmnSilosMedio);
		miNumSilos.add(ltmnSilosBajo);

		/*
		 * Miembros funcionales de numMisiles
		 */
		
		Value[] missileAltoX = {new Value(2*GameController.MAX_MISSILE_PLAYER / 3), new Value(GameController.MAX_MISSILE_PLAYER)};
		Value[] missileAltoY = {new Value(0), new Value(1)};

		MembershipFunction nmMissilesAlto = new MembershipFunctionPieceWiseLinear(missileAltoX, missileAltoY);
		MembershipFunction nmMissilesMedio = new MembershipFunctionTriangular(new Value(GameController.MAX_MISSILE_PLAYER / 3), new Value(GameController.MAX_MISSILE_PLAYER / 2), new Value((2*GameController.MAX_MISSILE_PLAYER/3) + 1));
		
		Value[] missilesBajoX = {new Value(0), new Value((GameController.MAX_MISSILE_PLAYER/3)+1)};
		Value[] missilesBajoY = {new Value(1), new Value(0)};
		MembershipFunction nmSilosBajo = new MembershipFunctionPieceWiseLinear(missilesBajoX, missilesBajoY);

		LinguisticTerm ltnmSilosAlto = new LinguisticTerm("Alto", nmMissilesAlto);
		LinguisticTerm ltnmSilosMedio = new LinguisticTerm("Medio", nmMissilesMedio);
		LinguisticTerm ltnmSilosBajo = new LinguisticTerm("Bajo", nmSilosBajo);
		numMisiles.add(ltnmSilosAlto);
		numMisiles.add(ltnmSilosMedio);
		numMisiles.add(ltnmSilosBajo);

		/*
		 * Miembros funcionales de efectuarDisp
		 */

		MembershipFunction eDispSi = new MembershipFunctionSingleton(new Value(1));
		MembershipFunction eDispNo = new MembershipFunctionSingleton(new Value(0));

		LinguisticTerm lteDispSi = new LinguisticTerm("Si", eDispSi);
		LinguisticTerm lteDispNo = new LinguisticTerm("No", eDispNo);
		efectuarDisp.add(lteDispSi);
		efectuarDisp.add(lteDispNo);

		/*
		 * Miembros funcionales de puntuacion de tiro
		 */

		Value[] scoreAltoX = {new Value(60), new Value(100)};
		Value[] scoreAltoY = {new Value(0), new Value(0)};
		MembershipFunction scoreAlto = new MembershipFunctionPieceWiseLinear(scoreAltoX, scoreAltoY);
		MembershipFunction scoreMedio = new MembershipFunctionTriangular(new Value(40), new Value(60), new Value(70));

		LinguisticTerm ltpScoreAlto = new LinguisticTerm("Alto", scoreAlto);
		LinguisticTerm ltpScoreMid = new LinguisticTerm("Medio", scoreMedio);
		
		scoreDisparo.add(ltpScoreAlto);
		scoreDisparo.add(ltpScoreMid);

		RuleBlock ruleBlock = new RuleBlock(functionBlock);
		ruleBlock.setName("Reglas de disparo del jugador");
		ruleBlock.setRuleAccumulationMethod(new RuleAccumulationMethodMax());
		ruleBlock.setRuleActivationMethod(new RuleActivationMethodMin());

		
		// IF NUM_MISSILES == ALTO AND BAJO_ATAQUE AND POB_OBJ == ALTA AND NUM_SILOS_OBJ == ALTO AND SCORE_DISPARO = ALTO THEN DISPARO
		Rule rule1 = new Rule("Rule1", ruleBlock);
		RuleTerm term1rule1 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule1 = new RuleTerm(bajoAtaque, "Ataque", false);
		RuleTerm term3rule1 = new RuleTerm(poblacionObj, "Alta", false);
		RuleTerm term5rule1 = new RuleTerm(numSilosObj, "Alto", false);
		RuleTerm term8rule1 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleExpression antecedenteAnd11 = new RuleExpression(term1rule1, term2rule1, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd12 = new RuleExpression(antecedenteAnd11, term3rule1,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd13 = new RuleExpression(antecedenteAnd12, term5rule1,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd14 = new RuleExpression(antecedenteAnd13, term8rule1,
				RuleConnectionMethodAndMin.get());
		rule1.setAntecedents(antecedenteAnd14);
		rule1.addConsequent(efectuarDisp, "Si", false);
		rule1.setWeight(this.weights[0]);
		ruleBlock.add(rule1);

		// IF NUM_MISSILES == ALTO AND BAJO_ATAQUE AND POBLACION_OBJ = ALTA AND NUM_SILOS_OBJ == ALTO AND MI_NUM_SILOS = ALTO AND SCORE_DISPARO != ALTO THEN NO_DISPARO
		Rule rule2 = new Rule("Rule2", ruleBlock);
		RuleTerm term1rule2 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule2 = new RuleTerm(bajoAtaque, "Ataque", false);
		RuleTerm term3rule2 = new RuleTerm(poblacionObj, "Alta", false);
		RuleTerm term5rule2 = new RuleTerm(numSilosObj, "Alto", false);
		RuleTerm term7rule2 = new RuleTerm(miNumSilos, "Alto", false);
		RuleTerm term8rule2 = new RuleTerm(scoreDisparo, "Alto", true);
		RuleExpression antecedenteAnd21 = new RuleExpression(term1rule2, term2rule2, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd22 = new RuleExpression(antecedenteAnd21, term3rule2,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd23 = new RuleExpression(antecedenteAnd22, term5rule2,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd24 = new RuleExpression(antecedenteAnd23, term7rule2,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd25 = new RuleExpression(antecedenteAnd24, term8rule2,
				RuleConnectionMethodAndMin.get());
		rule2.setAntecedents(antecedenteAnd25);
		rule2.addConsequent(efectuarDisp, "No", false);
		rule2.setWeight(this.weights[1]);
		ruleBlock.add(rule2);

		// IF NUM_MISSILES == ALTO AND !BAJO_ATAQUE AND POBLACION_OBJ == ALTA AND (MI_POBLACION != ALTA OR MI_NUM_SILOS != ALTO) AND NUM_SILOS_OBJ == ALTO AND SCORE_DISPARO == ALTO THEN DISPARO
		Rule rule3 = new Rule("Rule3", ruleBlock);
		RuleTerm term1rule3 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule3 = new RuleTerm(bajoAtaque, "No Ataque", false);
		RuleTerm term3rule3 = new RuleTerm(poblacionObj, "Alta", false);
		RuleTerm term4rule3 = new RuleTerm(miPoblacion, "Alta", true);
		RuleTerm term5rule3 = new RuleTerm(numSilosObj, "Alto", false);
		RuleTerm term7rule3 = new RuleTerm(miNumSilos, "Alto", true);
		RuleTerm term8rule3 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleExpression antecedenteAnd31 = new RuleExpression(term1rule3, term2rule3, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteOr31 = new RuleExpression(term4rule3, term7rule3, RuleConnectionMethodOrMax.get());
		RuleExpression antecedenteAnd32 = new RuleExpression(antecedenteAnd31, antecedenteOr31, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd33 = new RuleExpression(antecedenteAnd32, term4rule3,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd34 = new RuleExpression(antecedenteAnd33, term5rule3,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd35 = new RuleExpression(antecedenteAnd34, term8rule3,
				RuleConnectionMethodAndMin.get());
		rule3.setAntecedents(antecedenteAnd35);
		rule3.addConsequent(efectuarDisp, "Si", false);
		rule3.setWeight(this.weights[2]);
		ruleBlock.add(rule3);

		// IF NUM_MISSILES == ALTO AND !BAJO_ATAQUE AND POBLACION_OBJ == ALTA AND MI_POBLACION == ALTA AND NUM_SILOS_OBJ == ALTO AND MI_NUM_SILOS == ALTO THEN NO_DISPARO
		Rule rule4 = new Rule("Rule4", ruleBlock);
		RuleTerm term1rule4 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule4 = new RuleTerm(bajoAtaque, "No Ataque", false);
		RuleTerm term3rule4 = new RuleTerm(poblacionObj, "Alta", false);
		RuleTerm term4rule4 = new RuleTerm(miPoblacion, "Alta", false);
		RuleTerm term5rule4 = new RuleTerm(numSilosObj, "Alto", false);
		RuleTerm term7rule4 = new RuleTerm(miNumSilos, "Alto", false);
		RuleExpression antecedenteAnd41 = new RuleExpression(term1rule4, term2rule4, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd42 = new RuleExpression(antecedenteAnd41, term3rule4,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd43 = new RuleExpression(antecedenteAnd42, term4rule4,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd44 = new RuleExpression(antecedenteAnd43, term5rule4,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd45 = new RuleExpression(antecedenteAnd44, term7rule4,
				RuleConnectionMethodAndMin.get());
		rule4.setAntecedents(antecedenteAnd45);
		rule4.addConsequent(efectuarDisp, "No", false);
		rule4.setWeight(this.weights[3]);
		ruleBlock.add(rule4);

		// IF NUM_MISSILES == BAJO AND BAJO_ATAQUE AND POBLACION_OBJ == ALTA AND MI_POB == BAJA AND MI_NUM_SILOS == BAJO AND NUM_SILOS_OBJ == ALTO AND SCORE_DISPARO == ALTO THEN DISPARO
		Rule rule5 = new Rule("Rule5", ruleBlock);
		RuleTerm term1rule5 = new RuleTerm(numMisiles, "Bajo", false);
		RuleTerm term2rule5 = new RuleTerm(bajoAtaque, "Ataque", false);
		RuleTerm term3rule5 = new RuleTerm(poblacionObj, "Alta", false);
		RuleTerm term4rule5 = new RuleTerm(miPoblacion, "Baja", false);
		RuleTerm term5rule5 = new RuleTerm(numSilosObj, "Alto", false);
		RuleTerm term7rule5 = new RuleTerm(miNumSilos, "Bajo", false);
		RuleTerm term8rule5 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleExpression antecedenteAnd51 = new RuleExpression(term1rule5, term2rule5, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd52 = new RuleExpression(antecedenteAnd51, term3rule5,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd53 = new RuleExpression(antecedenteAnd52, term4rule5,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd54 = new RuleExpression(antecedenteAnd53, term5rule5,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd55 = new RuleExpression(antecedenteAnd54, term7rule5,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd56 = new RuleExpression(antecedenteAnd55, term8rule5,
				RuleConnectionMethodAndMin.get());
		rule5.setAntecedents(antecedenteAnd56);
		rule5.addConsequent(efectuarDisp, "Si", false);
		rule5.setWeight(this.weights[4]);
		ruleBlock.add(rule5);

		// IF NUM_MISSILES == ALTO AND !BAJO_ATAQUE AND MI_POBLACION == ALTA AND NUM_SILOS_OBJ == ALTO AND MI_NUM_SILOS == ALTO  AND SCORE_DISPARO == ALTO THEN DISPARO
		Rule rule6 = new Rule("Rule6", ruleBlock);
		RuleTerm term1rule6 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule6 = new RuleTerm(bajoAtaque, "No Ataque", false);
		RuleTerm term4rule6 = new RuleTerm(miPoblacion, "Alta", false);
		RuleTerm term5rule6 = new RuleTerm(numSilosObj, "Alto", false);
		RuleTerm term7rule6 = new RuleTerm(miNumSilos, "Alto", false);
		RuleTerm term8rule6 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleExpression antecedenteAnd61 = new RuleExpression(term1rule6, term2rule6, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd62 = new RuleExpression(antecedenteAnd61, term4rule6,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd63 = new RuleExpression(antecedenteAnd62, term5rule6,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd64 = new RuleExpression(antecedenteAnd63, term7rule6,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd65 = new RuleExpression(antecedenteAnd64, term8rule6,
				RuleConnectionMethodAndMin.get());
		rule6.setAntecedents(antecedenteAnd65);
		rule6.addConsequent(efectuarDisp, "Si", false);
		rule6.setWeight(this.weights[5]);
		ruleBlock.add(rule6);

		// IF NUM_MISSILES == ALTO AND BAJO_ATAQUE AND POBLACION_OBJ != BAJA AND MI_POBLACION != BAJA  AND NUM_SILOS_OBJ != BAJO AND MI_NUM_SILOS == ALTO AND SCORE_DISPARO == ALTO THEN DISPARO
		Rule rule7 = new Rule("Rule7", ruleBlock);
		RuleTerm term1rule7 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule7 = new RuleTerm(bajoAtaque, "Ataque", false);
		RuleTerm term3rule7 = new RuleTerm(poblacionObj, "Baja", true);
		RuleTerm term4rule7 = new RuleTerm(miPoblacion, "Baja", true);
		RuleTerm term5rule7 = new RuleTerm(numSilosObj, "Bajo", true);
		RuleTerm term7rule7 = new RuleTerm(miNumSilos, "Alto", false);
		RuleTerm term8rule7 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleExpression antecedenteAnd71 = new RuleExpression(term1rule7, term2rule7, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd72 = new RuleExpression(antecedenteAnd71, term3rule7,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd73 = new RuleExpression(antecedenteAnd72, term4rule7,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd74 = new RuleExpression(antecedenteAnd73, term5rule7,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd75 = new RuleExpression(antecedenteAnd74, term7rule7,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd76 = new RuleExpression(antecedenteAnd75, term8rule7,
				RuleConnectionMethodAndMin.get());
		rule7.setAntecedents(antecedenteAnd76);
		rule7.addConsequent(efectuarDisp, "Si", false);
		rule7.setWeight(this.weights[6]);
		ruleBlock.add(rule7);

		// IF NUM_MISSILES != ALTO AND !ATAQUE AND POBLACION_OBJ != ALTA AND NUM_SILOS_OBJ != ALTO SCORE !=ALTO THEN NO_DISPARO
		Rule rule8 = new Rule("Rule8", ruleBlock);
		RuleTerm term1rule8 = new RuleTerm(numMisiles, "Alto", true);
		RuleTerm term2rule8 = new RuleTerm(bajoAtaque, "No Ataque", false);
		RuleTerm term3rule8 = new RuleTerm(poblacionObj, "Alta", true);
		RuleTerm term5rule8 = new RuleTerm(numSilosObj, "Alto", true);
		RuleTerm term8rule8 = new RuleTerm(scoreDisparo, "Alto", true);
		RuleExpression antecedenteAnd81 = new RuleExpression(term1rule8, term2rule8, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd82 = new RuleExpression(antecedenteAnd81, term3rule8,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd83 = new RuleExpression(antecedenteAnd82, term5rule8,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd84 = new RuleExpression(antecedenteAnd83, term8rule8,
				RuleConnectionMethodAndMin.get());
		rule8.setAntecedents(antecedenteAnd84);
		rule8.addConsequent(efectuarDisp, "No", false);
		rule8.setWeight(this.weights[7]);
		ruleBlock.add(rule8);

		// IF NUM_MISILES == BAJO AND BAJO_ATAQUE AND POBLACION_OBJ == ALTA AND MI_POBLACION != ALTA AND MI_NUM_SILOS != ALTO AND SCORE_DISPARO == ALTO THEN DISPARO
		Rule rule9 = new Rule("Rule9", ruleBlock);
		RuleTerm term1rule9 = new RuleTerm(numMisiles, "Bajo", false);
		RuleTerm term2rule9 = new RuleTerm(bajoAtaque, "Ataque", false);
		RuleTerm term3rule9 = new RuleTerm(poblacionObj, "Alta", false);
		RuleTerm term4rule9 = new RuleTerm(miPoblacion, "Alta", true);
		RuleTerm term7rule9 = new RuleTerm(miNumSilos, "Alto", true);
		RuleTerm term8rule9 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleExpression antecedenteAnd91 = new RuleExpression(term1rule9, term2rule9, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd92 = new RuleExpression(antecedenteAnd91, term3rule9,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd93 = new RuleExpression(antecedenteAnd92, term4rule9,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd94 = new RuleExpression(antecedenteAnd93, term7rule9,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd95 = new RuleExpression(antecedenteAnd94, term8rule9,
				RuleConnectionMethodAndMin.get());
		rule9.setAntecedents(antecedenteAnd95);
		rule9.addConsequent(efectuarDisp, "Si", false);
		rule9.setWeight(this.weights[8]);
		ruleBlock.add(rule9);

		// IF NUM_MISSILES == ALTO AND BAJO_ATAQUE AND POBLACION_OBJ == ALTA AND MI_POBLACION == ALTA AND NUM_SILOS_OBJ == MEDIO AND MI_NUM_SILOS == MEDIO AND SCORE_DISPARO == ALTO THEN DISPARO
		Rule rule10 = new Rule("Rule10", ruleBlock);
		RuleTerm term1rule10 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule10 = new RuleTerm(bajoAtaque, "Ataque", false);
		RuleTerm term3rule10 = new RuleTerm(poblacionObj, "Alta", false);
		RuleTerm term4rule10 = new RuleTerm(miPoblacion, "Alta", false);
		RuleTerm term5rule10 = new RuleTerm(numSilosObj, "Medio", false);
		RuleTerm term7rule10 = new RuleTerm(miNumSilos, "Medio", false);
		RuleTerm term8rule10 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleExpression antecedenteAnd101 = new RuleExpression(term1rule10, term2rule10,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd102 = new RuleExpression(antecedenteAnd101, term3rule10,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd103 = new RuleExpression(antecedenteAnd102, term4rule10,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd104 = new RuleExpression(antecedenteAnd103, term5rule10,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd105 = new RuleExpression(antecedenteAnd104, term7rule10,
				RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd106 = new RuleExpression(antecedenteAnd105, term8rule10,
				RuleConnectionMethodAndMin.get());
		rule10.setAntecedents(antecedenteAnd106);
		rule10.addConsequent(efectuarDisp, "Si", false);
		rule10.setWeight(this.weights[9]);
		ruleBlock.add(rule10);
		
		// IF NUM_MISSILES != ALTO AND !BAJO_ATAQUE AND SCORE_DISPARO != ALTO AND MI_POBLACION != BAJA AND MI_NUM_SILOS != BAJO AND POBLACION_OBJ == BAJA AND NUM_SILOS_OBJ == BAJO THEN NO_DISPARO
		Rule rule11 = new Rule("Rule11", ruleBlock);
		RuleTerm term1rule11 = new RuleTerm(numMisiles, "Alto", true);
		RuleTerm term2rule11 = new RuleTerm(bajoAtaque, "No Ataque", false);
		RuleTerm term3rule11 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleTerm term4rule11 = new RuleTerm(miPoblacion, "Baja", true);
		RuleTerm term5rule11 = new RuleTerm(miNumSilos, "Bajo", true);
		RuleTerm term6rule11 = new RuleTerm(poblacionObj, "Baja", false);
		RuleTerm term7rule11 = new RuleTerm(numSilosObj, "Alto", false);
		RuleExpression antecedenteAnd111 = new RuleExpression(term1rule11, term2rule11, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd112 = new RuleExpression(antecedenteAnd111,term3rule11, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd113 = new RuleExpression(antecedenteAnd112, term4rule11, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd114 = new RuleExpression(antecedenteAnd113, term5rule11, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd115 = new RuleExpression(antecedenteAnd114, term6rule11, RuleConnectionMethodAndMin.get());
		RuleExpression antecedenteAnd116 = new RuleExpression(antecedenteAnd115, term7rule11, RuleConnectionMethodAndMin.get());
		rule11.setAntecedents(antecedenteAnd116);
		rule11.addConsequent(efectuarDisp, "No", false);
		rule11.setWeight(this.weights[10]);
		ruleBlock.add(rule11);
		
		// IF NUM_MISSILES != BAJO AND SCORE_DISPARO == ALTO THEN DISPARO
		Rule rule12 = new Rule("rule12", ruleBlock);
		RuleTerm term1rule12 = new RuleTerm(numMisiles, "Bajo", true);
		RuleTerm term2rule12 = new RuleTerm(scoreDisparo, "Alto", false);
		RuleExpression antecedentAnd121 = new RuleExpression(term1rule12, term2rule12, RuleConnectionMethodAndMin.get());
		rule12.setAntecedents(antecedentAnd121);
		rule12.addConsequent(efectuarDisp, "Si", false);
		rule12.setWeight(this.weights[11]);
		ruleBlock.add(rule12);
		
		// Estas reglas son para cuando el jugador va perdiendo. De esta forma se vuelve más agresivo
		
		// IF (MI_POBLACION != ALTA AND POBLACION_OBJ == ALTA) OR  (MI_POBLACION == BAJA AND POBLACION_OBJ != BAJA) THEN DISPARO
		Rule rule13 = new Rule("rule13", ruleBlock);
		RuleTerm term1rule13 = new RuleTerm(miPoblacion, "Alta", true);
		RuleTerm term2rule13 = new RuleTerm(miPoblacion, "Baja", false);
		RuleTerm term3rule13 = new RuleTerm(poblacionObj, "Alta", false);
		RuleTerm term4rule13 = new RuleTerm(poblacionObj, "Baja", true);
		RuleExpression antecedentAnd131 = new RuleExpression(term1rule13, term3rule13, RuleConnectionMethodAndMin.get());
		RuleExpression antecedentAnd132 = new RuleExpression(term2rule13, term4rule13, RuleConnectionMethodAndMin.get());
		RuleExpression antecedentOr131 = new RuleExpression(antecedentAnd131, antecedentAnd132, RuleConnectionMethodOrMax.get());
		rule13.setAntecedents(antecedentOr131);
		rule13.addConsequent(efectuarDisp, "Si", false);
		rule13.setWeight(this.weights[12]);
		ruleBlock.add(rule13);
		
		// IF (MI_NUM_SILOS != ALTO AND NUM_SILOS_OBJ == ALTO) OR (MI_NUM_SILOS == BAJO AND NUM_SILOS_OBJ != BAJO) THEN DISPARO
		Rule rule14 = new Rule("rule14", ruleBlock);
		RuleTerm term1rule14 = new RuleTerm(miNumSilos, "Alto", true);
		RuleTerm term2rule14 = new RuleTerm(miNumSilos, "Bajo", false);
		RuleTerm term3rule14 = new RuleTerm(numSilosObj, "Alto", false);
		RuleTerm term4rule14 = new RuleTerm(numSilosObj, "Bajo", true);
		RuleExpression antecedentAnd141 = new RuleExpression(term1rule14, term3rule14, RuleConnectionMethodAndMin.get());
		RuleExpression antecedentAnd142 = new RuleExpression(term2rule14, term4rule14, RuleConnectionMethodAndMin.get());
		RuleExpression antecedentOr141 = new RuleExpression(antecedentAnd141, antecedentAnd142, RuleConnectionMethodOrMax.get());
		rule14.setAntecedents(antecedentOr141);
		rule14.addConsequent(efectuarDisp, "Si", false);
		rule14.setWeight(this.weights[13]);
		ruleBlock.add(rule14);

		HashMap<String, RuleBlock> ruleBlocksMap = new HashMap<String, RuleBlock>();
		ruleBlocksMap.put(ruleBlock.getName(), ruleBlock);
		functionBlock.setRuleBlocks(ruleBlocksMap);

		fis.getVariable("numMisiles").setValue(this.getNumMissiles());
		fis.getVariable("bajoAtaque").setValue(this.game.underAttack(this) ? 1 : 0);
		fis.getVariable("miPoblacion").setValue(this.getPopulation());
		fis.getVariable("poblacionObj").setValue(opponentPopulation);
		fis.getVariable("numSilosObj").setValue(opponentSilos);
		fis.getVariable("miNumSilos").setValue(this.getNumSilos());
		fis.getVariable("score").setValue(mov.getScore());
		fis.evaluate();

		return fis.getVariable("efectuarDisp").getMembership("Si") > 0;
	}

	public double CBCJ() {

		FIS fis = new FIS();

		// FUNCTION_BLOCK

		FunctionBlock functionBlock = new FunctionBlock(fis);
		fis.addFunctionBlock("CBCJ", functionBlock);

		// Variables de entrada para el controlador borroso de carga del silo

		Variable numMisiles = new Variable("numMisiles"); // numero de misiles
															// del silo
		Variable distanciaObj = new Variable("distanciaObj"); // distancia al
																// objetivo
		functionBlock.setVariable(numMisiles.getName(), numMisiles);
		functionBlock.setVariable(distanciaObj.getName(), distanciaObj);

		// Variables de salida para el controlador borroso de carga del silo

		Variable carga = new Variable("carga"); // si se quiere cargar el silo o
												// no
		functionBlock.setVariable(carga.getName(), carga);

		/*
		 * Miembros funcionales para la variable de entrada numMisiles(numero de
		 * misiles)
		 */

		MembershipFunction numMisilAlto = new MembershipFunctionSingleton(new Value(5));

		MembershipFunction numMisilMedio = new MembershipFunctionTriangular(new Value(2), new Value(3), new Value(4));

		MembershipFunction numMisilBajo = new MembershipFunctionSingleton(new Value(0), new Value(1));

		LinguisticTerm ltmnumMisilesAlto = new LinguisticTerm("Alto", numMisilAlto);
		LinguisticTerm ltmnumMisilesMedio = new LinguisticTerm("Medio", numMisilMedio);
		LinguisticTerm ltmnumMisilesBajo = new LinguisticTerm("Bajo", numMisilBajo);
		numMisiles.add(ltmnumMisilesAlto);
		numMisiles.add(ltmnumMisilesMedio);
		numMisiles.add(ltmnumMisilesBajo);

		/*
		 * Miembros funcionales para la variable de entrada
		 * distanciaObj(distancia al objetivo)
		 */

		Value distLargaX[] = { new Value(50), new Value(60) };
		Value distLargaY[] = { new Value(0), new Value(1) };
		MembershipFunction dLarga = new MembershipFunctionPieceWiseLinear(distLargaX, distLargaY);

		MembershipFunction dMedia = new MembershipFunctionTriangular(new Value(30), new Value(40), new Value(45));

		Value distCortaX[] = { new Value(10), new Value(20) };
		Value distCortaY[] = { new Value(1), new Value(0) };
		MembershipFunction dCorta = new MembershipFunctionPieceWiseLinear(distCortaX, distCortaY);

		LinguisticTerm ltdDistanciaObjLarga = new LinguisticTerm("Larga", dLarga);
		LinguisticTerm ltdDistanciaObjMedia = new LinguisticTerm("Media", dMedia);
		LinguisticTerm ltdDistanciaObjCorta = new LinguisticTerm("Corta", dCorta);
		distanciaObj.add(ltdDistanciaObjLarga);
		distanciaObj.add(ltdDistanciaObjMedia);
		distanciaObj.add(ltdDistanciaObjCorta);

		/*
		 * Miembros funcionales para la variable de salida carga(si se quiere
		 * cargar el silo o no)
		 */

		MembershipFunction cValida = new MembershipFunctionSingleton(new Value(1));
		MembershipFunction cNoValida = new MembershipFunctionSingleton(new Value(0));

		LinguisticTerm ltcValida = new LinguisticTerm("Si", cValida);
		LinguisticTerm ltcNoValida = new LinguisticTerm("No", cNoValida);
		carga.add(ltcValida);
		carga.add(ltcNoValida);

		// Bloque de reglas

		RuleBlock ruleBlock = new RuleBlock(functionBlock);
		ruleBlock.setName("Reglas de carga del silo");
		ruleBlock.setRuleAccumulationMethod(new RuleAccumulationMethodMax());
		ruleBlock.setRuleActivationMethod(new RuleActivationMethodMin());

		// IF numMisiles IS Alto AND distObj IS Larga then carga IS false

		Rule rule1 = new Rule("Rule1", ruleBlock);
		RuleTerm term1rule1 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule1 = new RuleTerm(distanciaObj, "Larga", false);
		RuleExpression antecedenteAnd1 = new RuleExpression(term1rule1, term2rule1, RuleConnectionMethodAndMin.get());
		rule1.setAntecedents(antecedenteAnd1);
		rule1.addConsequent(carga, "No", false);
		ruleBlock.add(rule1);

		// IF numMisiles IS Alto AND distObj IS Media then carga IS false

		Rule rule2 = new Rule("Rule2", ruleBlock);
		RuleTerm term1rule2 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule2 = new RuleTerm(distanciaObj, "Media", false);
		RuleExpression antecedenteAnd2 = new RuleExpression(term1rule2, term2rule2, RuleConnectionMethodAndMin.get());
		rule2.setAntecedents(antecedenteAnd2);
		rule2.addConsequent(carga, "No", false);
		ruleBlock.add(rule2);

		// IF numMisiles IS Alto AND distObj IS Corta then carga IS true

		Rule rule3 = new Rule("Rule3", ruleBlock);
		RuleTerm term1rule3 = new RuleTerm(numMisiles, "Alto", false);
		RuleTerm term2rule3 = new RuleTerm(distanciaObj, "Corta", false);
		RuleExpression antecedenteAnd3 = new RuleExpression(term1rule3, term2rule3, RuleConnectionMethodAndMin.get());
		rule3.setAntecedents(antecedenteAnd3);
		rule3.addConsequent(carga, "Si", false);
		ruleBlock.add(rule3);

		// IF numMisiles IS Medio AND distObj IS Larga then carga IS false

		Rule rule4 = new Rule("Rule4", ruleBlock);
		RuleTerm term1rule4 = new RuleTerm(numMisiles, "Medio", false);
		RuleTerm term2rule4 = new RuleTerm(distanciaObj, "Larga", false);
		RuleExpression antecedenteAnd4 = new RuleExpression(term1rule4, term2rule4, RuleConnectionMethodAndMin.get());
		rule4.setAntecedents(antecedenteAnd4);
		rule4.addConsequent(carga, "No", false);
		ruleBlock.add(rule4);

		// IF numMisiles IS Medio AND distObj IS Media then carga IS false

		Rule rule5 = new Rule("Rule5", ruleBlock);
		RuleTerm term1rule5 = new RuleTerm(numMisiles, "Medio", false);
		RuleTerm term2rule5 = new RuleTerm(distanciaObj, "Media", false);
		RuleExpression antecedenteAnd5 = new RuleExpression(term1rule5, term2rule5, RuleConnectionMethodAndMin.get());
		rule5.setAntecedents(antecedenteAnd5);
		rule5.addConsequent(carga, "No", false);
		ruleBlock.add(rule5);

		// IF numMisiles IS Medio AND distObj IS Corta then carga IS true

		Rule rule6 = new Rule("Rule6", ruleBlock);
		RuleTerm term1rule6 = new RuleTerm(numMisiles, "Medio", false);
		RuleTerm term2rule6 = new RuleTerm(distanciaObj, "Corta", false);
		RuleExpression antecedenteAnd6 = new RuleExpression(term1rule6, term2rule6, RuleConnectionMethodAndMin.get());
		rule6.setAntecedents(antecedenteAnd6);
		rule6.addConsequent(carga, "Si", false);
		ruleBlock.add(rule6);

		// IF numMisiles IS Bajo AND distObj IS Larga then carga IS false

		Rule rule7 = new Rule("Rule7", ruleBlock);
		RuleTerm term1rule7 = new RuleTerm(numMisiles, "Bajo", false);
		RuleTerm term2rule7 = new RuleTerm(distanciaObj, "Larga", false);
		RuleExpression antecedenteAnd7 = new RuleExpression(term1rule7, term2rule7, RuleConnectionMethodAndMin.get());
		rule7.setAntecedents(antecedenteAnd7);
		rule7.addConsequent(carga, "No", false);
		ruleBlock.add(rule7);

		// IF numMisiles IS Bajo AND distObj IS Media then carga IS false

		Rule rule8 = new Rule("Rule8", ruleBlock);
		RuleTerm term1rule8 = new RuleTerm(numMisiles, "Bajo", false);
		RuleTerm term2rule8 = new RuleTerm(distanciaObj, "Media", false);
		RuleExpression antecedenteAnd8 = new RuleExpression(term1rule8, term2rule8, RuleConnectionMethodAndMin.get());
		rule8.setAntecedents(antecedenteAnd8);
		rule8.addConsequent(carga, "No", false);
		ruleBlock.add(rule8);

		// IF numMisiles IS Bajo AND distObj IS Corta then carga IS true

		Rule rule9 = new Rule("Rule9", ruleBlock);
		RuleTerm term1rule9 = new RuleTerm(numMisiles, "Bajo", false);
		RuleTerm term2rule9 = new RuleTerm(distanciaObj, "Corta", false);
		RuleExpression antecedenteAnd9 = new RuleExpression(term1rule9, term2rule9, RuleConnectionMethodAndMin.get());
		rule9.setAntecedents(antecedenteAnd9);
		rule9.addConsequent(carga, "Si", false);
		ruleBlock.add(rule9);

		HashMap<String, RuleBlock> ruleBlocksMap = new HashMap<String, RuleBlock>();
		ruleBlocksMap.put(ruleBlock.getName(), ruleBlock);
		functionBlock.setRuleBlocks(ruleBlocksMap);

		fis.getVariable("numMisiles").setValue(1);
		fis.getVariable("distanciaObj").setValue(20);
		fis.getVariable("carga").setValue(0);
		fis.evaluate();

		return fis.getVariable("carga").getValue();

	}

}
