package ca.germuth.neural_network;

import java.util.ArrayList;

import ca.germuth.neural_network.evolution.Evolvable;
import ca.germuth.neural_network.evolution.EvolvableNeuralNetwork;
import ca.germuth.neural_network.evolution.GeneticAlgorithm;
import ca.germuth.neural_network.solvable.XOR;
import ca.germuth.neural_network.solvable.cube.Cube;
import ca.germuth.neural_network.trainable.NeuralNetwork;
import ca.germuth.neural_network.trainable.StochasticBackPropagation;
import ca.germuth.neural_network.trainable.TrainingData;
/**
 * UnitTest
 * 
 * This class contains some unit test to test most of the public functionality
 * in this project
 * @author Aaron
 */
public class UnitTest {

	private static ArrayList<TrainingData> trainingXOR = FileHandler.readTrainingData("XOR");
	private static ArrayList<TrainingData> testingXOR = FileHandler.readTrainingData("XOR");
	private static ArrayList<TrainingData> training = FileHandler.readTrainingData("CUBE");
	private static ArrayList<TrainingData> testing = FileHandler.readTrainingData("CUBE");
	
	private static int[] hiddenSize = {2};
	private static NeuralNetwork XOR_NN = new NeuralNetwork(new XOR(), 2, 1, 1, hiddenSize);
	private static int[] hiddenSize2= {12};
	private static NeuralNetwork CUBE_NN = new NeuralNetwork(new Cube(2), 4 * 6 * 3, 4, 1, hiddenSize2);
	
	public static void testAll() {
		if(!test_XOR_training_data()){
			System.out.println("UNIT TEST FAIL: XOR unable to be read from file");
		}else if(!test_XOR_SBP_Error()){
			System.out.println("UNIT TEST FAIL: XOR had bad error");
		}else if(!test_XOR_output_count()){
			System.out.println("UNIT TEST FAIL: XOR has invalid number of output neurons");
		}else if(!test_XOR_correct_outputs()){
			System.out.println("UNIT TEST FAIL: XOR produced wrong output.");
		}else if(!test_CUBE_training_data()){
			System.out.println("UNIT TEST FAIL: CUBE unable to be read from file");
		}else if(!test_CUBE_SBP_Error()){
			System.out.println("UNIT TEST FAIL: Cube had bad error. It was insufficiently trained");
		}else if(!test_vector_difference()){
			System.out.println("UNIT TEST FAIL: Vector Difference has invalid value");
		}else if(!test_sort_by_fitness()){
			System.out.println("UNIT TEST FAIL: NN not properly sorted by raw Fitness");
		}else if(!test_XOR_GA()){
			System.out.println("UNIT TEST FAIL: GeneticAlgorithm failing to train NN adequately");
		}
		System.out.println("UNIT TESTS FINISHED");
	}

	public static boolean test_XOR_GA(){
		ArrayList<Evolvable> list = new ArrayList<Evolvable>();
		int[] hiddenNodes = {2};
		for(int i = 0; i < GeneticAlgorithm.getPOPULATION_SIZE(); i++){
			EvolvableNeuralNetwork next = new EvolvableNeuralNetwork(new XOR(), 2, 1, 1, hiddenNodes);
			next.randomizeAllEdgeWeights();
			list.add(next);
		}
		
		EvolvableNeuralNetwork evolved = (EvolvableNeuralNetwork) GeneticAlgorithm.run(list, trainingXOR);
		//TODO increase this
		return evolved.getFitness() >= 1.0;
	}
	
	public static boolean test_diversity_score(){
		int[] hiddenNodes = {2};
		ArrayList<Double> ones = new ArrayList<Double>();
		for(int i = 0; i < 9; i++){
			ones.add(1.0);
		}
		ArrayList<Double> negative_ones = new ArrayList<Double>();
		for(int i = 0; i < 9; i++){
			negative_ones.add(-1.0);
		}
		
		EvolvableNeuralNetwork nn1 = new EvolvableNeuralNetwork(new XOR(), 2, 1, 1, hiddenNodes);
		EvolvableNeuralNetwork nn2 = new EvolvableNeuralNetwork(new XOR(), 2, 1, 1, hiddenNodes);
		
		ArrayList<Evolvable> comparedTo = new ArrayList<Evolvable>();
		comparedTo.add(nn1);
		
		if(GeneticAlgorithm.calcDiversity(nn1, comparedTo) != 0.0){
			return false;
		}else if(GeneticAlgorithm.calcDiversity(nn2, comparedTo) != 36.0){
			return false;
		}
		return true;
		
	}
	public static boolean test_vector_difference(){
		Double[] first = {1.0, 1.0};
		Double[] second = {-1.0, -1.0};
		if(GeneticAlgorithm.vectorDifference(first, first) != 0.0){
			return false;
		} else if(GeneticAlgorithm.vectorDifference(first, second) != 8.0){
			return false;
		}
		return true;
	}
	
	public static boolean test_sort_by_fitness(){
		ArrayList<Evolvable> list = new ArrayList<Evolvable>();
		int[] hiddenNodes = {2};
		for(int i = 0; i < 10; i++){
			EvolvableNeuralNetwork next = new EvolvableNeuralNetwork(new XOR(), 2, 1, 1, hiddenNodes);
			next.randomizeAllEdgeWeights();
			list.add(next);
		}
		GeneticAlgorithm.trainingData = trainingXOR;
		GeneticAlgorithm.sortByFitness(list);
		for(int i = 0; i < list.size() - 1; i++){
			double fit1 = list.get(i).getFitness();
			double fit2 = list.get(i + 1).getFitness();
			if(fit1 < fit2){
				return false;
			}
		}
		return true;
	}
	
	public static boolean test_XOR_training_data() {
		XOR_NN = FileHandler.readNeuralNetwork("XOR");
		XOR_NN.setSolveable(new XOR());
		return XOR_NN != null;
	}
	
	public static boolean test_XOR_SBP_Error() {
		double err = StochasticBackPropagation.runForThreshold(XOR_NN, trainingXOR, testingXOR,
				0.000001, 0.01, 50000, 25);
//		System.out.println(err);
		return err <= 0.001;
	}
	
	public static boolean test_XOR_output_count() {
		double[] inputs = {-1, -1};
		double[] outputs = XOR_NN.feedForward(inputs);
		return 1 == outputs.length;
	}

	public static boolean test_XOR_correct_outputs() {
		double[] inputs = {-1, -1};
		double[] outputs = XOR_NN.feedForward(inputs);
		if (outputs[0] > 0) {
			return false;
		}
		double[] inputs2 = {1, -1};
		if (XOR_NN.feedForward(inputs2)[0] < 0) {
			return false;
		}
		double[] inputs3 = {-1, 1};
		if (XOR_NN.feedForward(inputs3)[0] < 0) {
			return false;		
		}
		double[] inputs4 = {1, 1};
		if (XOR_NN.feedForward(inputs4)[0] > 0) {
			return false;		
		}
		return true;
	}

	public static boolean test_CUBE_training_data() {
		CUBE_NN = FileHandler.readNeuralNetwork("CUBE");
		CUBE_NN.setSolveable(new Cube(2));
		return CUBE_NN != null;
	}
	
	public static boolean test_CUBE_SBP_Error() {
		double err = StochasticBackPropagation.runForThreshold(CUBE_NN, training, testing, 1200, 0.01,
				50000, 25);
		return err <= 3000;
	}
}
