package ca.germuth.neural_network.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgorithm {
	private static final int POPULATION_SIZE = 1000;
	private static final int ITERATIONS = 1000;
	private static Random rng = new Random();
	public static void run(){
		ArrayList<Evolvable> population = new ArrayList<Evolvable>();
		//start with random population
		for(int i = 0; i < POPULATION_SIZE; i++){
			population.add(null);
		}
		
		for(int i = 0; i < ITERATIONS; i++){
			population = evolve(population);
		}                                                                               
		
	}
	
	private static ArrayList<Evolvable> evolve(ArrayList<Evolvable> population){
		//print average fitness
		double avg = 0.0;
		for(Evolvable e: population){
			e.calcFitness();
			avg += e.getFitness();
		}
		System.out.println(avg/population.size());

		//sort by raw fitness
		Collections.sort(population, new Comparator<Evolvable>(){
			@Override
			public int compare(Evolvable o1, Evolvable o2) {
				return Double.compare(o1.getFitness(), o2.getFitness());
			}
		});
		for(int i = 0; i < population.size(); i++){
			population.get(i).setFitnessRank((i+1));
		}
		
		final ArrayList<Evolvable> elite = new ArrayList<Evolvable>();
		//use RNG to get 1st elite genome based only on raw fitness
		//iterate over all and assign probselection and pick one
		elite.add(population.get(selectGenomeProbabilistically(population.size())));
		
		//TODO make 20 percent
		for(int i = 0; i < 19; i++){
			//sort by fitness + diversity
			//hrm maybe each evolvable needs a fitness rank, diversity rank, prob of selection etc.
			//that would probably be easier
			Collections.sort(population, new Comparator<Evolvable>(){
				@Override
				public int compare(Evolvable o1, Evolvable o2){
					double first = 0.0;
					double second = 0.0;
					for(Evolvable genome: elite){
						first += vectorDifference(genome, o1);
						second += vectorDifference(genome, o2);
					}
					return Double.compare(first, second);
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
		for(int i = 0; i < 40; i++){
			Evolvable random = elite.get( rng.nextInt(elite.size()) );
			//TODO how do i do this?
			Evolvable mutated = random.copy();
			
			//set approx 10% of genes to be changed
			for(int j = 0; j < mutated.getGenome().length; j++){
				if( rng.nextDouble() <= 0.10 ){
					//maybe this should be between -1/d and 1/d
					//also this should sometimse just perterb not completely reset?
					mutated.getGenome()[j] = (rng.nextDouble() * 2 ) - 1.0;					
				}
			}
			elite.add(mutated);
		}
		
		//crossover
		for(int i = 0; i < (20 / 2); i++){
			//TODO can get same genome twice
			Evolvable random1 = elite.get( rng.nextInt(elite.size()) );
			Evolvable random2 = elite.get( rng.nextInt(elite.size()) );
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
	
	//this may be moved into evolvable object? then it can't be interface though...
	//does this work?
	private static int selectGenomeProbabilistically(int maxGenome){
		double random = rng.nextDouble();
		
		double previousCumulativeProb = 0.0;
		int selected = 0;
		
		while(random > previousCumulativeProb && selected < maxGenome){
			previousCumulativeProb +=  0.667 * (1 - previousCumulativeProb);
			selected++;
		}
		return selected;
	}
	
	private static double vectorDifference(Evolvable one, Evolvable two){
		double[] f = one.getGenome();
		double[] s = two.getGenome();
		
		double diff = 0.0;
		for (int i = 0; i < f.length; i++) {
			diff += Math.pow(f[i] - s[i], 2);
		}
		
		//We do not need to square root since we are comparing them to each other only relatively
		//saves some computation
//		diff = Math.sqrt(diff);
		return diff;
	}

}
