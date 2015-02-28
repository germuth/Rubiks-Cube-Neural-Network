package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.Random;

public class StochasticBackPropagation {
	private static Random rng = new Random();
	private static int TRAINING_EPOCHS = 100;
	private static int TRAINING_ITERATIONS = 100000;//25000;//5000;
	// with 500 i had 0.2-0.3-0.4
	// with 5000 i had 0.002-0.012-0.008
	private static double LEARNING_RATE = 0.01;//0.05;//;0.1;
	private static double ERROR_THRESHOLD = 2750;//30(This works with 2x2x2 2 moves);//100;//0.09;// 1.0; //4.0 using 1 for just 1 move; //I
														// got lazy, this should work though 3.0;
														// //XOR: 0.01;

	public static double runForMinimum(Trainable trainable, ArrayList<TrainingData> trainingData,
			ArrayList<TrainingData> testingData, double learningRate, int trainingIterations, int trainingEpoch) {
		LEARNING_RATE = learningRate;
		TRAINING_ITERATIONS = trainingIterations;
		TRAINING_EPOCHS = trainingEpoch;

		double MINIMUM_ERROR = Integer.MAX_VALUE;

		for (int epoch = 0; epoch < TRAINING_EPOCHS; epoch++) {
			train(trainable, trainingData);
			double testingError = testError(trainable, testingData);

//			System.out.println(epoch + " " + testingError);
			if (testingError < MINIMUM_ERROR) {
				MINIMUM_ERROR = testingError;
			}
		}
		return MINIMUM_ERROR;
	}

	public static double runForThreshold(Trainable trainable, ArrayList<TrainingData> trainingData,
			ArrayList<TrainingData> testingData) {
		for (int epoch = 0; epoch < TRAINING_EPOCHS; epoch++) {
			train(trainable, trainingData);
			double testingError = testError(trainable, testingData);

			if (testingError <= ERROR_THRESHOLD) {
//				testError(trainable, testingData);
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
	
	private static void alterEdgeWeights(Trainable trainable, TrainingData trainD){
		// randomly choose training tuple
		// convert training data to scaled input/output, ie -> [-1, 1]
		double[] inputs = trainable.getSolvable().mapTrainingInput(trainD.getInput());
		double[] expectedOutput = trainable.getSolvable().mapTrainingOutput(
				trainD.getOutput());
		double[] actualOutput = trainable.feedForward(inputs);

		// calculate amount each edge should change based on difference between expected and actual
		final int INPUT_SIZE = trainable.getLayerSize(0);
		final int HIDDEN_SIZE = trainable.getLayerSize(1);
		final int OUTPUT_SIZE = trainable.getLayerSize(2);

		double[][] delta_input_to_hidden = new double[INPUT_SIZE][HIDDEN_SIZE];
		double[][] delta_hidden_to_output = new double[HIDDEN_SIZE][OUTPUT_SIZE];
		double[] delta_hidden_to_bias = new double[HIDDEN_SIZE];
		double[] delta_output_to_bias = new double[OUTPUT_SIZE];

		// HIDDEN TO OUTPUT
		int outputLayer = trainable.getNumLayers() - 1;
		int hiddenLayer = outputLayer - 1;

		int outputLayerSize = trainable.getLayerSize(outputLayer);
		int hiddenLayerSize = trainable.getLayerSize(hiddenLayer);

		double[] deltaK = new double[outputLayerSize];

		for (int output = 0; output < outputLayerSize; output++) {
			deltaK[output] = (expectedOutput[output] - actualOutput[output])
					* fPrime(trainable.getNET(output, outputLayer));
			for (int hidden = 0; hidden < hiddenLayerSize; hidden++) {
				delta_hidden_to_output[hidden][output] = deltaK[output]
						* trainable.getACT(hidden, hiddenLayer) * LEARNING_RATE;
			}
			// OUTPUT TO BIAS
			delta_output_to_bias[output] = deltaK[output] * 1.0 * LEARNING_RATE;
		}

		// INPUT TO HIDDEN
		int inputLayer = 0;
		hiddenLayer = inputLayer + 1;
		hiddenLayerSize = trainable.getLayerSize(hiddenLayer);
		double[] deltaJ = new double[hiddenLayerSize];

		for (int hidden = 0; hidden < hiddenLayerSize; hidden++) {
			double sumError = 0;
			for (int output = 0; output < outputLayerSize; output++) {
				sumError += (deltaK[output] * trainable.getOuputToHiddenEW(output, hidden));
			}
			deltaJ[hidden] = sumError * fPrime(trainable.getNET(hidden, hiddenLayer));
			for (int input = 0; input < trainable.getLayerSize(inputLayer); input++) {
				delta_input_to_hidden[input][hidden] = deltaJ[hidden]
						* trainable.getACT(input, inputLayer) * LEARNING_RATE;
			}
			// HIDDEN TO BIAS
			delta_hidden_to_bias[hidden] = deltaJ[hidden] * 1.0 * LEARNING_RATE;
		}

		// apply edge weights
		// input to hidden
		for (int i = 0; i < delta_input_to_hidden.length; i++) {
			for (int j = 0; j < delta_input_to_hidden[i].length; j++) {
				trainable.offsetHiddenToInputEW(j, i, delta_input_to_hidden[i][j]);
			}
		}
		// hidden to output
		for (int j = 0; j < delta_hidden_to_output.length; j++) {
			for (int k = 0; k < delta_hidden_to_output[j].length; k++) {
				trainable.offsetOuputToHiddenEW(k, j, delta_hidden_to_output[j][k]);
			}
		}
		// hidden to bias
		for (int j = 0; j < delta_hidden_to_bias.length; j++) {
			trainable.offsetBiasToHiddenEW(j, 0, delta_hidden_to_bias[j]);
		}
		// output to bias
		for (int k = 0; k < delta_output_to_bias.length; k++) {
			trainable.offsetOutputToBiasEW(k, delta_output_to_bias[k]);
		}

		// TODO temp for debugging
		// calculate network error across testing data
		// double testingError = 0;
		// for(TrainingData test: testingData){
		// //convert training data to scaled input/output, ie -> [-1, 1]
		// double[] inputs2 = trainable.getSolvable().mapTrainingInput(test.getInput());
		// double[] expectedOutput2 =
		// trainable.getSolvable().mapTrainingOutput(test.getOutput());
		// double[] actualOutput2 = trainable.feedForward( inputs2 );
		//
		// //TODO: must be summed across all output neurons i believe
		// for(int i = 0; i < actualOutput2.length; i++){
		// testingError += Math.pow(expectedOutput2[i] - actualOutput2[i], 2);
		// }
		// testingError /= 2;
		// }
		// System.out.println(testingError);
		// pw.print(testingError + ",");
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
