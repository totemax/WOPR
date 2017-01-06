package game.entities;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.defuzzifier.DefuzzifierCenterOfGravity;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionGaussian;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionPieceWiseLinear;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionSingleton;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionTrapetzoidal;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionTriangular;
import net.sourceforge.jFuzzyLogic.membership.Value;
import net.sourceforge.jFuzzyLogic.plot.JDialogFis;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
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
import java.util.HashMap;
/**
 * Clase descriptora del silo.
 */
public class Silo extends MapLocation  {


    
	
	
	private static final Integer SILO_POPULATION = 50; // Población inicial del
														// silo.

	private Integer missiles; // Misiles del silo

	private Boolean destroyed = false; // Silo destruido

	public Silo(Integer missiles, Integer x, Integer y) {
		super(SILO_POPULATION, x, y);
		this.missiles = missiles;
	}


	//functionBlock.setVariable(numMisiles.getName(), numMisiles);
	
	
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
	
	public double CBC(){
		
		FIS fis = new FIS();
		
		// FUNCTION_BLOCK
		
		FunctionBlock functionBlock = new FunctionBlock(fis);
		fis.addFunctionBlock("CBC",functionBlock);
		
		//Variables de entrada para el controlador borroso de carga del silo
		
	    Variable numMisiles = new Variable("numMisiles"); // numero de misiles del silo
		Variable distanciaObj = new Variable("distanciaObj"); // distancia al objetivo
		functionBlock.setVariable(numMisiles.getName(), numMisiles);
		functionBlock.setVariable(distanciaObj.getName(), distanciaObj);
		
		//Variables de salida para el controlador borroso de carga del silo
		
		Variable carga = new Variable("carga"); // si se quiere cargar el silo o no
		functionBlock.setVariable(carga.getName(), carga);
		
		/*
		 * Miembros funcionales para la variable de entrada numMisiles(numero de misiles) 
		 */

        MembershipFunction numMisilAlto = new MembershipFunctionSingleton(new Value(5));
        
      
        MembershipFunction numMisilMedio = new MembershipFunctionTriangular(new Value(2),new Value(3),new Value(4));
        
       
        MembershipFunction numMisilBajo = new MembershipFunctionSingleton(new Value(0), new Value(1));
        
        LinguisticTerm ltmnumMisilesAlto = new LinguisticTerm("Alto", numMisilAlto);
        LinguisticTerm ltmnumMisilesMedio = new LinguisticTerm("Medio", numMisilMedio);
        LinguisticTerm ltmnumMisilesBajo = new LinguisticTerm("Bajo", numMisilBajo);
        numMisiles.add(ltmnumMisilesAlto);
        numMisiles.add(ltmnumMisilesMedio);
        numMisiles.add(ltmnumMisilesBajo);
        
        /*
        * Miembros funcionales para la variable de entrada distanciaObj(distancia al objetivo)   
        */
        
        Value distLargaX[] = {new Value(50), new Value(60)};
        Value distLargaY[] = {new Value(60), new Value(70)};
        MembershipFunction dLarga = new MembershipFunctionPieceWiseLinear(distLargaX,distLargaY);
        
        MembershipFunction dMedia = new MembershipFunctionTriangular( new Value(30), new Value(40), new Value(45));
        
        Value distCortaX[] = {new Value(10), new Value(20)};
        Value distCortaY[] = {new Value(0), new Value(20)};
        MembershipFunction dCorta = new MembershipFunctionPieceWiseLinear(distCortaX,distCortaY);
        
        LinguisticTerm ltdDistanciaObjLarga = new LinguisticTerm("Larga", dLarga);
        LinguisticTerm ltdDistanciaObjMedia = new LinguisticTerm("Media", dMedia);
        LinguisticTerm ltdDistanciaObjCorta = new LinguisticTerm("Corta", dCorta);
        distanciaObj.add(ltdDistanciaObjLarga);
        distanciaObj.add(ltdDistanciaObjMedia);
        distanciaObj.add(ltdDistanciaObjCorta);
        
        /*
         * Miembros funcionales para la variable de salida carga(si se quiere cargar el silo o no) 
         */
        
        MembershipFunction cValida = new MembershipFunctionSingleton(new Value(1));
        MembershipFunction cNoValida = new MembershipFunctionSingleton(new Value(0));
        
        LinguisticTerm ltcValida = new LinguisticTerm("Si",cValida);
        LinguisticTerm ltcNoValida = new LinguisticTerm("No",cNoValida);
        carga.add(ltcValida);
        carga.add(ltcNoValida);
        
        
        
        
        //Bloque de reglas
        
		RuleBlock ruleBlock = new RuleBlock(functionBlock);
		ruleBlock.setName("Reglas de carga del silo");
		ruleBlock.setRuleAccumulationMethod(new RuleAccumulationMethodMax());
		ruleBlock.setRuleActivationMethod(new RuleActivationMethodMin());
		
		// IF numMisiles IS Alto AND distObj IS Larga then carga IS false
		
		Rule rule1 = new Rule("Rule1", ruleBlock);
		RuleTerm term1rule1 = new RuleTerm(numMisiles,"Alto",false);
		RuleTerm term2rule1 = new RuleTerm(distanciaObj,"Larga",false);
		RuleExpression antecedenteAnd1 = new RuleExpression(term1rule1,term2rule1,RuleConnectionMethodAndMin.get());
		rule1.setAntecedents(antecedenteAnd1);
		rule1.addConsequent(carga, "No", false);
		ruleBlock.add(rule1);
		 
		// IF numMisiles IS Alto AND distObj IS Media then carga IS false
		
		Rule rule2 = new Rule("Rule2", ruleBlock);
		RuleTerm term1rule2 = new RuleTerm(numMisiles, "Alto",false);
		RuleTerm term2rule2 = new RuleTerm(distanciaObj, "Media",false);
		RuleExpression antecedenteAnd2 = new RuleExpression(term1rule2,term2rule2,RuleConnectionMethodAndMin.get());
		rule2.setAntecedents(antecedenteAnd2);
		rule2.addConsequent(carga, "No", false);
		ruleBlock.add(rule2);
		
		// IF numMisiles IS Alto AND distObj IS Corta then carga IS true
		
		Rule rule3 = new Rule("Rule3",ruleBlock);
		RuleTerm term1rule3 = new RuleTerm(numMisiles, "Alto",false);
		RuleTerm term2rule3 = new RuleTerm(distanciaObj, "Corta",false);
		RuleExpression antecedenteAnd3 = new RuleExpression(term1rule3,term2rule3,RuleConnectionMethodAndMin.get());
		rule3.setAntecedents(antecedenteAnd3);
		rule3.addConsequent(carga, "Si", false);
		ruleBlock.add(rule3);
		
		// IF numMisiles IS Medio AND distObj IS Larga then carga IS false
		
		Rule rule4 = new Rule("Rule4",ruleBlock);
		RuleTerm term1rule4 = new RuleTerm(numMisiles, "Medio",false);
		RuleTerm term2rule4 = new RuleTerm(distanciaObj, "Larga",false);
		RuleExpression antecedenteAnd4 = new RuleExpression(term1rule4,term2rule4,RuleConnectionMethodAndMin.get());
		rule4.setAntecedents(antecedenteAnd4);
		rule4.addConsequent(carga, "No", false);
		ruleBlock.add(rule4);
		
		// IF numMisiles IS Medio AND distObj IS Media then carga IS false
		
		Rule rule5 = new Rule("Rule5",ruleBlock);
		RuleTerm term1rule5 = new RuleTerm(numMisiles, "Medio",false);
		RuleTerm term2rule5 = new RuleTerm(distanciaObj, "Media",false);
		RuleExpression antecedenteAnd5 = new RuleExpression(term1rule5,term2rule5,RuleConnectionMethodAndMin.get());
		rule5.setAntecedents(antecedenteAnd5);
		rule5.addConsequent(carga, "No", false);
		ruleBlock.add(rule5);
		
		// IF numMisiles IS Medio AND distObj IS Corta then carga IS true
		
		Rule rule6 = new Rule("Rule6",ruleBlock);
		RuleTerm term1rule6 = new RuleTerm(numMisiles, "Medio",false);
		RuleTerm term2rule6 = new RuleTerm(distanciaObj, "Corta",false);
		RuleExpression antecedenteAnd6 = new RuleExpression(term1rule6,term2rule6,RuleConnectionMethodAndMin.get());
		rule6.setAntecedents(antecedenteAnd6);
		rule6.addConsequent(carga, "Si", false);
		ruleBlock.add(rule6);
		
		// IF numMisiles IS Bajo AND distObj IS Larga then carga IS false
		
		Rule rule7 = new Rule("Rule7",ruleBlock);
		RuleTerm term1rule7 = new RuleTerm(numMisiles, "Bajo",false);
		RuleTerm term2rule7 = new RuleTerm(distanciaObj, "Larga",false);
		RuleExpression antecedenteAnd7 = new RuleExpression(term1rule7,term2rule7,RuleConnectionMethodAndMin.get());
		rule7.setAntecedents(antecedenteAnd7);
		rule7.addConsequent(carga, "No", false);
		ruleBlock.add(rule7);
		
		// IF numMisiles IS Bajo AND distObj IS Media then carga IS false
		
		Rule rule8 = new Rule("Rule8",ruleBlock);
		RuleTerm term1rule8 = new RuleTerm(numMisiles, "Bajo",false);
		RuleTerm term2rule8 = new RuleTerm(distanciaObj, "Media",false);
		RuleExpression antecedenteAnd8 = new RuleExpression(term1rule8,term2rule8,RuleConnectionMethodAndMin.get());
		rule8.setAntecedents(antecedenteAnd8);
		rule8.addConsequent(carga, "No", false);
		ruleBlock.add(rule8);
		
		// IF numMisiles IS Bajo AND distObj IS Corta then carga IS true
		
		Rule rule9 = new Rule("Rule9",ruleBlock);
		RuleTerm term1rule9 = new RuleTerm(numMisiles, "Bajo",false);
		RuleTerm term2rule9 = new RuleTerm(distanciaObj, "Corta",false);
		RuleExpression antecedenteAnd9 = new RuleExpression(term1rule9,term2rule9,RuleConnectionMethodAndMin.get());
		rule9.setAntecedents(antecedenteAnd9);
		rule9.addConsequent(carga, "Si", false);
		ruleBlock.add(rule9);
		
		HashMap<String, RuleBlock> ruleBlocksMap = new HashMap<String,RuleBlock>();
		ruleBlocksMap.put(ruleBlock.getName(), ruleBlock);
		functionBlock.setRuleBlocks(ruleBlocksMap);
		
		fis.getVariable("numMisiles").setValue(1);
		fis.getVariable("distanciaObj").setValue(20);
		fis.getVariable("carga").setValue(0);
		fis.evaluate();
		
		
		return fis.getVariable("carga").getValue();
		
	}
	
public int CBD(){
	
	FIS fis = new FIS();
	
	// FUNCTION_BLOCK
	
	FunctionBlock functionBlock = new FunctionBlock(fis);
	fis.addFunctionBlock("CBD", functionBlock);
	
	//Variables de Entrada del Controlador Borroso de Disparo(VAR_INPUT)
	
	Variable numMisiles = new Variable("numMisiles"); // numero de misiles
	Variable distanciaObj = new Variable("distanciaObj"); // distancia objetivo
	Variable poblacionObj = new Variable("poblacionObj"); // poblacion objetivo
	Variable esSilo = new Variable("esSilo"); // si el objetivo es silo o no 
	functionBlock.setVariable(numMisiles.getName(), numMisiles);
	functionBlock.setVariable(distanciaObj.getName(), distanciaObj);
	functionBlock.setVariable(poblacionObj.getName(), poblacionObj);
	functionBlock.setVariable(esSilo.getName(), esSilo);
	
	
	//Variables de Salida del Controlador Borroso de Disparo(VAR_OUTPUT)
	
	Variable dirDisparo = new Variable("dirDisparo");
	functionBlock.setVariable(dirDisparo.getName(), dirDisparo);
	
	/*
	 * Miembros funcionales para la variable de entrada numMisiles(numero misiles)
	 */
	
	MembershipFunction nMisilesAlto = new MembershipFunctionSingleton(new Value(5));
	
	MembershipFunction nMisilesMedio = new MembershipFunctionTriangular(new Value(2), new Value(3), new Value(4));
	
	MembershipFunction nMisilesBajo = new MembershipFunctionSingleton(new Value(0),new Value(1));
	
	LinguisticTerm ltnMisilesAlto = new LinguisticTerm("Alto",nMisilesAlto);
	LinguisticTerm ltnMisilesMedio = new LinguisticTerm("Medio",nMisilesMedio);
	LinguisticTerm ltnMisilesBajo = new LinguisticTerm("Bajo",nMisilesBajo);
	numMisiles.add(ltnMisilesAlto);
	numMisiles.add(ltnMisilesMedio);
	numMisiles.add(ltnMisilesBajo);
	
	/*
	* Miembros funcionales para la variable de entrada distanciaObj(distancia al objetivo)
	*/
	
	Value dLargaX[] = { new Value(40) , new Value(50)};
	Value dLargaY[] = { new Value(40) , new Value(35)};
	MembershipFunction dLarga = new MembershipFunctionPieceWiseLinear(dLargaX, dLargaY);
	
	MembershipFunction dMedia = new MembershipFunctionTriangular(new Value(35), new Value(30), new Value(25));
	
	Value dCortaX[] = { new Value(25), new Value(20)};
	Value dCortaY[] = { new Value(20), new Value(0)};
	MembershipFunction dCorta = new MembershipFunctionPieceWiseLinear(dCortaX, dCortaY);
	
	LinguisticTerm ltdLarga = new LinguisticTerm("Larga",dLarga);
	LinguisticTerm ltdMedia = new LinguisticTerm("Media",dMedia);
	LinguisticTerm ltdCorta = new LinguisticTerm("Corta",dCorta);
	distanciaObj.add(ltdLarga);
	distanciaObj.add(ltdMedia);
	distanciaObj.add(ltdCorta);
	
	/*
	*  Miembros funcionales para la variable de entrada poblacionObj(poblacion del objetivo)
	*/
	
	MembershipFunction pAlta = new MembershipFunctionGaussian(new Value(1500),new Value(500));
	
	MembershipFunction pMedia = new MembershipFunctionGaussian(new Value(725), new Value(173.205));
	
	MembershipFunction pBaja = new MembershipFunctionGaussian(new Value(205), new Value(146));
	
	LinguisticTerm ltpAlta = new LinguisticTerm("Alta",pAlta);
	LinguisticTerm ltpMedia = new LinguisticTerm("Media",pMedia);
	LinguisticTerm ltpBaja = new LinguisticTerm("Baja",pBaja);
	poblacionObj.add(ltpAlta);
	poblacionObj.add(ltpMedia);
	poblacionObj.add(ltpBaja);
	
	/*
	* Miembros funcionales para la variable de entrada esSilo(si el objetivo es silo o no)
	*/
	
	MembershipFunction siloV = new MembershipFunctionSingleton(new Value(1));
	
	MembershipFunction siloNV = new MembershipFunctionSingleton(new Value(0));
	
	LinguisticTerm ltsV = new LinguisticTerm("Es silo",siloV);
	LinguisticTerm ltsNV = new LinguisticTerm("No es silo",siloNV);
	esSilo.add(ltsV);
	esSilo.add(ltsNV);
	
	
	
	
	
	return 0;
	
}	

}