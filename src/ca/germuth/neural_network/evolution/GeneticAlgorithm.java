package ca.germuth.neural_network.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import ca.germuth.neural_network.trainable.TrainingData;
/**
 * Genetic Algorithm
 * 
 * @author Aaron
 */
public class GeneticAlgorithm {
	private static int POPULATION_SIZE = 400;
	private static int ITERATIONS = 500;
	private static double POPULATION_ELITE_PERCENT = 0.2;
	private static double POPULATION_MUTATION_PERCENT = 0.4;
	private static double POPULATION_CROSSOVER_PERCENT = 0.4;
	private static Random rng = new Random();
	
	public static ArrayList<TrainingData> trainingData;
	
	/**
	 * Evolves a population of graphs towards high fitness.
	 * @param randomPopulation, random population of Evolvables
	 * @param training, training data set used to assess fitness of each Evolvable
	 * @return The highest performing individual on the last iteration
	 */
	public static Evolvable run(ArrayList<Evolvable> randomPopulation, ArrayList<TrainingData> training){
		ArrayList<Evolvable> population = randomPopulation;
		trainingData = training;
		
		for(int i = 0; i < ITERATIONS; i++){
			population = evolve(population);
		}                                                                               
		
		population = sortByFitness(population);
		System.out.println("BEST: " + population.get(0).getFitness());
		return population.get(0);
	}
	
	//one iteration of the GA
	private static ArrayList<Evolvable> evolve(ArrayList<Evolvable> population){
		//sort by raw fitness
		population = sortByFitness(population);
		
		final ArrayList<Evolvable> elite = new ArrayList<Evolvable>();
		//use RNG to get 1st elite genome based only on raw fitness
		//iterate over all and assign probselection and pick one
		elite.add(population.get(selectGenomeProbabilistically(population.size())));
		
		for(int i = 0; i < ((int)POPULATION_SIZE*POPULATION_ELITE_PERCENT) - 1; i++){
			//sort by fitness + diversity
			//hrm maybe each evolvable needs a fitness rank, diversity rank, prob of selection etc.
			//that would probably be easier
			Collections.sort(population, new Comparator<Evolvable>(){
				@Override
				public int compare(Evolvable o1, Evolvable o2){
					//second first so we order higher numbers first
					//higher numbers are more different, more diversity
					return Double.compare(calcDiversity(o2, elite), calcDiversity(o1, elite));
				}
			});
			
			for(int j = 0; j < population.size(); j++){
				population.get(j).setDiversityRank((j+1));
			}
			
			Collections.sort(population, new Comparator<Evolvable>(){
				@Override
				public int compare(Evolvable o1, Evolvable o2){
					return Integer.compare(o1.getFitnessRank() + o1.getDiversityRank(), o2.getFitnessRank() + o2.getDiversityRank());
				}
			});
			
			elite.add( population.get( selectGenomeProbabilistically(population.size()) ) );
		}
		
		//mutate elite
		for(int i = 0; i < (int)POPULATION_SIZE*POPULATION_MUTATION_PERCENT; i++){
			//TODO this is wrong, you cannot add result back to elite
			//then you have the chance of mutating the same genome twice
			//need to make a new list called mutated
			//same for crossover, because i can crossover mutated genomes
			Evolvable random = elite.get( rng.nextInt(elite.size()) );
			//TODO how do i do this?
			Evolvable mutated = random.copy();
			
			//set approx 10% of genes to be changed
			for(int j = 0; j < mutated.getGenome().length; j++){
				if( rng.nextDouble() <= 0.10 ){
					
					//i get way better results with perturbation rather than reset value
					//maybe this should be between -1/d and 1/d
//					mutated.getGenome()[j] = (rng.nextDouble() * 2 ) - 1.0;	
					
					if( rng.nextDouble() <= 0.50){
						mutated.getGenome()[j] += mutated.getGenome()[j] * 0.10;						
					}else{
						mutated.getGenome()[j] -= mutated.getGenome()[j] * 0.10;
					}
				}
			}
			elite.add(mutated);
		}
		
		//crossover
		for(int i = 0; i < ((int)POPULATION_SIZE*POPULATION_CROSSOVER_PERCENT) / 2; i++){
			//avoid getting the same genome twice
			int first = rng.nextInt(elite.size());
			int second = rng.nextInt(elite.size());
			while(first == second){
				second = rng.nextInt(elite.size());
			}
			
			Evolvable random1 = elite.get( first );
			Evolvable random2 = elite.get( second );
			Evolvable child1 = random1.copy();
			Evolvable child2 = random2.copy();
			for(int j = 0; j < random1.getGenome().length; j++){
				if( rng.nextDouble() <= 0.50 ){
					child1.getGenome()[j] = random2.getGenome()[j];
					child2.getGenome()[j] = random1.getGenome()[j];
				}
			}
			elite.add(child1);
			elite.add(child2);
		}
		
		assert(elite.size() == population.size());
		return elite;
	}
	
	/**
	 * Calculate fitness for each member in the population. Sort them by fitness
	 * and assign fitness ranks
	 */
	public static ArrayList<Evolvable> sortByFitness(ArrayList<Evolvable> population) {
		for (Evolvable e : population) {
			e.calcFitness(trainingData);
		}
		// sort by raw fitness
		Collections.sort(population, new Comparator<Evolvable>() {
			@Override
			public int compare(Evolvable o1, Evolvable o2) {
				//put second first so that larger numbers occur first
				//highest fitness first
				return Double.compare(o2.getFitness(), o1.getFitness());
			}
		});
		
		for(int i = 0; i < population.size(); i++){
			population.get(i).setFitnessRank((i+1));
		}
		
		return population;
	}
	
	/**
	 * Rather than always choose rank 1, we highly favour genomes
	 * towards the start of the list and randomly choose one. 
	 * First genome has 0.667 chance
	 * Second genome has 0.22 chance
	 * etc.
	 */
	private static int selectGenomeProbabilistically(int maxGenome){
		//this method works by repeatedly adding next probability to double
		//if random is 0.80
		//then 0.667 + 0.225 >= 0.80 so 2nd genome is chosen
		//if random is 0.50
		//then 0.667 >= 0.50 so 1st genome is chosen
		//etc.
		double random = rng.nextDouble();
		
		double previousCumulativeProb = 0.0;
		int selected = -1;
		
		while(random > previousCumulativeProb && selected < maxGenome - 1){
			previousCumulativeProb +=  0.667 * (1 - previousCumulativeProb);
			selected++;
		}
		return selected;
	}
	
	public static double calcDiversity(Evolvable novel, ArrayList<Evolvable> list){
		double score = 0.0;
		for(Evolvable curr: list){
			score += vectorDifference(curr.getGenome(), novel.getGenome());
		}
		return score;
	}
	
	/**
	 * Returns vector difference squared to save some computation since
	 * we are only comparing them relatively
	 */
	public static double vectorDifference(Double[] first, Double[] second){
		double diff = 0.0;
		for (int i = 0; i < first.length; i++) {
			diff += Math.pow(first[i] - second[i], 2);
		}
		
		//We do not need to square root since we are comparing them to each other only relatively
		//saves some computation
//		diff = Math.sqrt(diff);
		return diff;
	}

	public static int getPOPULATION_SIZE() {
		return POPULATION_SIZE;
	}

	public static void setPOPULATION_SIZE(int pOPULATION_SIZE) {
		POPULATION_SIZE = pOPULATION_SIZE;
	}

	public static int getITERATIONS() {
		return ITERATIONS;
	}

	public static void setITERATIONS(int iTERATIONS) {
		ITERATIONS = iTERATIONS;
	}

	public static double getPOPULATION_ELITE_PERCENT() {
		return POPULATION_ELITE_PERCENT;
	}

	public static void setPOPULATION_ELITE_PERCENT(double pOPULATION_ELITE_PERCENT) {
		POPULATION_ELITE_PERCENT = pOPULATION_ELITE_PERCENT;
	}

	public static double getPOPULATION_MUTATION_PERCENT() {
		return POPULATION_MUTATION_PERCENT;
	}

	public static void setPOPULATION_MUTATION_PERCENT(double pOPULATION_MUTATION_PERCENT) {
		POPULATION_MUTATION_PERCENT = pOPULATION_MUTATION_PERCENT;
	}

	public static double getPOPULATION_CROSSOVER_PERCENT() {
		return POPULATION_CROSSOVER_PERCENT;
	}

	public static void setPOPULATION_CROSSOVER_PERCENT(double pOPULATION_CROSSOVER_PERCENT) {
		POPULATION_CROSSOVER_PERCENT = pOPULATION_CROSSOVER_PERCENT;
	}
}
