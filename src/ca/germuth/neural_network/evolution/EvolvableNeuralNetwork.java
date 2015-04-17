package ca.germuth.neural_network.evolution;

import ca.germuth.neural_network.solvable.Solvable;
import ca.germuth.neural_network.trainable.NeuralNetwork;

public class EvolvableNeuralNetwork extends NeuralNetwork implements Evolvable{
	
	private double rawFitness;
	private int fitnessRank;
	private int diversityRank;
	private double[] genome;
	
	public EvolvableNeuralNetwork(Solvable solvable, int numInput, int numOutput,int numHiddenLayer, int[] hiddenLayerSize) {
		super(solvable, numInput, numOutput, numHiddenLayer, hiddenLayerSize);
	}
	
	public EvolvableNeuralNetwork(NeuralNetwork nn){
		super(nn.getSolvable(), nn.getNumInputNeurons(), nn.getNumOutputNeurons(), nn.getNumLayers() - 2, );
	}
	
	@Override
	public int getFitnessRank() {
		return fitnessRank;
	}

	@Override
	public void setFitnessRank(int rank) {
		fitnessRank = rank;
	}

	@Override
	public int getDiversityRank() {
		return diversityRank;
	}

	@Override
	public void setDiversityRank(int rank) {
		diversityRank = rank;
	}

	@Override
	public double getFitness() {
		return rawFitness;
	}

	@Override
	public void calcFitness() {
		rawFitness = 0.0;
	}

	@Override
	public double[] getGenome() {
		return genome;
	}

	@Override
	public void toNetwork(double[] genome) {
		// TODO Auto-generated method stub
	}

	@Override
	public Evolvable copy() {
		return new EvolvableNeuralNetwork(this);
	}

}
