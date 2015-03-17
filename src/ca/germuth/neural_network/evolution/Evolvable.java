package ca.germuth.neural_network.evolution;

public interface Evolvable {
	//Simulated Annealing most likely
	//if we are doing more than one trial
	//Fitness = (1-a)*AVG performance + (a)*BEST performance
	public double calcFitness();
	public double calcDiversity();
	public double[] toGenome();
	//public Evolvable toNetwork(double[] genome);
	//copies over???
	public void toNetwork(double[] genome);
}
