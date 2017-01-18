
import ec.vector.*;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.coevolve.*;
import ec.simple.SimpleFitness;
import game.GameController;

public class PlayerCompete extends Problem implements GroupedProblemForm {
	private static final Logger logger = LogManager.getLogger("AverageLogger");
	private static final Logger logger2 = LogManager.getLogger(GameController.class);

	public void preprocessPopulation(final EvolutionState state, Population pop, boolean[] updateFitness,
			boolean countVictoriesOnly) {
		for (int i = 0; i < pop.subpops.length; i++)
			if (updateFitness[i])
				for (int j = 0; j < pop.subpops[i].individuals.length; j++)
					((SimpleFitness) (pop.subpops[i].individuals[j].fitness)).trials = new ArrayList<Double>();

	}

	public void evaluate(final EvolutionState state, final Individual[] ind, final boolean[] updateFitness,
			final boolean countVictoriesOnly, int[] subpops, final int threadnum) {

		if (ind.length != 2 || updateFitness.length != 2)
			state.output.fatal("The InternalSumProblem evaluates only two individuals at a time.");

		double score1 = 0;
		double score2 = 0;

		FloatVectorIndividual player1;
		FloatVectorIndividual player2;

		player1 = (FloatVectorIndividual) ind[0];
		player2 = (FloatVectorIndividual) ind[1];

		GameController PlayerFitness = new GameController(player1.genome, player2.genome);

		PlayerFitness.startGame();

		score1 = PlayerFitness.player1Result();
		// System.out.println("Score1:" + score1);
		score2 = PlayerFitness.player2Result();
		// System.out.println("Score2:" + score2);

		// decimos al juego quien ha ganado
		if (updateFitness[0]) {
			SimpleFitness fit = ((SimpleFitness) (ind[0].fitness));
			fit.trials.add(score1);
			fit.setFitness(state, score1, false);
			//logger2.debug("Establecido fitness de jugador 1: {}", score1);
		}

		if (updateFitness[1]) {
			SimpleFitness fit = ((SimpleFitness) (ind[1].fitness));
			fit.trials.add(score2);
			fit.setFitness(state, score2, false);
			//logger2.debug("Establecido fitness de jugador 2: {}", score2);
		}

		// System.out.println("Generacion: " + state.generation);
		// System.out.println("Trial Fitness updated");

	}

	public void postprocessPopulation(final EvolutionState state, Population pop, boolean[] updateFitness,
			boolean countVictoriesOnly) {
		for (int i = 0; i < pop.subpops.length; i++) {
			int popSum = 0;
			int popLen = 0;
			double subpop_max = Double.NEGATIVE_INFINITY;
			if (updateFitness[i])
				for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
					SimpleFitness fit = ((SimpleFitness) (pop.subpops[i].individuals[j].fitness));

					// average of the trials we got
					int len = fit.trials.size();
					popLen += fit.trials.size();
					double sum = 0;
					for (int l = 0; l < len; l++) {
						sum += ((Double) (fit.trials.get(l))).doubleValue();
						popSum += ((Double) (fit.trials.get(l))).doubleValue();
					}
					sum /= len;

					// we'll not bother declaring the ideal
					fit.setFitness(state, sum, false);
					subpop_max = Math.max(subpop_max, sum);
					pop.subpops[i].individuals[j].evaluated = true;
				}
			logger.debug("{},{}, {}", state.generation, (double)(popSum/popLen), subpop_max);
		}
	}
}
