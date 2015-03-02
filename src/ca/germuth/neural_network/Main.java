package ca.germuth.neural_network;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import ca.germuth.neural_network.gui.MyGLCanvas;
import ca.germuth.neural_network.search.SearchFacade;
import ca.germuth.neural_network.search.SearchNode;
import ca.germuth.neural_network.search.SearchType;
import ca.germuth.neural_network.solvable.XOR;
import ca.germuth.neural_network.solvable.cube.Cube;
import ca.germuth.neural_network.trainable.NeuralNetwork;
import ca.germuth.neural_network.trainable.StochasticBackPropagation;
import ca.germuth.neural_network.trainable.TrainingData;
/**
 * CPSC 371 Project Phase 2
 * Main.java
 * 
 * February 27th, 2015 
 * @author Aaron Germuth
 */
public class Main {
	final static int PIXEL_WIDTH = 480;
	
	private static Cube cube;
	private static NeuralNetwork nn;
	private static String nnType;
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		Scanner lineScanner;
		printOptions();
		
		String inputLine = s.nextLine().trim();
		
		while(!inputLine.equals("quit")){	
			lineScanner = new Scanner(inputLine);
			switch(lineScanner.next().toLowerCase()){
				case "create":
					createNeuralNetwork(lineScanner.next(), s); break;
				case "solve":
					solveCube(); break;
				case "unit":
					unitTest(); break;
				case "traindata":
					trainData(lineScanner.next()); break;
				case "train":
					if(nn == null){
						System.out.println("You have not created a neural network");
						break;
					}
					String type = nn.getSolvable() instanceof XOR ? "XOR": "CUBE";
					if(type.equals("XOR")){
						System.out.println("Please Enter Error Threshold (Recommended: 0.05):");						
					} else { 
						System.out.println("Please Enter Error Threshold (Recommended: 200):");
					}
					double error_thres = s.nextDouble();
					System.out.println("Please Enter Learning Rate (Recommended: 0.005):");
					double learn_rate = s.nextDouble();
					System.out.println("Please Enter Training Iteraitons (Recommended: >100,000):");
					int train_iter = s.nextInt();
					System.out.println("Please Enter Training epochs (Recommended: >25):");
					int train_epoch= s.nextInt();
					trainNeuralNetwork(type, error_thres, learn_rate, train_iter, train_epoch);
					break;
				case "test":
					testNeuralNetwork(s); break;
				case "save":
					if(nn != null){
						FileHandler.writeNeuralNetwork(nn, (nn.getSolvable() instanceof XOR) ? "XOR": "CUBE");
					}else{
						System.out.println("No current neural network");
					}
					System.out.println("Saved succesfully");
					break;
				case "load":
					nnType = lineScanner.next();
					nn = FileHandler.readNeuralNetwork(nnType); 
					if(nnType.equals("XOR")){
						nn.setSolveable(new XOR());
					}else{
						createCube(2);
						nn.setSolveable(cube);
					}
					System.out.println("Loaded successfully");
					break;
				case "experiment":
					experiment(); break;
				case "help":
					System.out.println("There are 6 sides on a cube. Up, Down, Front, Back, Left, and Right");
					System.out.println("The default turn around these faces is clockwise. Adding a ' reverses this");
					System.out.println("A number can be prepended to turn the inner slices rather than the outer slices");
					System.out.println("Therefore \"2R'\" turns the second slice on the right hand side of a 4x4 clockwise");
					break;
				case "quit":
					System.exit(0); break;
			}
			
			inputLine = s.nextLine().trim();
			if(inputLine.isEmpty()){
				inputLine = s.nextLine().trim();
			}
		}
		
		s.close();
		System.exit(0);
	}
	
	private static void createNeuralNetwork(String type, Scanner scan){
		
		if(type.equals("CUBE")){
			System.out.println("Please Enter Hidden Layer Config: ('12 12' makes two layers of size 12)");
			System.out.println("Recommend 36");
			String[] strin = scan.nextLine().split(" ");
			int[] sizes = new int[strin.length];
			for(int i = 0; i < strin.length; i++){
				sizes[i] = Integer.parseInt(strin[i]);
			}
			createCube(2);
			nn = new NeuralNetwork(cube, 4*6*3, 4, sizes.length, sizes);
			nnType = "CUBE";
		}else{
			int[] arr = {2};
			nn = new NeuralNetwork(new XOR(), 2, 1, 1, arr);
			nnType = "XOR";
		}
		System.out.println("Neural Network Created");
	}
	
	private static void trainData(String type) {
		ArrayList<TrainingData> lines = new ArrayList<TrainingData>();
		if (type.equals("CUBE")) {
			HashMap<String, Boolean> visited = new HashMap<String, Boolean>();
			for (int k = 1; k < 8; k++) {
				for (int i = 0; i < 50; i++) {
					Cube experimentCube = new Cube(2);
					experimentCube.scrambleCube(k);
					if (experimentCube.isSolved()) {
						continue;
					}
					// replace unnecessary characters: ' ' ',' '[' ']
					String state = experimentCube.getKey().replaceAll(" |,|\\[|\\]", "");

					// unique key->value pairs
					if (!visited.containsKey(state)) {
						visited.put(state, true);

						SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, experimentCube,
								new Cube(2));
						String moveToMake = getPath(sn).split(" ")[0];
						lines.add(new TrainingData(state, moveToMake));
					}
				}
			}
		} else if (type.equals("XOR")) {
			lines.add(new TrainingData("-1 -1","-1"));
			lines.add(new TrainingData( "1 -1", "1"));
			lines.add(new TrainingData("-1 1",  "1"));
			lines.add(new TrainingData( "1 1", "-1"));
		}
		
		FileHandler.writeTrainingData(type, lines);
		System.out.println("Done creating training data");
	}

	private static void trainNeuralNetwork(String type, double ERROR_THRESHOLD, double LEARN_RATE,
			int train_iter, int train_epoch){
		ArrayList<TrainingData> training = FileHandler.readTrainingData(type);
		ArrayList<TrainingData> testing = FileHandler.readTrainingData(type);

		double err = StochasticBackPropagation.runForThreshold(nn, training, testing, ERROR_THRESHOLD, 
				LEARN_RATE, train_iter, train_epoch);
		if(err < ERROR_THRESHOLD){
			System.out.println("Neural Network Trained Successfully with " + err + " error");
		}else{
			System.out.println("Neural Network Trained UnSuccessfully with " + err + " error");
		}
	}
	
	private static void testNeuralNetwork(Scanner scan){
		Random r = new Random();
		while(true){
			
			if(nn.getSolvable() instanceof Cube){
				cube.scrambleCube(r.nextInt(8) + 1);
				
				while(!cube.isSolved()){
					String cubeState = cube.getKey().replaceAll(" |,|\\[|\\]", "");
					double[] moveArr = nn.feedForward( cube.mapTrainingInput(cubeState) );
					
					String move = cube.getMoveString(moveArr);
					System.out.println("Neural Network Says " + move);
					System.out.println("Would you like to perform this move (Y/N)");
					if(!scan.next().trim().toUpperCase().equals("Y")){
						break;
					} else{
						cube.turn(move);						
					}
				}
			}else{
				System.out.println("Enter an input (for ex. -1 1)");
				double[] input = new double[2];
				input[0] = scan.nextDouble();
				input[1] = scan.nextDouble();
				double[] move = nn.feedForward(input);
				System.out.println("Neural Network Says " + move[0]);
			}
			
			System.out.println("Try again? (Y/N)");
			if(!scan.next().trim().toUpperCase().equals("Y")){
				break;
			}
		}
		if(cube != null){
			cube.setSolved();
		}
	}
	
	//Create cube and initialize openGL window
	private static void createCube(int size){
		cube = new Cube(size);
		GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities capabilities = new GLCapabilities( glprofile );
        MyGLCanvas rd = new MyGLCanvas(capabilities, PIXEL_WIDTH, PIXEL_WIDTH, cube);

        final JFrame jframe = new JFrame( "Rubik's Cube Simulator" ); 
        jframe.setResizable(false);
        jframe.getContentPane().add( rd, BorderLayout.CENTER);
        jframe.setSize( PIXEL_WIDTH, PIXEL_WIDTH );
        jframe.setVisible( true );
        java.awt.EventQueue.invokeLater(new Runnable(){
        	@Override
        	public void run(){
        		jframe.toFront();
        		jframe.repaint();
        	}
        });
	}
	
	//Asks AStarSearch to solve the cube. Prints solution to output
	public static void solveCube(){
		long start = System.currentTimeMillis();
		SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, cube, new Cube(cube.getsize()));
		long end = System.currentTimeMillis();
		System.out.println("Solution: " + getPath(sn));
		System.out.println("Duration: " + (end - start) / 1000.0 + " seconds");
	}
	
	//Takes path from AStarSearch and creates string of moves
	public static String getPath(SearchNode lastNode){
		ArrayList<String> moves = new ArrayList<String>();
		SearchNode currNode = lastNode;
		while(currNode != null){
			String move = currNode.getSearchable().getMoveTaken();
			//initial state will have no move taken
			if(move != null){
				moves.add(move.replace("1", ""));
			}
			currNode = currNode.getParent();
		}
		StringBuilder sb = new StringBuilder();
		for(int i = moves.size() - 1; i >= 0; i--){
			sb.append(moves.get(i));
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private static void unitTest(){
		ArrayList<TrainingData> trainingXOR = FileHandler.readTrainingData("XOR");
		ArrayList<TrainingData> testingXOR = FileHandler.readTrainingData("XOR");
		ArrayList<TrainingData> training = FileHandler.readTrainingData("CUBE");
		ArrayList<TrainingData> testing = FileHandler.readTrainingData("CUBE");
		
		int[] arr = {2};
		NeuralNetwork nnet = new NeuralNetwork(new XOR(), 2, 1, 1, arr);
		double err = StochasticBackPropagation.runForThreshold(nnet, trainingXOR, testingXOR, 0.000001, 0.01, 50000, 25);
		System.out.println(err);
		if(err > 0.001){
			System.out.println("UNIT TEST FAIL: XOR had bad error");
		}
		
		int[] arr2 = {12};
		NeuralNetwork nnet2 = new NeuralNetwork(new Cube(2), 4*6*3, 4, 1, arr2);
		err = StochasticBackPropagation.runForThreshold(nnet2, training, testing, 1200, 0.01, 50000, 25);
		if(err > 3000){
			System.out.println("UNIT TEST FAIL: CUBE had bad error");
		}
		
		NeuralNetwork nnet3 = FileHandler.readNeuralNetwork("XOR");
		if(nnet3 == null){
			System.out.println("UNIT TEST FAIL: XOR unable to be read from file");
		}
		double[] inputs = {-1, -1};
		double[] outputs = nnet3.feedForward(inputs);
		if(outputs.length != 1){
			System.out.println("UNIT TEST FAIL: XOR has invalid number of output neurons");
		}
		if(outputs[0] > 0){
			System.out.println("UNIT TEST FAIL: XOR produced wrong output for -1 -1");
		}
		double[] inputs2 = {1,-1};
		if(nnet3.feedForward(inputs2)[0] < 0){
			System.out.println("UNIT TEST FAIL: XOR produced wrong output for 1 -1");
		}
		double[] inputs3 = {-1,1};
		if(nnet3.feedForward(inputs3)[0] < 0){
			System.out.println("UNIT TEST FAIL: XOR produced wrong output for -1 1");
		}
		double[] inputs4 = {1,1};
		if(nnet3.feedForward(inputs4)[0] > 0){
			System.out.println("UNIT TEST FAIL: XOR produced wrong output for 1 1");
		}
		
		NeuralNetwork nnet4 = FileHandler.readNeuralNetwork("CUBE");
		if(nnet4 == null){
			System.out.println("UNIT TEST FAIL: CUBE unable to be read from file");
		}
		
		System.out.println("UNIT TEST COMPLETE");
	}
	
	private static void printOptions() {
		System.out.println("CPSC 371 - Project Phase #2: Solving 2x2x2 with Neural Network"); 
		System.out.println("--------------------------------------------------------------");
		System.out.println("You can substitute XOR for CUBE");
		System.out.println("");
		System.out.println("Enter 'traindata XOR'	to run an experiment creating training data.");
		System.out.println("Enter 'create XOR'		to create a neural network");
		System.out.println("Enter 'train'			to train current neural network");
		System.out.println("Enter 'test'			to test the current neural network");
		System.out.println("Enter 'save'			to save current neural network");
		System.out.println("Enter 'load XOR'		to load an optimized neural network");
		System.out.println("Enter 'unit' 			to run Unit Test Suite");
		System.out.println("Enter 'help' 			to receive a list of possible moves");
		System.out.println("Enter 'quit' 			to quit the program");
	}
	
	private static void experiment(){
		int[] hiddenLayerSize = 	{54};
//		int[] hiddenLayerSize = 	{42, 48, 54, 60, 66};
		double[] learningRate = 	{0.002, 0.004, 0.005, 0.006, 0.08};
//		double[] learningRate = 	{0.005};
//		int[] trainingIterations = 	{100000};
		int[] trainingIterations =  {5000, 10000, 25000, 50000, 100000};
		
		int TRAINING_EPOCHS = 10;
		int[] hidSize = new int[2];
		
		File f = new File("new_data_triple_2_3.csv");
		try {
			f.createNewFile();
			PrintWriter pw = new PrintWriter(f);
			pw.println(Arrays.toString(hiddenLayerSize));
			pw.println(Arrays.toString(learningRate));
			pw.println(Arrays.toString(trainingIterations));
			
			for(int i = 0; i < hiddenLayerSize.length; i++){
				int HIDDEN_LAYER_SIZE = hiddenLayerSize[i];
				hidSize[0] = HIDDEN_LAYER_SIZE;
				
				for (int j = 0; j < learningRate.length; j++) {
					double LEARNING_RATE = learningRate[j];
					for (int k = 0; k < trainingIterations.length; k++) {
						int TRAINING_ITERATIONS = trainingIterations[k];

						System.out.println(i + " " + j + " " + k);
//						pw.print(i + " " + j + " " + k);;

						NeuralNetwork nn = new NeuralNetwork(new Cube(2), 4 * 6 * 3, 4, 1, hidSize);
						ArrayList<TrainingData> training = FileHandler
								.readTrainingData("CUBE");
						ArrayList<TrainingData> testing = FileHandler
								.readTrainingData("CUBE");
						double err = StochasticBackPropagation.runForMinimum(nn, training, testing,
								LEARNING_RATE, TRAINING_ITERATIONS, TRAINING_EPOCHS);
						pw.print(err + ",");

					}
					pw.println();
				}
				pw.println();
			}
			System.out.println("FILE DONE");
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}