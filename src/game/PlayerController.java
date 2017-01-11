package game;



import java.util.ArrayList;
import java.util.List;

import game.entities.City;
import game.entities.MapLocation;
import game.entities.PlayerMovement;
import game.entities.Silo;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionGaussian;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionSingleton;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionTrapetzoidal;
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

	public double CBDJ() //Metodo del controlador borroso de disparo del jugador
	{
		double probfallo=1;

		FIS fis = new FIS();

		FunctionBlock functionBlock = new FunctionBlock(fis);
		fis.addFunctionBlock("CBDJ", functionBlock);

		Variable dirDisparo = new Variable("dirDisparo"); // direccion del disparo del silo (salida del controlador borroso de disparo del silo)
		Variable bajoAtaque = new Variable("bajoAtaque"); // si el jugador se encuentra o no bajo ataque
		Variable miPoblacion = new Variable("miPoblacion"); //Poblacion que tengo
		Variable poblacionObj = new Variable("poblacionObj"); //Poblacion del enemigo
		Variable numSilosObj = new Variable("numSilosObj"); // numero de silos del objetivo
		Variable miNumSilos = new Variable("miNumSilos"); // Numero de silos que tengo
		Variable numMisiles = new Variable("numMisiles"); // Numero de misiles que tengo
		Variable probFallo = new Variable("proFallo");

		Variable efectuarDisp = new Variable("efectuarDisp"); // si se efectua o no el disparo


		/*
		 * Miembros funcionales de dirDisparo
		 */

		MembershipFunction dDisparo = new MembershipFunctionSingleton(new Value(50), new Value(60));

		LinguisticTerm ltdDisparo = new LinguisticTerm("Dir disparo",dDisparo);
		dirDisparo.add(ltdDisparo);

		/*
		 * Miembros funcionales de bajoAtaque
		 */

		MembershipFunction meAtacan = new MembershipFunctionSingleton(new Value(1));

		MembershipFunction noAtacan = new MembershipFunctionSingleton(new Value(0));

		LinguisticTerm ltmAtacan = new LinguisticTerm("Ataque",meAtacan);
		LinguisticTerm ltnAtacan = new LinguisticTerm("No Ataque",noAtacan);
		bajoAtaque.add(ltnAtacan);
	    bajoAtaque.add(ltmAtacan);

        /*
         * Miembros funcionales de miPoblacion
         */

       MembershipFunction pmAlta = new MembershipFunctionGaussian(new Value(1500), new Value(750));
       MembershipFunction pmMedia = new MembershipFunctionGaussian(new Value(725), new Value(173.205));
       MembershipFunction pmBaja = new MembershipFunctionGaussian(new Value(205), new Value(146));

       LinguisticTerm ltpmAlta = new LinguisticTerm("Alta",pmAlta);
       LinguisticTerm ltpmMedia = new LinguisticTerm("Media",pmMedia);
       LinguisticTerm ltpmBaja = new LinguisticTerm("Baja",pmBaja);
       miPoblacion.add(ltpmAlta);
       miPoblacion.add(ltpmMedia);
       miPoblacion.add(ltpmBaja);

       /*
        * Miembros funcionales de poblacionObj
        */

       MembershipFunction pObjAlta = new MembershipFunctionGaussian(new Value(1500), new Value(750));
       MembershipFunction pObjMedia = new MembershipFunctionGaussian(new Value(725), new Value(173.205));
       MembershipFunction pObjBaja = new MembershipFunctionGaussian(new Value(205), new Value(146));

       LinguisticTerm ltpObjAlta = new LinguisticTerm("Alta",pObjAlta);
       LinguisticTerm ltpObjMedia = new LinguisticTerm("Media",pObjMedia);
       LinguisticTerm ltpObjBaja = new LinguisticTerm("Baja",pObjBaja);
       poblacionObj.add(ltpObjAlta);
       poblacionObj.add(ltpObjMedia);
       poblacionObj.add(ltpObjBaja);

       /*
        * Miembros funcionales de numSilosObj
        */

       MembershipFunction nmObjAlto = new MembershipFunctionSingleton(new Value(5), new Value(4));
       MembershipFunction nmObjMedio = new MembershipFunctionSingleton(new Value(3));
       MembershipFunction nmObjBajo = new MembershipFunctionTriangular(new Value(2), new Value(1), new Value(0));

       LinguisticTerm ltnmObjAlto = new LinguisticTerm("Alto",nmObjAlto);
       LinguisticTerm ltnmObjMedio = new LinguisticTerm("Medio",nmObjMedio);
       LinguisticTerm ltnmObjBajo = new LinguisticTerm("Bajo",nmObjBajo);
       numSilosObj.add(ltnmObjAlto);
       numSilosObj.add(ltnmObjMedio);
       numSilosObj.add(ltnmObjBajo);

       /*
        * Miembros funcionales de miNumSilos
        */

       MembershipFunction mnSilosAlto = new MembershipFunctionSingleton(new Value(6), new Value(5));
       MembershipFunction mnSilosMedio = new MembershipFunctionSingleton(new Value(4), new Value(3));
       MembershipFunction mnSilosBajo = new MembershipFunctionTriangular(new Value(2), new Value(1), new Value(0));

       LinguisticTerm ltmnSilosAlto = new LinguisticTerm("Alto",mnSilosAlto);
       LinguisticTerm ltmnSilosMedio = new LinguisticTerm("Medio",mnSilosMedio);
       LinguisticTerm ltmnSilosBajo = new LinguisticTerm("Bajo",mnSilosBajo);
       miNumSilos.add(ltmnSilosAlto);
       miNumSilos.add(ltmnSilosMedio);
       miNumSilos.add(ltmnSilosBajo);

       /*
        * Miembros funcionales de numMisiles
        */

       MembershipFunction nmSilosAlto = new MembershipFunctionSingleton(new Value(5), new Value(4));
       MembershipFunction nmSilosMedio = new MembershipFunctionSingleton(new Value(3));
       MembershipFunction nmSilosBajo = new MembershipFunctionTriangular(new Value(2), new Value(1), new Value(0));

       LinguisticTerm ltnmSilosAlto = new LinguisticTerm("Alto",nmSilosAlto);
       LinguisticTerm ltnmSilosMedio = new LinguisticTerm("Medio",nmSilosMedio);
       LinguisticTerm ltnmSilosBajo = new LinguisticTerm("Bajo",nmSilosBajo);
       numMisiles.add(ltnmSilosAlto);
       numMisiles.add(ltnmSilosMedio);
       numMisiles.add(ltnmSilosBajo);

       /*
        * Miembros funcionales de efectuarDisp
        */

       MembershipFunction eDispSi = new MembershipFunctionSingleton(new Value(1));
       MembershipFunction eDispNo = new MembershipFunctionSingleton(new Value(0));

       LinguisticTerm lteDispSi = new LinguisticTerm("Si",eDispSi);
       LinguisticTerm lteDispNo = new LinguisticTerm("No",eDispNo);
       efectuarDisp.add(lteDispSi);
       efectuarDisp.add(lteDispNo);

       /*
        * Miembros funcionales de probabilidad de fallo
        */

       MembershipFunction pFalloAlta = new MembershipFunctionTriangular(new Value(100), new Value(90), new Value(80));
       MembershipFunction pFalloMedia = new MembershipFunctionTrapetzoidal(new Value(70), new Value(60),new Value(50), new Value(40));
       MembershipFunction pFalloBaja = new MembershipFunctionTrapetzoidal(new Value(30), new Value(20), new Value(10), new Value(0));

       LinguisticTerm ltpFalloAlta = new LinguisticTerm("Alta",pFalloAlta);
       LinguisticTerm ltpFalloMedia = new LinguisticTerm("Media",pFalloMedia);
       LinguisticTerm ltpFalloBaja = new LinguisticTerm("Baja",pFalloBaja);
       probFallo.add(ltpFalloAlta);
       probFallo.add(ltpFalloMedia);
       probFallo.add(ltpFalloBaja);

   	   RuleBlock ruleBlock = new RuleBlock(functionBlock);
   	   ruleBlock.setName("Reglas de disparo del jugador");
   	   ruleBlock.setRuleAccumulationMethod(new RuleAccumulationMethodMax());
   	   ruleBlock.setRuleActivationMethod(new RuleActivationMethodMin());

	   Rule rule1 = new Rule("Rule1",ruleBlock);
	   RuleTerm term1rule1 = new RuleTerm(numMisiles, "Alto",false);
	   RuleTerm term2rule1 = new RuleTerm(bajoAtaque, "Ataque",false);
	   RuleTerm term3rule1 = new RuleTerm(poblacionObj, "Alta",false);
	   RuleTerm term4rule1 = new RuleTerm(miPoblacion,"Alta",false);
	   RuleTerm term5rule1 = new RuleTerm(numSilosObj,"Alto",false);
	   RuleTerm term6rule1 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule1 = new RuleTerm(miNumSilos,"Alto",false);
	   RuleTerm term8rule1 = new RuleTerm(probFallo,"Baja",false);
	   RuleExpression antecedenteAnd11 = new RuleExpression(term1rule1,term2rule1,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd12 = new RuleExpression(antecedenteAnd11,term3rule1,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd13 = new RuleExpression(antecedenteAnd12,term4rule1,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd14 = new RuleExpression(antecedenteAnd13,term5rule1,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd15 = new RuleExpression(antecedenteAnd14,term6rule1,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd16 = new RuleExpression(antecedenteAnd15,term7rule1,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd17 = new RuleExpression(antecedenteAnd16,term8rule1,RuleConnectionMethodAndMin.get());
	   rule1.setAntecedents(antecedenteAnd17);
	   rule1.addConsequent(efectuarDisp, "Si", false);
	   ruleBlock.add(rule1);

	   Rule rule2 = new Rule("Rule2",ruleBlock);
	   RuleTerm term1rule2 = new RuleTerm(numMisiles, "Alto",false);
	   RuleTerm term2rule2 = new RuleTerm(bajoAtaque, "Ataque",false);
	   RuleTerm term3rule2 = new RuleTerm(poblacionObj, "Alta",false);
	   RuleTerm term4rule2 = new RuleTerm(miPoblacion,"Alta",false);
	   RuleTerm term5rule2 = new RuleTerm(numSilosObj,"Alto",false);
	   RuleTerm term6rule2 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule2 = new RuleTerm(miNumSilos,"Alto",false);
	   RuleTerm term8rule2 = new RuleTerm(probFallo,"Alta",false);
	   RuleExpression antecedenteAnd21 = new RuleExpression(term1rule2,term2rule2,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd22 = new RuleExpression(antecedenteAnd21,term3rule2,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd23 = new RuleExpression(antecedenteAnd22,term4rule2,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd24 = new RuleExpression(antecedenteAnd23,term5rule2,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd25 = new RuleExpression(antecedenteAnd24,term6rule2,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd26 = new RuleExpression(antecedenteAnd25,term7rule2,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd27 = new RuleExpression(antecedenteAnd26,term8rule2,RuleConnectionMethodAndMin.get());
	   rule2.setAntecedents(antecedenteAnd27);
	   rule2.addConsequent(efectuarDisp, "No", false);
	   ruleBlock.add(rule2);

	   Rule rule3 = new Rule("Rule3",ruleBlock);
	   RuleTerm term1rule3 = new RuleTerm(numMisiles, "Alto",false);
	   RuleTerm term2rule3 = new RuleTerm(bajoAtaque, "No Ataque",false);
	   RuleTerm term3rule3 = new RuleTerm(poblacionObj, "Alta",false);
	   RuleTerm term4rule3 = new RuleTerm(miPoblacion,"Alta",false);
	   RuleTerm term5rule3 = new RuleTerm(numSilosObj,"Alto",false);
	   RuleTerm term6rule3 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule3 = new RuleTerm(miNumSilos,"Alto",false);
	   RuleTerm term8rule3 = new RuleTerm(probFallo,"Baja",false);
	   RuleExpression antecedenteAnd31 = new RuleExpression(term1rule3,term2rule3,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd32 = new RuleExpression(antecedenteAnd31,term3rule3,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd33 = new RuleExpression(antecedenteAnd32,term4rule3,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd34 = new RuleExpression(antecedenteAnd33,term5rule3,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd35 = new RuleExpression(antecedenteAnd34,term6rule3,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd36 = new RuleExpression(antecedenteAnd35,term7rule3,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd37 = new RuleExpression(antecedenteAnd36,term8rule3,RuleConnectionMethodAndMin.get());
	   rule3.setAntecedents(antecedenteAnd37);
	   rule3.addConsequent(efectuarDisp, "Si", false);
	   ruleBlock.add(rule3);

	   Rule rule4 = new Rule("Rule4",ruleBlock);
	   RuleTerm term1rule4 = new RuleTerm(numMisiles, "Alto",false);
	   RuleTerm term2rule4 = new RuleTerm(bajoAtaque, "No Ataque",false);
	   RuleTerm term3rule4 = new RuleTerm(poblacionObj, "Alta",false);
	   RuleTerm term4rule4 = new RuleTerm(miPoblacion,"Alta",false);
	   RuleTerm term5rule4 = new RuleTerm(numSilosObj,"Alto",false);
	   RuleTerm term6rule4 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule4 = new RuleTerm(miNumSilos,"Alto",false);
	   RuleTerm term8rule4 = new RuleTerm(probFallo,"Alta",false);
	   RuleExpression antecedenteAnd41 = new RuleExpression(term1rule4,term2rule4,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd42 = new RuleExpression(antecedenteAnd41,term3rule4,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd43 = new RuleExpression(antecedenteAnd42,term4rule4,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd44 = new RuleExpression(antecedenteAnd43,term5rule4,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd45 = new RuleExpression(antecedenteAnd44,term6rule4,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd46 = new RuleExpression(antecedenteAnd45,term7rule4,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd47 = new RuleExpression(antecedenteAnd46,term8rule4,RuleConnectionMethodAndMin.get());
	   rule4.setAntecedents(antecedenteAnd47);
	   rule4.addConsequent(efectuarDisp, "No", false);
	   ruleBlock.add(rule4);

	   Rule rule5 = new Rule("Rule5",ruleBlock);
	   RuleTerm term1rule5 = new RuleTerm(numMisiles, "Bajo",false);
	   RuleTerm term2rule5 = new RuleTerm(bajoAtaque, "Ataque",false);
	   RuleTerm term3rule5 = new RuleTerm(poblacionObj, "Alta",false);
	   RuleTerm term4rule5 = new RuleTerm(miPoblacion,"Baja",false);
	   RuleTerm term5rule5 = new RuleTerm(numSilosObj,"Alto",false);
	   RuleTerm term6rule5 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule5 = new RuleTerm(miNumSilos,"Bajo",false);
	   RuleTerm term8rule5 = new RuleTerm(probFallo,"Baja",false);
	   RuleExpression antecedenteAnd51 = new RuleExpression(term1rule5,term2rule5,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd52 = new RuleExpression(antecedenteAnd51,term3rule5,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd53 = new RuleExpression(antecedenteAnd52,term4rule5,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd54 = new RuleExpression(antecedenteAnd53,term5rule5,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd55 = new RuleExpression(antecedenteAnd54,term6rule5,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd56 = new RuleExpression(antecedenteAnd55,term7rule5,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd57 = new RuleExpression(antecedenteAnd56,term8rule5,RuleConnectionMethodAndMin.get());
	   rule5.setAntecedents(antecedenteAnd57);
	   rule5.addConsequent(efectuarDisp, "Si", false);
	   ruleBlock.add(rule5);

	   Rule rule6 = new Rule("Rule6",ruleBlock);
	   RuleTerm term1rule6 = new RuleTerm(numMisiles, "Alto",false);
	   RuleTerm term2rule6 = new RuleTerm(bajoAtaque, "No Ataque",false);
	   RuleTerm term3rule6 = new RuleTerm(poblacionObj, "Baja",false);
	   RuleTerm term4rule6 = new RuleTerm(miPoblacion,"Alta",false);
	   RuleTerm term5rule6 = new RuleTerm(numSilosObj,"Bajo",false);
	   RuleTerm term6rule6 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule6 = new RuleTerm(miNumSilos,"Alto",false);
	   RuleTerm term8rule6 = new RuleTerm(probFallo,"Baja",false);
	   RuleExpression antecedenteAnd61 = new RuleExpression(term1rule6,term2rule6,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd62 = new RuleExpression(antecedenteAnd61,term3rule6,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd63 = new RuleExpression(antecedenteAnd62,term4rule6,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd64 = new RuleExpression(antecedenteAnd63,term5rule6,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd65 = new RuleExpression(antecedenteAnd64,term6rule6,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd66 = new RuleExpression(antecedenteAnd65,term7rule6,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd67 = new RuleExpression(antecedenteAnd66,term8rule6,RuleConnectionMethodAndMin.get());
	   rule6.setAntecedents(antecedenteAnd67);
	   rule6.addConsequent(efectuarDisp, "Si", false);
	   ruleBlock.add(rule6);

	   Rule rule7 = new Rule("Rule7",ruleBlock);
	   RuleTerm term1rule7 = new RuleTerm(numMisiles, "Alto",false);
	   RuleTerm term2rule7 = new RuleTerm(bajoAtaque, "Ataque",false);
	   RuleTerm term3rule7 = new RuleTerm(poblacionObj, "Media",false);
	   RuleTerm term4rule7 = new RuleTerm(miPoblacion,"Media",false);
	   RuleTerm term5rule7 = new RuleTerm(numSilosObj,"Alto",false);
	   RuleTerm term6rule7 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule7 = new RuleTerm(miNumSilos,"Alto",false);
	   RuleTerm term8rule7 = new RuleTerm(probFallo,"Baja",false);
	   RuleExpression antecedenteAnd71 = new RuleExpression(term1rule7,term2rule7,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd72 = new RuleExpression(antecedenteAnd71,term3rule7,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd73 = new RuleExpression(antecedenteAnd72,term4rule7,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd74 = new RuleExpression(antecedenteAnd73,term5rule7,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd75 = new RuleExpression(antecedenteAnd74,term6rule7,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd76 = new RuleExpression(antecedenteAnd75,term7rule7,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd77 = new RuleExpression(antecedenteAnd76,term8rule7,RuleConnectionMethodAndMin.get());
	   rule7.setAntecedents(antecedenteAnd77);
	   rule7.addConsequent(efectuarDisp, "Si", false);
	   ruleBlock.add(rule7);

	   Rule rule8 = new Rule("Rule8",ruleBlock);
	   RuleTerm term1rule8 = new RuleTerm(numMisiles, "Medio",false);
	   RuleTerm term2rule8 = new RuleTerm(bajoAtaque, "No Ataque",false);
	   RuleTerm term3rule8 = new RuleTerm(poblacionObj, "Media",false);
	   RuleTerm term4rule8 = new RuleTerm(miPoblacion,"Baja",false);
	   RuleTerm term5rule8 = new RuleTerm(numSilosObj,"Alto",false);
	   RuleTerm term6rule8 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule8 = new RuleTerm(miNumSilos,"Bajo",false);
	   RuleTerm term8rule8 = new RuleTerm(probFallo,"Alta",false);
	   RuleExpression antecedenteAnd81 = new RuleExpression(term1rule8,term2rule8,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd82 = new RuleExpression(antecedenteAnd81,term3rule8,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd83 = new RuleExpression(antecedenteAnd82,term4rule8,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd84 = new RuleExpression(antecedenteAnd83,term5rule8,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd85 = new RuleExpression(antecedenteAnd84,term6rule8,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd86 = new RuleExpression(antecedenteAnd85,term7rule8,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd87 = new RuleExpression(antecedenteAnd86,term8rule8,RuleConnectionMethodAndMin.get());
	   rule8.setAntecedents(antecedenteAnd87);
	   rule8.addConsequent(efectuarDisp, "No", false);
	   ruleBlock.add(rule8);

	   Rule rule9 = new Rule("Rule9",ruleBlock);
	   RuleTerm term1rule9 = new RuleTerm(numMisiles, "Bajo",false);
	   RuleTerm term2rule9 = new RuleTerm(bajoAtaque, "Ataque",false);
	   RuleTerm term3rule9 = new RuleTerm(poblacionObj, "Alta",false);
	   RuleTerm term4rule9 = new RuleTerm(miPoblacion,"Media",false);
	   RuleTerm term5rule9 = new RuleTerm(numSilosObj,"Bajo",false);
	   RuleTerm term6rule9 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule9 = new RuleTerm(miNumSilos,"Medio",false);
	   RuleTerm term8rule9 = new RuleTerm(probFallo,"Baja",false);
	   RuleExpression antecedenteAnd91 = new RuleExpression(term1rule9,term2rule9,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd92 = new RuleExpression(antecedenteAnd91,term3rule9,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd93 = new RuleExpression(antecedenteAnd92,term4rule9,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd94 = new RuleExpression(antecedenteAnd93,term5rule9,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd95 = new RuleExpression(antecedenteAnd94,term6rule9,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd96 = new RuleExpression(antecedenteAnd95,term7rule9,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd97 = new RuleExpression(antecedenteAnd96,term8rule9,RuleConnectionMethodAndMin.get());
	   rule9.setAntecedents(antecedenteAnd97);
	   rule9.addConsequent(efectuarDisp, "Si", false);
	   ruleBlock.add(rule9);

	   Rule rule10 = new Rule("Rule10",ruleBlock);
	   RuleTerm term1rule10 = new RuleTerm(numMisiles, "Alto",false);
	   RuleTerm term2rule10 = new RuleTerm(bajoAtaque, "Ataque",false);
	   RuleTerm term3rule10 = new RuleTerm(poblacionObj, "Alta",false);
	   RuleTerm term4rule10 = new RuleTerm(miPoblacion,"Alta",false);
	   RuleTerm term5rule10 = new RuleTerm(numSilosObj,"Medio",false);
	   RuleTerm term6rule10 = new RuleTerm(dirDisparo,"Dir disparo",false);
	   RuleTerm term7rule10 = new RuleTerm(miNumSilos,"Medio",false);
	   RuleTerm term8rule10 = new RuleTerm(probFallo,"Baja",false);
	   RuleExpression antecedenteAnd101 = new RuleExpression(term1rule10,term2rule10,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd102 = new RuleExpression(antecedenteAnd101,term3rule10,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd103 = new RuleExpression(antecedenteAnd102,term4rule10,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd104 = new RuleExpression(antecedenteAnd103,term5rule10,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd105 = new RuleExpression(antecedenteAnd104,term6rule10,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd106 = new RuleExpression(antecedenteAnd105,term7rule10,RuleConnectionMethodAndMin.get());
	   RuleExpression antecedenteAnd107 = new RuleExpression(antecedenteAnd106,term8rule10,RuleConnectionMethodAndMin.get());
	   rule10.setAntecedents(antecedenteAnd107);
	   rule10.addConsequent(efectuarDisp, "Si", false);
	   ruleBlock.add(rule10);
	   return 0;
	 }

}
