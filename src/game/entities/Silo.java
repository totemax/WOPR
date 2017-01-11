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
	
public double CBD(){
	
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
	
	/*
	 * Miembros funcionales para la variable de salida dirDisparo(direccion del disparo)
	 */
	
	MembershipFunction dDisp = new MembershipFunctionSingleton(new Value(50), new Value(60));
	
	LinguisticTerm ltdDisp = new LinguisticTerm("Direccion disparo",dDisp);
	dirDisparo.add(ltdDisp);
	
	
	
    //Bloque de reglas
    
	RuleBlock ruleBlock = new RuleBlock(functionBlock);
	ruleBlock.setName("Reglas de disparo del silo");
	ruleBlock.setRuleAccumulationMethod(new RuleAccumulationMethodMax());
	ruleBlock.setRuleActivationMethod(new RuleActivationMethodMin());
	
	
	// IF numMisiles IS Alto AND distanciaObj IS Alta AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo
	
	Rule rule1 = new Rule("Rule1",ruleBlock);
	RuleTerm term1rule1 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule1 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule1 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule1 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd11 = new RuleExpression(term1rule1,term2rule1,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd12 = new RuleExpression(antecedenteAnd11,term3rule1,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd13 = new RuleExpression(antecedenteAnd12,term4rule1,RuleConnectionMethodAndMin.get());
	rule1.setAntecedents(antecedenteAnd13);
	rule1.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule1);
	
	// IF numMisiles IS Alto AND distanciaObj IS Alta AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo
	
	Rule rule2 = new Rule("Rule2",ruleBlock);
	RuleTerm term1rule2 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule2 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule2 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule2 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd21 = new RuleExpression(term1rule2,term2rule2,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd22 = new RuleExpression(antecedenteAnd21,term3rule2,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd23 = new RuleExpression(antecedenteAnd22,term4rule2,RuleConnectionMethodAndMin.get());
	rule2.setAntecedents(antecedenteAnd23);
	rule2.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule2);
	
	// IF numMisiles IS Alto AND distanciaObj IS Alta AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo
	
	
	Rule rule3 = new Rule("Rule3",ruleBlock);
	RuleTerm term1rule3 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule3 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule3 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule3 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd31 = new RuleExpression(term1rule3,term2rule3,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd32 = new RuleExpression(antecedenteAnd31,term3rule3,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd33 = new RuleExpression(antecedenteAnd32,term4rule3,RuleConnectionMethodAndMin.get());
	rule3.setAntecedents(antecedenteAnd33);
	rule3.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule3);
	
	// IF numMisiles IS Alto AND distanciaObj IS Alta AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	Rule rule4 = new Rule("Rule4",ruleBlock);
	RuleTerm term1rule4 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule4 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule4 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule4 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd41 = new RuleExpression(term1rule4,term2rule4,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd42 = new RuleExpression(antecedenteAnd41,term3rule4,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd43 = new RuleExpression(antecedenteAnd42,term4rule4,RuleConnectionMethodAndMin.get());
	rule4.setAntecedents(antecedenteAnd43);
	rule4.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule4);
	
	// IF numMisiles IS Alto AND distanciaObj IS Alta AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule5 = new Rule("Rule5",ruleBlock);
	RuleTerm term1rule5 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule5 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule5 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule5 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd51 = new RuleExpression(term1rule5,term2rule5,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd52 = new RuleExpression(antecedenteAnd51,term3rule5,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd53 = new RuleExpression(antecedenteAnd52,term4rule5,RuleConnectionMethodAndMin.get());
	rule5.setAntecedents(antecedenteAnd53);
	rule5.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule5);
	
	// IF numMisiles IS Alto AND distanciaObj IS Alta AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule6 = new Rule("Rule6",ruleBlock);
	RuleTerm term1rule6 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule6 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule6 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule6 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd61 = new RuleExpression(term1rule6,term2rule6,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd62 = new RuleExpression(antecedenteAnd61,term3rule6,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd63 = new RuleExpression(antecedenteAnd62,term4rule6,RuleConnectionMethodAndMin.get());
	rule6.setAntecedents(antecedenteAnd63);
	rule6.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule6);
	
	// IF numMisiles IS Alto AND distanciaObj IS Media AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule7 = new Rule("Rule7",ruleBlock);
	RuleTerm term1rule7 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule7 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule7 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule7 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd71 = new RuleExpression(term1rule7,term2rule7,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd72 = new RuleExpression(antecedenteAnd71,term3rule7,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd73 = new RuleExpression(antecedenteAnd72,term4rule7,RuleConnectionMethodAndMin.get());
	rule7.setAntecedents(antecedenteAnd73);
	rule7.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule7);
	
	// IF numMisiles IS Alto AND distanciaObj IS Media AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule8 = new Rule("Rule8",ruleBlock);
	RuleTerm term1rule8 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule8 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule8 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule8 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd81 = new RuleExpression(term1rule8,term2rule8,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd82 = new RuleExpression(antecedenteAnd81,term3rule8,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd83 = new RuleExpression(antecedenteAnd82,term4rule8,RuleConnectionMethodAndMin.get());
	rule8.setAntecedents(antecedenteAnd83);
	rule8.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule8);
	
	// IF numMisiles IS Alto AND distanciaObj IS Media AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule9 = new Rule("Rule9",ruleBlock);
	RuleTerm term1rule9 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule9 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule9 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule9 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd91 = new RuleExpression(term1rule9,term2rule9,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd92 = new RuleExpression(antecedenteAnd91,term3rule9,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd93 = new RuleExpression(antecedenteAnd92,term4rule9,RuleConnectionMethodAndMin.get());
	rule9.setAntecedents(antecedenteAnd93);
	rule9.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule9);
	
	// IF numMisiles IS Alto AND distanciaObj IS Media AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule10 = new Rule("Rule10",ruleBlock);
	RuleTerm term1rule10 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule10 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule10 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule10 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd101 = new RuleExpression(term1rule10,term2rule10,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd102 = new RuleExpression(antecedenteAnd101,term3rule10,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd103 = new RuleExpression(antecedenteAnd102,term4rule10,RuleConnectionMethodAndMin.get());
	rule10.setAntecedents(antecedenteAnd103);
	rule10.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule10);
	
	// IF numMisiles IS Alto AND distanciaObj IS Media AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule11 = new Rule("Rule11",ruleBlock);
	RuleTerm term1rule11 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule11 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule11 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule11 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd111 = new RuleExpression(term1rule11,term2rule11,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd112 = new RuleExpression(antecedenteAnd111,term3rule11,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd113 = new RuleExpression(antecedenteAnd112,term4rule11,RuleConnectionMethodAndMin.get());
	rule11.setAntecedents(antecedenteAnd113);
	rule11.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule11);
	
	// IF numMisiles IS Alto AND distanciaObj IS Media AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule12 = new Rule("Rule12",ruleBlock);
	RuleTerm term1rule12 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule12 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule12 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule12 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd121 = new RuleExpression(term1rule12,term2rule12,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd122 = new RuleExpression(antecedenteAnd121,term3rule12,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd123 = new RuleExpression(antecedenteAnd122,term4rule12,RuleConnectionMethodAndMin.get());
	rule12.setAntecedents(antecedenteAnd123);
	rule12.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule12);
	
	// IF numMisiles IS Alto AND distanciaObj IS Baja AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo
	
	Rule rule13 = new Rule("Rule13",ruleBlock);
	RuleTerm term1rule13 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule13 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule13 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule13 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd131 = new RuleExpression(term1rule13,term2rule13,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd132 = new RuleExpression(antecedenteAnd131,term3rule13,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd133 = new RuleExpression(antecedenteAnd132,term4rule13,RuleConnectionMethodAndMin.get());
	rule13.setAntecedents(antecedenteAnd133);
	rule13.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule13);
	
	// IF numMisiles IS Alto AND distanciaObj IS Baja AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule14 = new Rule("Rule14",ruleBlock);
	RuleTerm term1rule14 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule14 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule14 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule14 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd141 = new RuleExpression(term1rule14,term2rule14,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd142 = new RuleExpression(antecedenteAnd141,term3rule14,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd143 = new RuleExpression(antecedenteAnd142,term4rule14,RuleConnectionMethodAndMin.get());
	rule14.setAntecedents(antecedenteAnd143);
	rule14.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule14);
	
	// IF numMisiles IS Alto AND distanciaObj IS Baja AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule15 = new Rule("Rule15",ruleBlock);
	RuleTerm term1rule15 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule15 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule15 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule15 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd151 = new RuleExpression(term1rule15,term2rule15,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd152 = new RuleExpression(antecedenteAnd151,term3rule15,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd153 = new RuleExpression(antecedenteAnd152,term4rule15,RuleConnectionMethodAndMin.get());
	rule15.setAntecedents(antecedenteAnd153);
	rule15.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule15);
	
	// IF numMisiles IS Alto AND distanciaObj IS Baja AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule16 = new Rule("Rule16",ruleBlock);
	RuleTerm term1rule16 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule16 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule16 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule16 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd161 = new RuleExpression(term1rule16,term2rule16,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd162 = new RuleExpression(antecedenteAnd161,term3rule16,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd163 = new RuleExpression(antecedenteAnd162,term4rule16,RuleConnectionMethodAndMin.get());
	rule16.setAntecedents(antecedenteAnd163);
	rule16.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule16);
	
	// IF numMisiles IS Alto AND distanciaObj IS Baja AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule17 = new Rule("Rule17",ruleBlock);
	RuleTerm term1rule17 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule17 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule17 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule17 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd171 = new RuleExpression(term1rule17,term2rule17,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd172 = new RuleExpression(antecedenteAnd171,term3rule17,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd173 = new RuleExpression(antecedenteAnd172,term4rule17,RuleConnectionMethodAndMin.get());
	rule17.setAntecedents(antecedenteAnd173);
	rule17.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule17);
	
	// IF numMisiles IS Alto AND distanciaObj IS Baja AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	Rule rule18 = new Rule("Rule18",ruleBlock);
	RuleTerm term1rule18 = new RuleTerm(numMisiles, "Alto",false);
	RuleTerm term2rule18 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule18 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule18 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd181 = new RuleExpression(term1rule18,term2rule18,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd182 = new RuleExpression(antecedenteAnd181,term3rule18,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd183 = new RuleExpression(antecedenteAnd182,term4rule18,RuleConnectionMethodAndMin.get());
	rule18.setAntecedents(antecedenteAnd183);
	rule18.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule18);
	
	// IF numMisiles IS Medio AND distanciaObj IS Alta AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule19 = new Rule("Rule19",ruleBlock);
	RuleTerm term1rule19 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule19 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule19 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule19 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd191 = new RuleExpression(term1rule19,term2rule19,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd192 = new RuleExpression(antecedenteAnd191,term3rule19,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd193 = new RuleExpression(antecedenteAnd192,term4rule19,RuleConnectionMethodAndMin.get());
	rule19.setAntecedents(antecedenteAnd193);
	rule19.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule19);
	
	
	// IF numMisiles IS Medio AND distanciaObj IS Alta AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule20 = new Rule("Rule20",ruleBlock);
	RuleTerm term1rule20 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule20 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule20 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule20 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd201 = new RuleExpression(term1rule20,term2rule20,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd202 = new RuleExpression(antecedenteAnd201,term3rule20,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd203 = new RuleExpression(antecedenteAnd202,term4rule20,RuleConnectionMethodAndMin.get());
	rule20.setAntecedents(antecedenteAnd203);
	rule20.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule20);
	
	// IF numMisiles IS Medio AND distanciaObj IS Alta AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule21 = new Rule("Rule21",ruleBlock);
	RuleTerm term1rule21 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule21 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule21 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule21 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd211 = new RuleExpression(term1rule21,term2rule21,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd212 = new RuleExpression(antecedenteAnd211,term3rule21,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd213 = new RuleExpression(antecedenteAnd212,term4rule21,RuleConnectionMethodAndMin.get());
	rule21.setAntecedents(antecedenteAnd213);
	rule21.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule21);
	
	// IF numMisiles IS Medio AND distanciaObj IS Alta AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule22 = new Rule("Rule22",ruleBlock);
	RuleTerm term1rule22 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule22 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule22 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule22 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd221 = new RuleExpression(term1rule22,term2rule22,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd222 = new RuleExpression(antecedenteAnd221,term3rule22,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd223 = new RuleExpression(antecedenteAnd222,term4rule22,RuleConnectionMethodAndMin.get());
	rule22.setAntecedents(antecedenteAnd223);
	rule22.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule22);
	
	// IF numMisiles IS Medio AND distanciaObj IS Alta AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo
	
	Rule rule23 = new Rule("Rule23",ruleBlock);
	RuleTerm term1rule23 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule23 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule23 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule23 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd231 = new RuleExpression(term1rule23,term2rule23,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd232 = new RuleExpression(antecedenteAnd231,term3rule23,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd233 = new RuleExpression(antecedenteAnd232,term4rule23,RuleConnectionMethodAndMin.get());
	rule23.setAntecedents(antecedenteAnd233);
	rule23.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule23);
	
	// IF numMisiles IS Medio AND distanciaObj IS Alta AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule24 = new Rule("Rule24",ruleBlock);
	RuleTerm term1rule24 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule24 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule24 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule24 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd241 = new RuleExpression(term1rule24,term2rule24,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd242 = new RuleExpression(antecedenteAnd241,term3rule24,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd243 = new RuleExpression(antecedenteAnd242,term4rule24,RuleConnectionMethodAndMin.get());
	rule24.setAntecedents(antecedenteAnd243);
	rule24.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule24);
	
	// IF numMisiles IS Medio AND distanciaObj IS Media AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule25 = new Rule("Rule25",ruleBlock);
	RuleTerm term1rule25 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule25 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule25 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule25 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd251 = new RuleExpression(term1rule25,term2rule25,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd252 = new RuleExpression(antecedenteAnd251,term3rule25,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd253 = new RuleExpression(antecedenteAnd252,term4rule25,RuleConnectionMethodAndMin.get());
	rule25.setAntecedents(antecedenteAnd253);
	rule25.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule25);
	
	// IF numMisiles IS Medio AND distanciaObj IS Media AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule26 = new Rule("Rule26",ruleBlock);
	RuleTerm term1rule26 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule26 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule26 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule26 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd261 = new RuleExpression(term1rule26,term2rule26,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd262 = new RuleExpression(antecedenteAnd261,term3rule26,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd263 = new RuleExpression(antecedenteAnd262,term4rule26,RuleConnectionMethodAndMin.get());
	rule26.setAntecedents(antecedenteAnd263);
	rule26.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule26);
	
	// IF numMisiles IS Medio AND distanciaObj IS Media AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	Rule rule27 = new Rule("Rule27",ruleBlock);
	RuleTerm term1rule27 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule27 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule27 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule27 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd271 = new RuleExpression(term1rule27,term2rule27,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd272 = new RuleExpression(antecedenteAnd271,term3rule27,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd273 = new RuleExpression(antecedenteAnd272,term4rule27,RuleConnectionMethodAndMin.get());
	rule27.setAntecedents(antecedenteAnd273);
	rule27.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule27);
	
	// IF numMisiles IS Medio AND distanciaObj IS Media AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule28 = new Rule("Rule28",ruleBlock);
	RuleTerm term1rule28 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule28 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule28 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule28 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd281 = new RuleExpression(term1rule28,term2rule28,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd282 = new RuleExpression(antecedenteAnd281,term3rule28,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd283 = new RuleExpression(antecedenteAnd282,term4rule28,RuleConnectionMethodAndMin.get());
	rule28.setAntecedents(antecedenteAnd283);
	rule28.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule28);
	
	// IF numMisiles IS Medio AND distanciaObj IS Media AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule29 = new Rule("Rule29",ruleBlock);
	RuleTerm term1rule29 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule29 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule29 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule29 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd291 = new RuleExpression(term1rule29,term2rule29,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd292 = new RuleExpression(antecedenteAnd291,term3rule29,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd293 = new RuleExpression(antecedenteAnd292,term4rule29,RuleConnectionMethodAndMin.get());
	rule29.setAntecedents(antecedenteAnd293);
	rule29.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule29);
	
	// IF numMisiles IS Medio AND distanciaObj IS Media AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule30 = new Rule("Rule30",ruleBlock);
	RuleTerm term1rule30 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule30 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule30 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule30 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd301 = new RuleExpression(term1rule30,term2rule30,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd302 = new RuleExpression(antecedenteAnd301,term3rule30,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd303 = new RuleExpression(antecedenteAnd302,term4rule30,RuleConnectionMethodAndMin.get());
	rule30.setAntecedents(antecedenteAnd303);
	rule30.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule30);
	
	// IF numMisiles IS Medio AND distanciaObj IS Baja AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule31 = new Rule("Rule31",ruleBlock);
	RuleTerm term1rule31 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule31 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule31 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule31 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd311 = new RuleExpression(term1rule31,term2rule31,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd312 = new RuleExpression(antecedenteAnd311,term3rule31,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd313 = new RuleExpression(antecedenteAnd312,term4rule31,RuleConnectionMethodAndMin.get());
	rule31.setAntecedents(antecedenteAnd313);
	rule31.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule31);
	
	// IF numMisiles IS Medio AND distanciaObj IS Baja AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule32 = new Rule("Rule32",ruleBlock);
	RuleTerm term1rule32 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule32 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule32 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule32 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd321 = new RuleExpression(term1rule32,term2rule32,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd322 = new RuleExpression(antecedenteAnd321,term3rule32,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd323 = new RuleExpression(antecedenteAnd322,term4rule32,RuleConnectionMethodAndMin.get());
	rule32.setAntecedents(antecedenteAnd323);
	rule32.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule32);
	
	// IF numMisiles IS Medio AND distanciaObj IS Baja AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule33 = new Rule("Rule33",ruleBlock);
	RuleTerm term1rule33 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule33 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule33 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule33 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd331 = new RuleExpression(term1rule33,term2rule33,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd332 = new RuleExpression(antecedenteAnd331,term3rule33,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd333 = new RuleExpression(antecedenteAnd332,term4rule33,RuleConnectionMethodAndMin.get());
	rule33.setAntecedents(antecedenteAnd333);
	rule33.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule33);
	
	// IF numMisiles IS Medio AND distanciaObj IS Baja AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule34 = new Rule("Rule34",ruleBlock);
	RuleTerm term1rule34 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule34 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule34 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule34 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd341 = new RuleExpression(term1rule34,term2rule34,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd342 = new RuleExpression(antecedenteAnd341,term3rule34,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd343 = new RuleExpression(antecedenteAnd342,term4rule34,RuleConnectionMethodAndMin.get());
	rule34.setAntecedents(antecedenteAnd343);
	rule34.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule34);
	
	// IF numMisiles IS Medio AND distanciaObj IS Baja AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule35 = new Rule("Rule35",ruleBlock);
	RuleTerm term1rule35 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule35 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule35 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule35 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd351 = new RuleExpression(term1rule35,term2rule35,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd352 = new RuleExpression(antecedenteAnd351,term3rule35,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd353 = new RuleExpression(antecedenteAnd352,term4rule35,RuleConnectionMethodAndMin.get());
	rule35.setAntecedents(antecedenteAnd353);
	rule35.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule35);
	
	// IF numMisiles IS Medio AND distanciaObj IS Baja AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule36 = new Rule("Rule36",ruleBlock);
	RuleTerm term1rule36 = new RuleTerm(numMisiles, "Medio",false);
	RuleTerm term2rule36 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule36 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule36 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd361 = new RuleExpression(term1rule36,term2rule36,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd362 = new RuleExpression(antecedenteAnd361,term3rule36,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd363 = new RuleExpression(antecedenteAnd362,term4rule36,RuleConnectionMethodAndMin.get());
	rule36.setAntecedents(antecedenteAnd363);
	rule36.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule36);
    
	// IF numMisiles IS Bajo AND distanciaObj IS Alta AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule37 = new Rule("Rule37",ruleBlock);
	RuleTerm term1rule37 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule37 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule37 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule37 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd371 = new RuleExpression(term1rule37,term2rule37,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd372 = new RuleExpression(antecedenteAnd371,term3rule37,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd373 = new RuleExpression(antecedenteAnd372,term4rule37,RuleConnectionMethodAndMin.get());
	rule37.setAntecedents(antecedenteAnd373);
	rule37.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule37);

	// IF numMisiles IS Bajo AND distanciaObj IS Alta AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule38 = new Rule("Rule38",ruleBlock);
	RuleTerm term1rule38 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule38 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule38 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule38 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd381 = new RuleExpression(term1rule38,term2rule38,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd382 = new RuleExpression(antecedenteAnd381,term3rule38,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd383 = new RuleExpression(antecedenteAnd382,term4rule38,RuleConnectionMethodAndMin.get());
	rule38.setAntecedents(antecedenteAnd383);
	rule38.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule38);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Alta AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule39 = new Rule("Rule39",ruleBlock);
	RuleTerm term1rule39 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule39 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule39 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule39 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd391 = new RuleExpression(term1rule39,term2rule39,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd392 = new RuleExpression(antecedenteAnd391,term3rule39,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd393 = new RuleExpression(antecedenteAnd392,term4rule39,RuleConnectionMethodAndMin.get());
	rule39.setAntecedents(antecedenteAnd393);
	rule39.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule39);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Alta AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	Rule rule40 = new Rule("Rule40",ruleBlock);
	RuleTerm term1rule40 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule40 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule40 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule40 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd401 = new RuleExpression(term1rule40,term2rule40,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd402 = new RuleExpression(antecedenteAnd401,term3rule40,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd403 = new RuleExpression(antecedenteAnd402,term4rule40,RuleConnectionMethodAndMin.get());
	rule40.setAntecedents(antecedenteAnd403);
	rule40.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule40);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Alta AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule41 = new Rule("Rule41",ruleBlock);
	RuleTerm term1rule41 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule41 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule41 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule41 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd411 = new RuleExpression(term1rule41,term2rule41,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd412 = new RuleExpression(antecedenteAnd411,term3rule41,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd413 = new RuleExpression(antecedenteAnd412,term4rule41,RuleConnectionMethodAndMin.get());
	rule41.setAntecedents(antecedenteAnd413);
	rule41.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule41);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Alta AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	Rule rule42 = new Rule("Rule42",ruleBlock);
	RuleTerm term1rule42 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule42 = new RuleTerm(distanciaObj, "Alta",false);
	RuleTerm term3rule42 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule42 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd421 = new RuleExpression(term1rule42,term2rule42,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd422 = new RuleExpression(antecedenteAnd421,term3rule42,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd423 = new RuleExpression(antecedenteAnd422,term4rule42,RuleConnectionMethodAndMin.get());
	rule42.setAntecedents(antecedenteAnd423);
	rule42.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule42);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Media AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule43 = new Rule("Rule43",ruleBlock);
	RuleTerm term1rule43 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule43 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule43 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule43 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd431 = new RuleExpression(term1rule43,term2rule43,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd432 = new RuleExpression(antecedenteAnd431,term3rule43,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd433 = new RuleExpression(antecedenteAnd432,term4rule43,RuleConnectionMethodAndMin.get());
	rule43.setAntecedents(antecedenteAnd433);
	rule43.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule43);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Media AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule44 = new Rule("Rule44",ruleBlock);
	RuleTerm term1rule44 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule44 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule44 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule44 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd441 = new RuleExpression(term1rule44,term2rule44,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd442 = new RuleExpression(antecedenteAnd441,term3rule44,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd443 = new RuleExpression(antecedenteAnd442,term4rule44,RuleConnectionMethodAndMin.get());
	rule44.setAntecedents(antecedenteAnd443);
	rule44.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule44);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Media AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule45 = new Rule("Rule45",ruleBlock);
	RuleTerm term1rule45 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule45 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule45 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule45 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd451 = new RuleExpression(term1rule45,term2rule45,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd452 = new RuleExpression(antecedenteAnd451,term3rule45,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd453 = new RuleExpression(antecedenteAnd452,term4rule45,RuleConnectionMethodAndMin.get());
	rule45.setAntecedents(antecedenteAnd453);
	rule45.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule45);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Media AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule46 = new Rule("Rule46",ruleBlock);
	RuleTerm term1rule46 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule46 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule46 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule46 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd461 = new RuleExpression(term1rule46,term2rule46,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd462 = new RuleExpression(antecedenteAnd461,term3rule46,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd463 = new RuleExpression(antecedenteAnd462,term4rule46,RuleConnectionMethodAndMin.get());
	rule46.setAntecedents(antecedenteAnd463);
	rule46.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule46);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Media AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule47 = new Rule("Rule47",ruleBlock);
	RuleTerm term1rule47 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule47 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule47 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule47 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd471 = new RuleExpression(term1rule47,term2rule47,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd472 = new RuleExpression(antecedenteAnd471,term3rule47,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd473 = new RuleExpression(antecedenteAnd472,term4rule47,RuleConnectionMethodAndMin.get());
	rule47.setAntecedents(antecedenteAnd473);
	rule47.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule47);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Media AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule48 = new Rule("Rule48",ruleBlock);
	RuleTerm term1rule48 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule48 = new RuleTerm(distanciaObj, "Media",false);
	RuleTerm term3rule48 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule48 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd481 = new RuleExpression(term1rule48,term2rule48,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd482 = new RuleExpression(antecedenteAnd481,term3rule48,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd483 = new RuleExpression(antecedenteAnd482,term4rule48,RuleConnectionMethodAndMin.get());
	rule48.setAntecedents(antecedenteAnd483);
	rule48.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule48);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Baja AND poblacionObj IS Alta AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule49 = new Rule("Rule49",ruleBlock);
	RuleTerm term1rule49 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule49 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule49 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule49 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd491 = new RuleExpression(term1rule49,term2rule49,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd492 = new RuleExpression(antecedenteAnd491,term3rule49,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd493 = new RuleExpression(antecedenteAnd492,term4rule49,RuleConnectionMethodAndMin.get());
	rule49.setAntecedents(antecedenteAnd493);
	rule49.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule49);

	// IF numMisiles IS Bajo AND distanciaObj IS Baja AND poblacionObj IS Media AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule50 = new Rule("Rule50",ruleBlock);
	RuleTerm term1rule50 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule50 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule50 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule50 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd501 = new RuleExpression(term1rule50,term2rule50,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd502 = new RuleExpression(antecedenteAnd501,term3rule50,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd503 = new RuleExpression(antecedenteAnd502,term4rule50,RuleConnectionMethodAndMin.get());
	rule50.setAntecedents(antecedenteAnd503);
	rule50.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule50);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Baja AND poblacionObj IS Baja AND esSilo IS No THEN dirDisparo IS Direccion de disparo

	
	Rule rule51 = new Rule("Rule51",ruleBlock);
	RuleTerm term1rule51 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule51 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule51 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule51 = new RuleTerm(esSilo,"No",false);
	RuleExpression antecedenteAnd511 = new RuleExpression(term1rule51,term2rule51,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd512 = new RuleExpression(antecedenteAnd511,term3rule51,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd513 = new RuleExpression(antecedenteAnd512,term4rule51,RuleConnectionMethodAndMin.get());
	rule51.setAntecedents(antecedenteAnd513);
	rule51.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule51);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Baja AND poblacionObj IS Alta AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule52 = new Rule("Rule52",ruleBlock);
	RuleTerm term1rule52 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule52 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule52 = new RuleTerm(poblacionObj, "Alta",false);
	RuleTerm term4rule52 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd521 = new RuleExpression(term1rule52,term2rule52,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd522 = new RuleExpression(antecedenteAnd521,term3rule52,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd523 = new RuleExpression(antecedenteAnd522,term4rule52,RuleConnectionMethodAndMin.get());
	rule52.setAntecedents(antecedenteAnd523);
	rule52.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule52);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Baja AND poblacionObj IS Media AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule53 = new Rule("Rule53",ruleBlock);
	RuleTerm term1rule53 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule53 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule53 = new RuleTerm(poblacionObj, "Media",false);
	RuleTerm term4rule53 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd531 = new RuleExpression(term1rule53,term2rule53,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd532 = new RuleExpression(antecedenteAnd531,term3rule53,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd533 = new RuleExpression(antecedenteAnd532,term4rule53,RuleConnectionMethodAndMin.get());
	rule53.setAntecedents(antecedenteAnd533);
	rule53.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule53);
	
	// IF numMisiles IS Bajo AND distanciaObj IS Baja AND poblacionObj IS Baja AND esSilo IS Si THEN dirDisparo IS Direccion de disparo

	
	Rule rule54 = new Rule("Rule54",ruleBlock);
	RuleTerm term1rule54 = new RuleTerm(numMisiles, "Bajo",false);
	RuleTerm term2rule54 = new RuleTerm(distanciaObj, "Baja",false);
	RuleTerm term3rule54 = new RuleTerm(poblacionObj, "Baja",false);
	RuleTerm term4rule54 = new RuleTerm(esSilo,"Si",false);
	RuleExpression antecedenteAnd541 = new RuleExpression(term1rule54,term2rule54,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd542 = new RuleExpression(antecedenteAnd541,term3rule54,RuleConnectionMethodAndMin.get());
	RuleExpression antecedenteAnd543 = new RuleExpression(antecedenteAnd542,term4rule54,RuleConnectionMethodAndMin.get());
	rule54.setAntecedents(antecedenteAnd543);
	rule54.addConsequent(dirDisparo, "Direccion disparo", false);
	ruleBlock.add(rule54);
	
	
	HashMap<String, RuleBlock> ruleBlocksMap = new HashMap<String,RuleBlock>();
	ruleBlocksMap.put(ruleBlock.getName(), ruleBlock);
	functionBlock.setRuleBlocks(ruleBlocksMap);
	
	fis.getVariable("numMisiles").setValue(1);
	fis.getVariable("distanciaObj").setValue(20);
	fis.getVariable("poblacionObj").setValue(1000);
	fis.getVariable("esSilo").setValue(0);
	fis.getVariable("dirDisparo").setValue(50);
	fis.evaluate();
	
	
	return fis.getVariable("dirDisparo").getValue();

	
	
	
}	

}
