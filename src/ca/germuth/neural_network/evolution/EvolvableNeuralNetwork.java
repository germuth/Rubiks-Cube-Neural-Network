package ca.germuth.neural_network.evolution;

import java.util.ArrayList;

import ca.germuth.neural_network.solvable.Solvable;
import ca.germuth.neural_network.trainable.NeuralNetwork;
import ca.germuth.neural_network.trainable.TrainingData;
/**
 * EvolvableNeuralNetwork
 * 
 * This class contains the logic to allow a Neural Network to be Evolved by a Genetic Algorithm. 
 * Separating into a subclass makes the resulting Neural Network class much cleaner, and 
 * this class is now very focused. 
 * @author Aaron
 *
 */
public class EvolvableNeuralNetwork extends NeuralNetwork implements Evolvable{
	
	private double rawFitness;
	private int fitnessRank;
	private int diversityRank;
	private Double[] genome;
	
	public EvolvableNeuralNetwork(Solvable solvable, int numInput, int numOutput, int numHiddenLayer, int[] hiddenLayerSize) {
		super(solvable, numInput, numOutput, numHiddenLayer, hiddenLayerSize);
		genome = this.weightList().toArray(new Double[1]);
	}
	
	public EvolvableNeuralNetwork(NeuralNetwork nn){
		super(nn.getSolvable(), nn.layerSizes(), nn.weightList());
		genome = nn.weightList().toArray(new Double[1]);
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
	public Double[] getGenome() {
		return genome;
	}

	@Override
	public void toNetwork() {
		ArrayList<Double> weights = new ArrayList<Double>();
		for(int i = 0; i < genome.length; i++){
			weights.add(genome[i]);
		}
		this.updateEdgeWeights(weights);
	}

	@Override
	public Evolvable copy() {
		return new EvolvableNeuralNetwork(this);
	}

	@Override
	public void calcFitness(ArrayList<TrainingData> training) {
		// calculate network error across testing data
		double testingError = 0;
		for (TrainingData test : training) {
			// convert training data to scaled input/output, ie -> [-1, 1]
			double[] inputs = getSolvable().mapTrainingInput(test.getInput());
			double[] expectedOutput = getSolvable().mapTrainingOutput(test.getOutput());
			//copy edges over
			toNetwork();
			double[] actualOutput = feedForward(inputs);

			for (int i = 0; i < actualOutput.length; i++) {
				testingError += Math.pow(expectedOutput[i] - actualOutput[i], 2);
			}
		}
		double mean_squared_error = testingError / training.size();
		//take inverse to larger fitness value is better performance
		this.rawFitness = 1 / mean_squared_error;
	}

}
