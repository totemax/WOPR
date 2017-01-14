package game.entities;

import java.util.HashMap;
import java.util.Map;

import game.GameController;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.defuzzifier.DefuzzifierCenterOfGravity;
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

/**
 * Clase descriptora del silo.
 */
public class Silo extends MapLocation {

	public static final Integer SILO_POPULATION = 50; // Poblacion inicial del
														// silo.
	public static final Integer MAX_MISSILES = 6;

	private Integer missiles = 0; // Misiles del silo

	private double[] weights;

	public Silo(Integer x, Integer y, double[] weights) {
		super(SILO_POPULATION, x, y);
		this.weights = weights;
	}

	public PlayerMovement getDisparo(MapLocation[] rivalLocations) {
		if (this.missiles == 0) {
			return null;
		}
		HashMap<MapLocation, Double> d = new HashMap<>();
		// Lo hago aleatorio para probar
		for (MapLocation loc : rivalLocations) {
			if (!loc.destroyed) {
				Double data = this.CBD(loc);
				if (data != null) {
					d.put(loc, data);
				}
			}
		}
		if (d.size() > 0) {
			MapLocation result = null;
			double val = 0;
			for (Map.Entry<MapLocation, Double> entry : d.entrySet()) {
				if (val < entry.getValue()) {
					result = entry.getKey();
					val = entry.getValue();
				}
			}
			return new PlayerMovement(this, result, val);
		}

		return null; // No se ejecuta movimiento
	}

	public void destroyLocation() {
		super.destroyLocation();
		this.missiles = 0;
		this.destroyed = true; // Queda inutilizado
	}

	public void recharge() {
		this.missiles++;
	}
	
	public void dispararMisil(){
		this.missiles--;
	}

	public Integer getNumMissiles() {
		return this.missiles;
	}

	public Double CBD(MapLocation location) {

		FIS fis = new FIS();

		// FUNCTION_BLOCK

		FunctionBlock functionBlock = new FunctionBlock(fis);
		fis.addFunctionBlock("CBD", functionBlock);

		// Variables de Entrada del Controlador Borroso de Disparo(VAR_INPUT)
		Variable numMisiles = new Variable("numMisiles"); // numero de misiles
		Variable distanciaObj = new Variable("distanciaObj"); // distancia //
																// objetivo
		Variable poblacionObj = new Variable("poblacionObj"); // poblacion //
																// objetivo
		Variable esSilo = new Variable("esSilo"); // si el objetivo es silo o no
		functionBlock.setVariable(numMisiles.getName(), numMisiles);
		functionBlock.setVariable(distanciaObj.getName(), distanciaObj);
		functionBlock.setVariable(poblacionObj.getName(), poblacionObj);
		functionBlock.setVariable(esSilo.getName(), esSilo);

		// Variables de Salida del Controlador Borroso de Disparo(VAR_OUTPUT)
		Variable dirDisparo = new Variable("disparo");
		functionBlock.setVariable(dirDisparo.getName(), dirDisparo);

		/*
		 * Miembros funcionales para la variable de entrada numMisiles(numero
		 * misiles)
		 */
		Value[] missilesAltoX = { new Value(2 * MAX_MISSILES / 3), new Value(MAX_MISSILES) };
		Value[] missilesAltoY = { new Value(0), new Value(1) };
		MembershipFunction nMisilesAlto = new MembershipFunctionPieceWiseLinear(missilesAltoX, missilesAltoY);

		MembershipFunction nMisilesMedio = new MembershipFunctionTriangular(new Value(MAX_MISSILES / 3),
				new Value(MAX_MISSILES / 2), new Value(2 * MAX_MISSILES / 3));

		Value[] missilesBajoX = { new Value(0), new Value(MAX_MISSILES / 3) };
		Value[] missilesBajoY = { new Value(1), new Value(0) };
		MembershipFunction nMisilesBajo = new MembershipFunctionPieceWiseLinear(missilesBajoX, missilesBajoY);

		LinguisticTerm ltnMisilesAlto = new LinguisticTerm("Alto", nMisilesAlto);
		LinguisticTerm ltnMisilesMedio = new LinguisticTerm("Medio", nMisilesMedio);
		LinguisticTerm ltnMisilesBajo = new LinguisticTerm("Bajo", nMisilesBajo);
		numMisiles.add(ltnMisilesAlto);
		numMisiles.add(ltnMisilesMedio);
		numMisiles.add(ltnMisilesBajo);

		/*
		 * Miembros funcionales para la variable de entrada
		 * distanciaObj(distancia al objetivo)
		 */
		Value dLargaX[] = { new Value(GameController.MAX_DISTANCE / 2), new Value(GameController.MAX_DISTANCE) };
		Value dLargaY[] = { new Value(0), new Value(1) };
		MembershipFunction dLarga = new MembershipFunctionPieceWiseLinear(dLargaX, dLargaY);

		MembershipFunction dMedia = new MembershipFunctionTriangular(new Value(GameController.MAX_DISTANCE / 3),
				new Value(GameController.MAX_DISTANCE / 2), new Value(2 * GameController.MAX_DISTANCE / 3));

		Value dCortaX[] = { new Value(0), new Value(GameController.MAX_DISTANCE / 2) };
		Value dCortaY[] = { new Value(1), new Value(0) };
		MembershipFunction dCorta = new MembershipFunctionPieceWiseLinear(dCortaX, dCortaY);

		LinguisticTerm ltdLarga = new LinguisticTerm("Larga", dLarga);
		LinguisticTerm ltdMedia = new LinguisticTerm("Media", dMedia);
		LinguisticTerm ltdCorta = new LinguisticTerm("Corta", dCorta);
		distanciaObj.add(ltdLarga);
		distanciaObj.add(ltdMedia);
		distanciaObj.add(ltdCorta);

		/*
		 * Miembros funcionales para la variable de entrada
		 * poblacionObj(poblacion del objetivo)
		 */

		MembershipFunction pAlta = new MembershipFunctionSingleton(new Value(City.CITY_POPULATION));

		MembershipFunction pMedia = new MembershipFunctionSingleton(new Value(Forest.FOREST_POPULATION));

		MembershipFunction pBaja = new MembershipFunctionSingleton(new Value(Silo.SILO_POPULATION));

		LinguisticTerm ltpAlta = new LinguisticTerm("Alta", pAlta);
		LinguisticTerm ltpMedia = new LinguisticTerm("Media", pMedia);
		LinguisticTerm ltpBaja = new LinguisticTerm("Baja", pBaja);
		poblacionObj.add(ltpAlta);
		poblacionObj.add(ltpMedia);
		poblacionObj.add(ltpBaja);

		/*
		 * Miembros funcionales para la variable de entrada esSilo(si el
		 * objetivo es silo o no)
		 */

		MembershipFunction siloV = new MembershipFunctionSingleton(new Value(1));

		MembershipFunction siloNV = new MembershipFunctionSingleton(new Value(0));

		LinguisticTerm ltsV = new LinguisticTerm("SILO", siloV);
		LinguisticTerm ltsNV = new LinguisticTerm("NO_SILO", siloNV);
		esSilo.add(ltsV);
		esSilo.add(ltsNV);

		/*
		 * Miembros funcionales para la variable de salida dirDisparo(direccion
		 * del disparo)
		 */
		Value[] dispKOX = { new Value(0), new Value(50) };
		Value[] dispKOY = { new Value(1), new Value(0) };
		MembershipFunction dDispKO = new MembershipFunctionPieceWiseLinear(dispKOX, dispKOY);
		LinguisticTerm ltdDispKO = new LinguisticTerm("DispKO", dDispKO);

		Value[] dispOKX = { new Value(50), new Value(100) };
		Value[] dispOKY = { new Value(0), new Value(1) };
		MembershipFunction dDispOK = new MembershipFunctionPieceWiseLinear(dispOKX, dispOKY);
		LinguisticTerm ltdDispOK = new LinguisticTerm("DispOK", dDispOK);

		dirDisparo.add(ltdDispKO);
		dirDisparo.add(ltdDispOK);

		dirDisparo.setDefuzzifier(new DefuzzifierCenterOfGravity(dirDisparo));

		// Bloque de reglas

		RuleBlock ruleBlock = new RuleBlock(functionBlock);
		ruleBlock.setName("Reglas de disparo del silo");
		ruleBlock.setRuleAccumulationMethod(new RuleAccumulationMethodMax());
		ruleBlock.setRuleActivationMethod(new RuleActivationMethodMin());

		// IF NUM_MISSILES != BAJO && DISTANCIA != LARGA THEN DISPARO_OK
		Rule rule1 = new Rule("Rule1", ruleBlock);
		RuleTerm term1rule1 = new RuleTerm(numMisiles, "Bajo", true);
		RuleTerm term2rule1 = new RuleTerm(distanciaObj, "Larga", true);
		RuleExpression rule1Antecessor1 = new RuleExpression(term1rule1, term2rule1, RuleConnectionMethodAndMin.get());
		rule1.setAntecedents(rule1Antecessor1);
		rule1.addConsequent(dirDisparo, "DispOK", false);
		rule1.setWeight(this.weights[0]);
		ruleBlock.add(rule1);

		// IF NUM_MISSILES != BAJO && DISTANCIA != LARGA && IS_SILO THEN
		// DISPARO_OK
		Rule rule2 = new Rule("Rule2", ruleBlock);
		RuleTerm term1rule2 = new RuleTerm(numMisiles, "Bajo", true);
		RuleTerm term2rule2 = new RuleTerm(distanciaObj, "Larga", true);
		RuleTerm term3rule2 = new RuleTerm(esSilo, "SILO", false);
		RuleExpression rule2Antecessor1 = new RuleExpression(term1rule2, term2rule2, RuleConnectionMethodAndMin.get());
		RuleExpression rule2Antecessor2 = new RuleExpression(rule2Antecessor1, term3rule2,
				RuleConnectionMethodAndMin.get());
		rule2.setAntecedents(rule2Antecessor2);
		rule2.addConsequent(dirDisparo, "DispOK", false);
		rule2.setWeight(this.weights[1]);
		ruleBlock.add(rule2);

		// IF NUM_MISSILES == BAJO || DISTANCIA == LARGA THEN DISPARO_KO
		Rule rule3 = new Rule("Rule3", ruleBlock);
		RuleTerm term1rule3 = new RuleTerm(numMisiles, "Bajo", false);
		RuleTerm term2rule3 = new RuleTerm(distanciaObj, "Larga", false);
		RuleExpression rule3Antecessor1 = new RuleExpression(term1rule3, term2rule3, RuleConnectionMethodOrMax.get());
		rule3.setAntecedents(rule3Antecessor1);
		rule3.addConsequent(dirDisparo, "DispKO", false);
		rule3.setWeight(this.weights[2]);
		ruleBlock.add(rule3);

		// IF NUM_MISSILES != BAJO && DISTANCIA != LARGA && POBLACION = ALTA
		// THEN DISPARO_OK
		Rule rule4 = new Rule("Rule4", ruleBlock);
		RuleTerm term1rule4 = new RuleTerm(numMisiles, "Bajo", true);
		RuleTerm term2rule4 = new RuleTerm(distanciaObj, "Larga", true);
		RuleTerm term3rule4 = new RuleTerm(poblacionObj, "Alta", true);
		RuleExpression rule4Antecessor1 = new RuleExpression(term1rule4, term2rule4, RuleConnectionMethodAndMin.get());
		RuleExpression rule4Antecessor2 = new RuleExpression(rule4Antecessor1, term3rule4,
				RuleConnectionMethodAndMin.get());
		rule4.setAntecedents(rule4Antecessor2);
		rule4.addConsequent(dirDisparo, "DispOK", true);
		rule4.setWeight(this.weights[3]);
		ruleBlock.add(rule4);

		// IF (NUM_MISSILES != ALTO || DISTANCIA != CORTA) AND (POBLACION !=
		// ALTA || !IS_SILO) DISPARO_KO
		Rule rule5 = new Rule("Rule5", ruleBlock);
		RuleTerm term1rule5 = new RuleTerm(numMisiles, "Alto", true);
		RuleTerm term2rule5 = new RuleTerm(distanciaObj, "Corta", true);
		RuleTerm term3rule5 = new RuleTerm(poblacionObj, "Alta", true);
		RuleTerm term4rule5 = new RuleTerm(esSilo, "NO_SILO", false);
		RuleExpression rule5antecessor1 = new RuleExpression(term1rule5, term2rule5, RuleConnectionMethodOrMax.get());
		RuleExpression rule5antecessor2 = new RuleExpression(term3rule5, term4rule5, RuleConnectionMethodOrMax.get());
		RuleExpression rule5And = new RuleExpression(rule5antecessor1, rule5antecessor2,
				RuleConnectionMethodAndMin.get());
		rule5.setAntecedents(rule5And);
		rule5.addConsequent(dirDisparo, "DispKO", false);
		rule5.setWeight(this.weights[4]);
		ruleBlock.add(rule5);

		HashMap<String, RuleBlock> ruleBlocksMap = new HashMap<String, RuleBlock>();
		ruleBlocksMap.put(ruleBlock.getName(), ruleBlock);
		functionBlock.setRuleBlocks(ruleBlocksMap);

		fis.getVariable("numMisiles").setValue(this.getNumMissiles());
		fis.getVariable("distanciaObj").setValue(this.distance(location));
		fis.getVariable("poblacionObj").setValue(location.getPopulation());
		fis.getVariable("esSilo").setValue((Silo.class.equals(location.getClass())) ? 1 : 0);
		fis.evaluate();

		if (fis.getVariable("disparo").getMembership("DispKO") > 0) {
			return null;
		}

		return fis.getVariable("disparo").getValue();
	}

	public static Integer getSilosInMap(MapLocation[] map) {
		Integer numSilos = 0;
		for (MapLocation loc : map) {
			if (!loc.destroyed && loc.getClass().equals(Silo.class)) {
				numSilos++;
			}
		}
		return numSilos;
	}

}
