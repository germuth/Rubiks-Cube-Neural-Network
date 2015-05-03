package ca.germuth.neural_network.trainable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
/**
 * StochasticBackPropagation
 * 
 * Algorithm which trains a trainable by repeatedly adjusting edge weights to reduce error
 * 
 * @author Aaron
 */
public class StochasticBackPropagation {
	private static Random rng = new Random();
	
	private static int TRAINING_EPOCHS = 100;
	private static int TRAINING_ITERATIONS = 50000;
	private static double LEARNING_RATE = 0.01;
	private static double ERROR_THRESHOLD = 30;
	
	public static double runForMinimum(Trainable trainable, ArrayList<TrainingData> trainingData,
			ArrayList<TrainingData> testingData, double learningRate, int trainingIterations, int trainingEpoch) {
		LEARNING_RATE = learningRate;
		TRAINING_ITERATIONS = trainingIterations;
		TRAINING_EPOCHS = trainingEpoch;

		double MINIMUM_ERROR = Integer.MAX_VALUE;

		for (int epoch = 0; epoch < TRAINING_EPOCHS; epoch++) {
			train(trainable, trainingData);
			double testingError = testError(trainable, testingData);

			if (testingError < MINIMUM_ERROR) {
				MINIMUM_ERROR = testingError;
			}
		}
		return MINIMUM_ERROR;
	}

	public static double runForThreshold(Trainable trainable, ArrayList<TrainingData> trainingData,
			ArrayList<TrainingData> testingData, double error_thres, double learningRate, int trainingIterations,
			int trainingEpoch) {
		ERROR_THRESHOLD = error_thres;
		LEARNING_RATE = learningRate;
		TRAINING_ITERATIONS = trainingIterations;
		TRAINING_EPOCHS = trainingEpoch;
		
		for (int epoch = 0; epoch < TRAINING_EPOCHS; epoch++) {
			train(trainable, trainingData);
			double testingError = testError(trainable, testingData);
			if (testingError <= ERROR_THRESHOLD) {
				return testingError;
			}
		}
		return Integer.MAX_VALUE;
	}

	private static void train(Trainable trainable, ArrayList<TrainingData> trainingData){
		// randomly initialize nn
		trainable.randomizeAllEdgeWeights();
		for (int trainingIter = 0; trainingIter < TRAINING_ITERATIONS; trainingIter++) {
			//randomly select one from training data, and change edge weights to reduce error 
			alterEdgeWeights(trainable, trainingData.get(rng.nextInt(trainingData.size())));
		}
	}
	
	//TODO split into more methods
	private static void alterEdgeWeights(Trainable trainable, TrainingData trainD){
		// randomly choose training tuple
		// convert training data to scaled input/output, ie -> [-1, 1]
		double[] inputs = trainable.getSolvable().mapTrainingInput(trainD.getInput());
		double[] expectedOutput = trainable.getSolvable().mapTrainingOutput(
				trainD.getOutput());
		double[] actualOutput = trainable.feedForward(inputs);

		// calculate amount each edge should change based on difference between expected and actual

		//from input to first hiddenlayer, and from hiddenLayer i to hiddenLayer j
		ArrayList<double[][]> delta_layer_to_layer = new ArrayList<double[][]>();
		for(int i = 0; i < trainable.getNumLayers() - 2; i++){
			final int LAYER_1_SIZE = trainable.getLayerSize(i);
			final int LAYER_2_SIZE = trainable.getLayerSize(i+1);
			delta_layer_to_layer.add(new double[LAYER_1_SIZE][LAYER_2_SIZE]);
		}
		//from last hidden layer to output
		int OUTPUT_LAYER_SIZE = trainable.getLayerSize(trainable.getNumLayers() - 1);
		int LAST_HIDDEN_SIZE = trainable.getLayerSize(trainable.getNumLayers() - 2);
		double[][] delta_hidden_to_output = new double[LAST_HIDDEN_SIZE][OUTPUT_LAYER_SIZE];
		
		ArrayList<double[]> delta_hidden_to_bias = new ArrayList<double[]>();
		for(int i = 1; i < trainable.getNumLayers() - 1; i++){
			int HIDDEN_LAYER_SIZE  = trainable.getLayerSize(i);
			delta_hidden_to_bias.add(new double[HIDDEN_LAYER_SIZE]);
			
		}
		double[] delta_output_to_bias = new double[OUTPUT_LAYER_SIZE];

		// HIDDEN TO OUTPUT
		int outputLayer = trainable.getNumLayers() - 1;
		int lastHiddenLayer = outputLayer - 1;

		int outputLayerSize = trainable.getLayerSize(outputLayer);
		int hiddenLayerSize = trainable.getLayerSize(lastHiddenLayer);

		//list of error at each output node
		double[] deltaK = new double[outputLayerSize];

		for (int output = 0; output < outputLayerSize; output++) {
			deltaK[output] = (expectedOutput[output] - actualOutput[output])
					* fPrime(trainable.getNET(output, outputLayer));
			for (int hidden = 0; hidden < hiddenLayerSize; hidden++) {
				delta_hidden_to_output[hidden][output] = deltaK[output]
						* trainable.getACT(hidden, lastHiddenLayer) * LEARNING_RATE;
			}
			// OUTPUT TO BIAS
			delta_output_to_bias[output] = deltaK[output] * 1.0 * LEARNING_RATE;
		}

		//list of error at each node
		LinkedList<double[]> deltaForNodes = new LinkedList<double[]>();
		deltaForNodes.addFirst(deltaK);
		
		// INPUT TO HIDDEN and HIDDEN TO HIDDEN
		for(int i = delta_layer_to_layer.size() - 1; i >= 0; i--){
			double[][] edges = delta_layer_to_layer.get(i);
			
			int thirdLayerSize = deltaForNodes.getFirst().length;
			int secondLayerSize = edges[0].length;
			int firstLayerSize = edges.length;
			
			double[] deltaJ = new double[secondLayerSize];
			for (int secondLayer = 0; secondLayer < secondLayerSize; secondLayer++) {
				double sumError = 0;
				for (int thirdLayer = 0; thirdLayer < thirdLayerSize; thirdLayer++) {
					//error at previous layer
					double temp = deltaForNodes.getFirst()[thirdLayer];
					//times by edge: scaling factor
					temp *= trainable.getEW(secondLayer, i + 1, thirdLayer, i + 2);
					sumError += temp;
				}
				deltaJ[secondLayer] = sumError * fPrime(trainable.getNET(secondLayer, lastHiddenLayer));
				for (int firstLayer = 0; firstLayer < firstLayerSize; firstLayer++) {
					edges[firstLayer][secondLayer] = deltaJ[secondLayer]
							* trainable.getACT(firstLayer, i) * LEARNING_RATE;
				}
				// HIDDEN TO BIAS
				delta_hidden_to_bias.get(i)[secondLayer] = deltaJ[secondLayer] * 1.0 * LEARNING_RATE;
			}		
			deltaForNodes.addFirst(deltaJ);
		}

		// apply edge weights
		
		// INPUT TO HIDDEN and HIDDEN TO HIDDEN
		for(int i = 0; i < delta_layer_to_layer.size(); i++){
			double[][] edges = delta_layer_to_layer.get(i);
			for(int j = 0; j < edges.length; j++){
				for(int k = 0; k < edges[j].length; k++){
					trainable.offsetEW(j, i, k, i + 1, edges[j][k]);
				}
			}
		}

		// hidden to output
		for (int j = 0; j < delta_hidden_to_output.length; j++) {
			for (int k = 0; k < delta_hidden_to_output[j].length; k++) {
				trainable.offsetOuputToHiddenEW(k, j, delta_hidden_to_output[j][k]);
			}
		}
		// hidden to bias
		for(int i = 0; i < delta_hidden_to_bias.size(); i++){
			double[] delta_one_hidden_to_bias = delta_hidden_to_bias.get(i);
			for (int j = 0; j < delta_one_hidden_to_bias.length; j++) {
				trainable.offsetBiasToHiddenEW(j, 0, delta_one_hidden_to_bias[j]);
			}			
		}
		// output to bias
		for (int k = 0; k < delta_output_to_bias.length; k++) {
			trainable.offsetOutputToBiasEW(k, delta_output_to_bias[k]);
		}
	}
	
	private static double testError(Trainable trainable, ArrayList<TrainingData> testingData) {
		// calculate network error across testing data
		double testingError = 0;
		for (TrainingData test : testingData) {
			// convert training data to scaled input/output, ie -> [-1, 1]
			double[] inputs = trainable.getSolvable().mapTrainingInput(test.getInput());
			double[] expectedOutput = trainable.getSolvable().mapTrainingOutput(test.getOutput());
			double[] actualOutput = trainable.feedForward(inputs);

			for (int i = 0; i < actualOutput.length; i++) {
				testingError += Math.pow(expectedOutput[i] - actualOutput[i], 2);
			}
		}
		testingError /= 2;
		return testingError;
	}
	
	private static double fPrime(double net) {
		return 1.0 - Math.pow(Math.tanh(net), 2);
	}
}
