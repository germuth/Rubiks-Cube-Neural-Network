package ca.germuth.neural_network.evolution;

public interface Evolvable {
	public int getFitnessRank();
	public void setFitnessRank(int rank);
	public int getDiversityRank();
	public void setDiversityRank(int rank);
	
	//Simulated Annealing most likely
	//if we are doing more than one trial
	//Fitness = (1-a)*AVG performance + (a)*BEST performance
	public double getFitness();
	public void calcFitness();
	//TODO I don't like this name
	public double[] getGenome();
	//public Evolvable toNetwork(double[] genome);
	//copies over???
	public void toNetwork(double[] genome);
	//create copy of this evolvable
	public Evolvable copy();
}
