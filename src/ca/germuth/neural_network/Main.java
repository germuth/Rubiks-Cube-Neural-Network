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

import ca.germuth.neural_network.evolution.Evolvable;
import ca.germuth.neural_network.evolution.EvolvableNeuralNetwork;
import ca.germuth.neural_network.evolution.GeneticAlgorithm;
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
 * CPSC 371 Project Phase 3
 * Main.java
 * 
 * April 17th, 2015 
 * @author Aaron Germuth
 */

//TODO
//implement phase1 and phase2 marks
//large number of epochs like 1000 for optimal
//over a million iterations
//less hidden neurons, 30 prob too much
//higher learning rate 0.1
//implement momentum, weight decay, intialization (easiest one)

//Phase 3
//run genetic algorithm on population of 2 2 1 networks
	//implement probability of selection
	//
//implement other things
//start to run experiments the smart way
//write document
//hand in

//TODO
//delete these comments
//adjust numbers in Genetic Algorithm
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
				case "evolve":
					evolveNeuralNetworks(); break;
				case "config":
					configGA(s); break;
				case "solve":
					solveCube(); break;
				case "unit":
					UnitTest.testAll(); break;
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
				case "experiment1":
					experiment1(); break;
				case "experimenta":
					experimentA(); break;
				case "experimentb":
					experimentB(); break;
				case "experimentc":
					experimentC(); break;
				case "experimentd":
					experimentD(); break;
				case "experimente":
					experimentE(); break;
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
		
		System.out.println("Please Enter Hidden Layer Config: ('12 12' makes two layers of size 12)");
		System.out.println("Recommend 36 for Cube and 2 for XOR");
		String[] strin = scan.nextLine().split(" ");
		int[] sizes = new int[strin.length];
		for(int i = 0; i < strin.length; i++){
			sizes[i] = Integer.parseInt(strin[i]);
		}
		if(type.equals("CUBE")){
			createCube(2);
			nn = new NeuralNetwork(cube, 4*6*3, 4, sizes.length, sizes);
			nnType = "CUBE";
		}else{
			nn = new NeuralNetwork(new XOR(), 2, 1, sizes.length, sizes);
			nnType = "XOR";
		}
		System.out.println("Neural Network Created");
	}
	
	private static void evolveNeuralNetworks(){
		//TODO NOT JUST XOR
		ArrayList<TrainingData> training = FileHandler.readTrainingData("XOR");
		
		ArrayList<Evolvable> list = new ArrayList<Evolvable>();
		int[] hiddenNodes = {2};
		for(int i = 0; i < GeneticAlgorithm.getPOPULATION_SIZE(); i++){
			EvolvableNeuralNetwork next;
			if(nn == null){
				next = new EvolvableNeuralNetwork(new XOR(), 2, 1, 1, hiddenNodes);
			}else{
				next = new EvolvableNeuralNetwork(new XOR(), 2, 1, nn.getNumLayers() - 2, nn.hiddenLayerSizes());
			}
			next.randomizeAllEdgeWeights();
			next.calcFitness(training);
			list.add(next);
		}
		
		EvolvableNeuralNetwork evolved = (EvolvableNeuralNetwork) GeneticAlgorithm.run(list, training);
		evolved.toNetwork();
		nn = evolved;
		System.out.println("Evolution Complete");
		
	}
	
	private static void configGA(Scanner s){
		System.out.println("Please Enter Population Size (Recommended:400):");
		GeneticAlgorithm.setPOPULATION_SIZE(s.nextInt());
		System.out.println("Please Enter GA Iterations (Recommended: 500):");
		GeneticAlgorithm.setITERATIONS(s.nextInt());
		System.out.println("Please Enter Elite % (Recommended: 0.2):");
		GeneticAlgorithm.setPOPULATION_ELITE_PERCENT(s.nextDouble() / 100);
		System.out.println("Please Enter Mutation % (Recommended: 0.4):");
		GeneticAlgorithm.setPOPULATION_MUTATION_PERCENT(s.nextDouble() / 100);
		System.out.println("Please Enter CrossOver % (Recommended: 0.4):");
		GeneticAlgorithm.setPOPULATION_CROSSOVER_PERCENT(s.nextDouble() / 100);
		System.out.println("Genetic Algorithm Configured");
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
	
	private static void printOptions() {
		System.out.println("CPSC 371 - Project Phase #3: Evolving Neural Networks towards XOR"); 
		System.out.println("--------------------------------------------------------------");
		System.out.println("The current training data is stored in XORdata.csv");
		System.out.println("");
		System.out.println("Enter 'traindata XOR'	to run an experiment creating training data");
		System.out.println("Enter 'create XOR'		to create a neural network");
		System.out.println("Enter 'evolve'			to evolve many neural networks and use best");
		System.out.println("Enter 'config'			to configure the Genetic Algorithm Parameters");
		System.out.println("Enter 'train'			to train current neural network");
		System.out.println("Enter 'test'			to test the current neural network");
		System.out.println("Enter 'experimentX		to run experiment X. X = one of [1, A, B, C, D, or E]");
		System.out.println("Enter 'save'			to save current neural network to XORNeuralNetwork.csv");
		System.out.println("Enter 'load XOR'		to load an optimized neural network from XORNeuralNetwork.csv");
		System.out.println("Enter 'unit' 			to run Unit Test Suite");
//		System.out.println("Enter 'help' 			to receive a list of possible moves");
		System.out.println("Enter 'quit' 			to quit the program");
	}
	
	private static void runExperiment(String experimentName, int[] popSize, int[] iterations, double[] elite_percent, double[] mutation_percent, int repeats){
		int[] hid = {2};
		File f = new File(experimentName + ".csv");
		PrintWriter pw = null;
		try {
			f.createNewFile();
			pw = new PrintWriter(f);

			ArrayList<TrainingData> training = FileHandler.readTrainingData("XOR");

			for (int i = 0; i < popSize.length; i++) {
				int POPULATION_SIZE = popSize[i];
				GeneticAlgorithm.setPOPULATION_SIZE(POPULATION_SIZE);
				for (int j = 0; j < iterations.length; j++) {
					int ITERATIONS = iterations[j];
					GeneticAlgorithm.setITERATIONS(ITERATIONS);
					for (int k = 0; k < elite_percent.length; k++) {
						double ELITE = elite_percent[k];
						GeneticAlgorithm.setPOPULATION_ELITE_PERCENT(ELITE);
						for (int m = 0; m < mutation_percent.length; m++) {
							double MUTATION = mutation_percent[m];
							if (ELITE + MUTATION > 1.0) {
								continue;
							}
							double CROSSOVER = 1.0 - ELITE - MUTATION;
							GeneticAlgorithm.setPOPULATION_MUTATION_PERCENT(MUTATION);
							GeneticAlgorithm.setPOPULATION_CROSSOVER_PERCENT(CROSSOVER);
							System.out.println(i + " " + j + " " + k + " " + m);
							
							//evaluating each parameters by the best run over 10 tries
							//average would be viable option as well for more consistent performance
							double bestFitness = Double.MIN_VALUE;
							for (int r = 0; r < repeats; r++) {

								ArrayList<Evolvable> list = new ArrayList<Evolvable>();
								for (int x = 0; x < GeneticAlgorithm.getPOPULATION_SIZE(); x++) {
									EvolvableNeuralNetwork next = new EvolvableNeuralNetwork(new XOR(), 2, 1, 1, hid);
									next.randomizeAllEdgeWeights();
									list.add(next);
								}

								EvolvableNeuralNetwork evolved = (EvolvableNeuralNetwork) GeneticAlgorithm.run(list, training);
								evolved.toNetwork();
								if (evolved.getFitness() > bestFitness) {
									bestFitness = evolved.getFitness();
								}
							}
							pw.print(bestFitness + " " + POPULATION_SIZE + "-" + ITERATIONS + "-"
									+ ELITE + "-" + MUTATION + "-" + CROSSOVER + ",");
						}
					}
				}
			}
			System.out.println("FILE DONE");
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(pw != null){
				pw.flush();
				pw.close();				
			}
		}
	}
	
	/**
	 * Experiment 1 Is Used to search the paramater space, looking for 5 promising regions 
	 * to further investigate in experiment A-E
	 */
	private static void experiment1() {
		//Finding The Top 5 combinations of parameters
		int[] popSize = 			{20, 50, 100, 200, 500};
		int[] iterations = 			{20, 50, 100, 200, 500};
		double[] elite_percent = 	{0.10, 0.20, 0.50, 0.80};
		double[] mutation_percent = {0.10, 0.20, 0.50, 0.80};
		int repeats = 10;
		runExperiment("GA_XOR_experiment_1", popSize, iterations, elite_percent, mutation_percent, repeats);
	}
	
	/**
	 * Data Point 1. 
	 * Parameters: 		popSize, iter, elite%, mutation%, crossover% 				
	 * Centered Around: 500, 500, 0.30, 0.40, 0.20
	 */
	private static void experimentA() {
		int[] center_pop = 			{500};
		int[] center_iter =			{500};
		double[] center_elite = 	{0.30};
		double[] center_mutation = 	{0.40};
		
		int[] popSize = 			{300, 400, 500, 600, 700, 800, 900, 1000};
		int[] iterations = 			{300, 400, 500, 600, 700, 800, 900, 1000};
		double[] elite_percent = 	{0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50};
		int repeats = 10;
		//fitness(Inverse of Accuracy) vs. Number of Generations
		runExperiment("GA_XOR_experiment_A1", center_pop, iterations, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. Number of Genomes
		runExperiment("GA_XOR_experiment_A2", popSize, center_iter, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. % Elite
		runExperiment("GA_XOR_experiment_A3", center_pop, center_iter, elite_percent, center_mutation, repeats);
	}
	
	/**
	 * Data Point 2. 
	 * Parameters: 		popSize, iter, elite%, mutation%, crossover% 				
	 * Centered Around: 500, 500, 0.20, 0.50, 0.30
	 */
	private static void experimentB() {
		//Data Point 2. Centered Around 500, 500, 0.20, 0.50, 0.30
		int[] center_pop = 			{500};
		int[] center_iter =			{500};
		double[] center_elite = 	{0.20};
		double[] center_mutation = 	{0.50};
		
		int[] popSize = 			{300, 400, 500, 600, 700, 800, 900, 1000};
		int[] iterations = 			{300, 400, 500, 600, 700, 800, 900, 1000};
		double[] elite_percent = 	{0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40};
		int repeats = 10;
		//fitness(Inverse of Accuracy) vs. Number of Generations
		runExperiment("GA_XOR_experiment_B1", center_pop, iterations, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. Number of Genomes
		runExperiment("GA_XOR_experiment_B2", popSize, center_iter, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. % Elite
		runExperiment("GA_XOR_experiment_B3", center_pop, center_iter, elite_percent, center_mutation, repeats);
	}
	
	/**
	 * Data Point 3. 
	 * Parameters: 		popSize, iter, elite%, mutation%, crossover% 				
	 * Centered Around: 500, 500, 0.10, 0.20, 0.70
	 */
	private static void experimentC() {
		//Data Point 3. Centered Around 500, 500, 0.10, 0.20, 0.70
		int[] center_pop = 			{500};
		int[] center_iter =			{500};
		double[] center_elite = 	{0.10};
		double[] center_mutation = 	{0.20};
		
		int[] popSize = 			{300, 400, 500, 600, 700, 800, 900, 1000};
		int[] iterations = 			{300, 400, 500, 600, 700, 800, 900, 1000};
		double[] elite_percent = 	{0.01, 0.05, 0.08, 0.10, 0.12, 0.15, 0.18, 0.20, 0.25};
		int repeats = 10;
		//fitness(Inverse of Accuracy) vs. Number of Generations
		runExperiment("GA_XOR_experiment_C1", center_pop, iterations, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. Number of Genomes
		runExperiment("GA_XOR_experiment_C2", popSize, center_iter, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. % Elite
		runExperiment("GA_XOR_experiment_C3", center_pop, center_iter, elite_percent, center_mutation, repeats);
	}
	
	/**
	 * Data Point 4. 
	 * Parameters: 		popSize, iter, elite%, mutation%, crossover% 				
	 * Centered Around: 200, 500, 0.30, 0.50, 0.10
	 */
	private static void experimentD() {
		//Data Point 4. Centered Around 200, 500, 0.30, 0.50, 0.10
		int[] center_pop = 			{200};
		int[] center_iter =			{500};
		double[] center_elite = 	{0.30};
		double[] center_mutation = 	{0.50};
		
		int[] popSize = 			{100, 150, 180, 200, 220, 250, 300, 400, 500};
		int[] iterations = 			{300, 400, 500, 600, 700, 800, 900, 1000};
		double[] elite_percent = 	{0.15, 0.20, 0.22, 0.25, 0.28, 0.30, 0.35, 0.40, 0.45};
		int repeats = 10;
		//fitness(Inverse of Accuracy) vs. Number of Generations
		runExperiment("GA_XOR_experiment_D1", center_pop, iterations, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. Number of Genomes
		runExperiment("GA_XOR_experiment_D2", popSize, center_iter, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. % Elite
		runExperiment("GA_XOR_experiment_D3", center_pop, center_iter, elite_percent, center_mutation, repeats);
	}
	
	/**
	 * Data Point 5. 
	 * Parameters: 		popSize, iter, elite%, mutation%, crossover% 				
	 * Centered Around: 200, 500, 0.30, 0.60, 0.20
	 */
	private static void experimentE() {
		//Data Point 5. Centered Around 200, 500, 0.30, 0.60, 0.20
		int[] center_pop = 			{200};
		int[] center_iter =			{500};
		double[] center_elite = 	{0.30};
		double[] center_mutation = 	{0.60};
		
		int[] popSize = 			{100, 150, 180, 200, 220, 250, 300, 400, 500};
		int[] iterations = 			{300, 400, 500, 600, 700, 800, 900, 1000};
		double[] elite_percent = 	{0.10, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50};
		int repeats = 10;
		//fitness(Inverse of Accuracy) vs. Number of Generations
		runExperiment("GA_XOR_experiment_E1", center_pop, iterations, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. Number of Genomes
		runExperiment("GA_XOR_experiment_E2", popSize, center_iter, center_elite, center_mutation, repeats);
		//fitness(Inverse of Accuracy) vs. % Elite
		runExperiment("GA_XOR_experiment_E3", center_pop, center_iter, elite_percent, center_mutation, repeats);
	}
}