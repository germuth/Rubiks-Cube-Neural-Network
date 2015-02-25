package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.Random;

public class StochasticBackPropagation {
	private static final int TRAINING_EPOCHS = 100;
	private static final int TRAINING_ITERATIONS = 1000;
	private static final double LEARNING_RATE = 0.2;
	private static final double ERROR_THRESHOLD = 0.01;
	
	public static void run(Trainable trainable, ArrayList<TrainingData> trainingData, ArrayList<TrainingData> testingData){
		Random rng = new Random();
		int trainingDataSize = trainingData.size();
		
		for(int epoch = 0; epoch < TRAINING_EPOCHS; epoch++){
			//randomly initialize nn
			trainable.randomizeAllEdgeWeights();
			for(int trainingIter = 0; trainingIter < TRAINING_ITERATIONS; trainingIter++){
				System.out.println(epoch + " " + trainingIter);
				//randomly choose training tuple
				TrainingData trainD = trainingData.get( rng.nextInt(trainingDataSize) ); 
				
				//convert training data to scaled input/output, ie -> [-1, 1]
				double[] inputs = trainable.getSolveable().mapTrainingInput(trainD.getInput());
				double[] expectedOutput = trainable.getSolveable().mapTrainingOutput(trainD.getOutput());
				double[] actualOutput = trainable.feedForward( inputs );
				
				//calculate amount each edge should change based on difference between expected and actual
				final int INPUT_SIZE = trainable.getLayerSize(0);
				final int HIDDEN_SIZE = trainable.getLayerSize(1);
				final int OUTPUT_SIZE = trainable.getLayerSize(2);
				
				double[][] delta_input_to_hidden = new double[INPUT_SIZE][HIDDEN_SIZE];
				double[][] delta_hidden_to_output = new double[HIDDEN_SIZE][OUTPUT_SIZE];
				double[] delta_hidden_to_bias = new double[HIDDEN_SIZE];
				double[] delta_output_to_bias = new double[OUTPUT_SIZE];
				
				//HIDDEN TO OUTPUT
				int outputLayer = trainable.getNumLayers() - 1;
				int hiddenLayer = outputLayer - 1;
				
				int outputLayerSize = trainable.getLayerSize(outputLayer);
				int hiddenLayerSize = trainable.getLayerSize(hiddenLayer);
				
				double[] deltaK = new double[outputLayerSize];
				
				for(int output = 0; output < outputLayerSize; output++){
					deltaK[output] = (expectedOutput[output] - actualOutput[output]) * fPrime( trainable.getNET(output, outputLayer));
					for(int hidden = 0; hidden < hiddenLayerSize ; hidden++){
						delta_hidden_to_output[hidden][output] = deltaK[output] * trainable.getACT(hidden, hiddenLayer) * LEARNING_RATE;
					}
					//OUTPUT TO BIAS
					delta_output_to_bias[output] = deltaK[output] * 1.0 * LEARNING_RATE;
				}
				
				//INPUT TO HIDDEN
				int inputLayer = 0;
				hiddenLayer = inputLayer + 1;
				hiddenLayerSize = trainable.getLayerSize(hiddenLayer);
				double[] deltaJ = new double[hiddenLayerSize];
				
				for(int hidden = 0; hidden < hiddenLayerSize; hidden++){
					double sumError = 0;
					for(int output = 0; output < outputLayerSize; output++){
						sumError += (deltaK[output] * trainable.getOuputToHiddenEW(output, hidden));
					}
					deltaJ[hidden] = sumError * fPrime( trainable.getNET(hidden, hiddenLayer));
					for(int input = 0; input < trainable.getLayerSize(inputLayer); input++){
						delta_input_to_hidden[input][hidden] = deltaJ[hidden] * trainable.getACT(input, inputLayer) * LEARNING_RATE;
					}
					//HIDDEN TO BIAS
					delta_hidden_to_bias[hidden] = deltaJ[hidden] * 1.0 * LEARNING_RATE;
				}
				
				//apply edge weights
				//input to hidden
				for(int i = 0; i < delta_input_to_hidden.length; i++){
					for(int j = 0; j < delta_input_to_hidden[i].length; j++){
						trainable.offsetHiddenToInputEW(j, i, delta_input_to_hidden[i][j]);
					}
				}
				//hidden to output
				for(int j = 0; j < delta_hidden_to_output.length; j++){
					for(int k = 0; k < delta_hidden_to_output[j].length; k++){
						trainable.offsetOuputToHiddenEW(k, j, delta_hidden_to_output[j][k]);
					}
				}
				//hidden to bias
				for(int j = 0; j < delta_hidden_to_bias.length; j++){
					trainable.offsetBiasToHiddenEW(j, 0, delta_hidden_to_bias[j]);
				}
				//output to bias
				for(int k = 0; k < delta_output_to_bias.length; k++){
					trainable.offsetOutputToBiasEW(k, delta_output_to_bias[k]);
				}
				
				//TODO temp for debugging
				//calculate network error across testing data
				double testingError = 0;
				for(TrainingData test: testingData){
					//convert training data to scaled input/output, ie -> [-1, 1]
					double[] inputs2 = trainable.getSolveable().mapTrainingInput(test.getInput());
					double[] expectedOutput2 = trainable.getSolveable().mapTrainingOutput(test.getOutput());
					double[] actualOutput2 = trainable.feedForward( inputs2 );
						
					//TODO: must be summed across all output neurons i believe
					for(int i = 0; i < actualOutput2.length; i++){
						testingError += Math.pow(expectedOutput2[i] - actualOutput2[i], 2);
					}
					testingError /= 2;
				} 
				System.out.println(testingError);
				
			}
			
			//calculate network error across testing data
			double testingError = 0;
			for(TrainingData test: testingData){
				//convert training data to scaled input/output, ie -> [-1, 1]
				double[] inputs = trainable.getSolveable().mapTrainingInput(test.getInput());
				double[] expectedOutput = trainable.getSolveable().mapTrainingOutput(test.getOutput());
				double[] actualOutput = trainable.feedForward( inputs );
					
				//TODO: must be summed across all output neurons i believe
				for(int i = 0; i < actualOutput.length; i++){
					testingError += Math.pow(expectedOutput[i] - actualOutput[i], 2);
				}
				testingError /= 2;
			} 
			System.out.println(testingError);
			if(testingError <= ERROR_THRESHOLD){
				return;
			}
		}
	}
	//TODO vector Diff should only compare one output nodes value not all of them??!
//	private static double vectorDiff(double[] vec1, double vec2[]){
//		if(vec1.length != vec2.length){
//			throw new IllegalArgumentException("Both vectors must have the same size");
//		}
//		double[] diffVec = new double[vec1.length];
//		for(int i = 0; i < vec1.length; i++){
//			diffVec[i] = vec1[i] - vec2[i];
//		}
//		//calculate magnitude
//		double mag = 0;
//		for(int i = 0; i < diffVec.length; i++){
//			mag += (diffVec[i] * diffVec[i]);
//		}
//		return Math.sqrt(mag);
//	}
	
	private static double fPrime(double net){
		return 1.0 - (Math.tanh(net) * Math.tanh(net));
	}
}
