package ca.germuth.neural_network.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GeneticAlgorithm {
	private static final int POPULATION_SIZE = 1000;
	private static final int ITERATIONS = 1000;
	public static void run(){
		ArrayList<Evolvable> population = new ArrayList<Evolvable>();
		//start with random population
		for(int i = 0; i < POPULATION_SIZE; i++){
			population.add(null);
		}
		
		for(int i = 0; i < ITERATIONS; i++){
			evolve(population);
		}
		
	}
	
	private static void evolve(ArrayList<Evolvable> population){
		for(Evolvable e: population){
			e.calcFitness();
		}
		//sort by raw fitness
		Collections.sort(population, new Comparator<Evolvable>(){
			@Override
			public int compare(Evolvable o1, Evolvable o2) {
				return Double.compare(o1.calcFitness(), o2.calcFitness());
			}
		});
		//use RNG to get 1st elite genome based only on raw fitness
		//iterate over all and assign probselection and pick one
		
		//sort by fitness + diversity
		//hrm maybe each evolvable needs a fitness rank, diversity rank, prob of selection etc.
		//that would probably be easier
		
		
		//use RNG to get 20% i believe
		//remaining chosen probabilitstically by prob selection
		
	}
	
	//this may be moved into evolvable object? then it can't be interface though...
	private static double previousCumulativeProbability = 0.0;
	private static double probSelection(){
		double ans = 0.667 * (1 - previousCumulativeProbability);
		previousCumulativeProbability = ans;
		return ans;
	}
}
