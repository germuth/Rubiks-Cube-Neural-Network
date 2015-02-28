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
/**
 * Main.java
 * 
 * January 30th, 2015 
 * @author Aaron Germuth
 */

//nn output double[] doesn't account for all possibilities, trying to figure out something. right now there just map to some move
//should train with only data for 12 possible moves, and try to solve 1 away. IT SHOULD BE ABLE TO BE PERFECT
//gonna graph the error every training iteraiton and see at what amount of iterations it stops being useful
	// right now The whole thing is in a try PrintWriter loop. More iteraitons is definetly good, and actually makes adifferent
	// but my neural network is still completely retarded, i should just start doing his experiments. it will make my neural network not bad, and do the assignment
	// also it should be noted that my neural network is this retarded even when all scrambles are 1 move, and the training data consists perfectly of all of THE ONLY 12 cases!!!!!

//running experiment in order to find optimal triple value
//will use this optimal triple value as base point, and start exploring in 2D field in both directions from here
//also this entire experiment is done on only getting the neural network to learn how to solve 1 move on a 2x2
//im so depressed right now

//OMGGGGGGGGGGGGGGGGGGG
//IT TURNS OUT THE NEURAL NETWORK IS NOT RETARDED, ITS ACTUALLY JUST ME 
//PROBABLY
//MAPPING FROM -1 and 1s to R/L/B/F/U etc turns is screwed up on one side or the other or even both or something
//!

//Can train the network to perfect accuarcy with only 2 moves
//however giving the training data all possible cases for 3 moves it is not very trainable
//trying multiple layers with some hope it might work
//can also try changing input or output or both to not be binary but rather like 1 0 0 or 0 1 0 or 0 0 1 kind of thing, might be easier to discern for network...

//need to actually make SBP work with multiple hidden layers
//need to give up on solving 3 and try actual experiment maybe??
//need to allow neural network to be saved and restored from disk
//need to unit test
//experiment
//comments TODO
public class Main {
	final static int PIXEL_WIDTH = 480;
	
	private static Cube cube;
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		Scanner lineScanner;
		printOptions();
		
		String inputLine = s.nextLine().trim();
		
		while(!inputLine.equals("quit")){	
			lineScanner = new Scanner(inputLine);
			switch(lineScanner.next().toLowerCase()){
				case "create":
					createCube(lineScanner.nextInt()); break;
				case "scramble":
					System.out.println(cube.scrambleCube(lineScanner.nextInt())); break;
				case "move":
					while(lineScanner.hasNext()){
						cube.turn(lineScanner.next()); 
					} break;
				case "solve":
					solveCube(); break;
				case "unit":
					unitTest(); break;
				case "data": 
					createExperimentData(); break;
				case "train": 
					trainNeuralNetwork(); break;
				case "experiment":
					experiment(); break;
				case "help":
					System.out.println("There are 6 sides on a cube. Up, Down, Front, Back, Left, and Right");
					System.out.println("The default turn around these faces is clockwise. Adding a ' reverses this");
					System.out.println("A number can be prepended to turn the inner slices rather than the outer slices");
					System.out.println("Therefore \"2R'\" turns the second slice on the right hand side of a 4x4 clockwise");
					break;
				case "nn":
					nn();
					break;
				case "nnn":
					nnn();
					break;
				case "quit":
					System.exit(0); break;
			}
			
			inputLine = s.nextLine().trim();			
		}
		
		s.close();
		System.exit(0);
	}
	
	private static void trainNeuralNetwork(){
		nnn();
	}
	
	private static void experiment(){
		int[] hiddenLayerSize = 	{5, 12, 24};//{24, 32, 40, 48, 56};//{20, 24, 28, 32};//{12, 20, 28, 36, 44, 52};//12
		int[] hiddenLayerSize_2 = 	{5, 12, 24};
		double[] learningRate = {0.01};//{0.005, 0.01, 0.02};//{0.01, 0.05, 0.1, 0.2, 0.3};//0.05
		//0.01 learning rate is best
		int[] trainingIterations = {100000};//{50000, 75000, 100000};//{5000, 10000, 50000};//{5000};//5000
		//training iterations had biggest effect
		int[] trainingEpoch = {100};//{10, 50, 100};//100 actually does better, but 50 is really close
		//TODO i don't think this is what he meant
		//he wanted epochs to be the repeats...
		int REPEATS = 1;
		int[] hidSize = new int[2];
		
		File f = new File("new_data_6.csv");
		try {
			f.createNewFile();
			PrintWriter pw = new PrintWriter(f);
			
			for(int i = 0; i < hiddenLayerSize.length; i++){
				int HIDDEN_LAYER_SIZE = hiddenLayerSize[i];
				int HIDDEN_LAYER_SIZE_2 = hiddenLayerSize_2[i];
				
				hidSize[0] = HIDDEN_LAYER_SIZE;
				hidSize[1] = HIDDEN_LAYER_SIZE_2;
				
				for(int j = 0; j < learningRate.length; j++){
					double LEARNING_RATE = learningRate[j];
					for(int k = 0; k < trainingIterations.length; k++){
						int TRAINING_ITERATIONS = trainingIterations[k];
						for(int l = 0; l < trainingEpoch.length; l++){
							int TRAINING_EPOCH = trainingEpoch[l];
							
							System.out.println(i + " " + j + " " + k + " " + l);
							pw.print(i + " " + j + " " + k + " " + l + ",");
							double MIN_ERROR = Double.MAX_VALUE;
							
							for(int r = 0; r < REPEATS; r++){
								
								NeuralNetwork nn = new NeuralNetwork(new Cube(2), 4*6*3, 4, 2, hidSize);
								ArrayList<TrainingData> training = CSVHandler.read("data.csv");
								ArrayList<TrainingData> testing = CSVHandler.read("data.csv");
								double err = StochasticBackPropagation.runForMinimum(nn, training, testing, LEARNING_RATE, TRAINING_ITERATIONS, TRAINING_EPOCH);
								
								if(err < MIN_ERROR){
									MIN_ERROR = err;
								}
								
							}
							
							pw.println(MIN_ERROR + ",");
						}
						
					}
				}
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void nn(){
		int[] arr = {2};
		NeuralNetwork nn = new NeuralNetwork(new XOR(), 2, 1, 1, arr);
		ArrayList<TrainingData> training = CSVHandler.read("data.csv");
		ArrayList<TrainingData> testing = CSVHandler.read("data.csv");
		StochasticBackPropagation.runForThreshold(nn, training, testing);
		double[] arr2 = {-1.0, -1.0};
		System.out.println(Arrays.toString(arr2) + " " + Arrays.toString(nn.feedForward(arr2)));
		double[] arr3 = {-1.0, 1.0};
		System.out.println(Arrays.toString(arr3) + " " + Arrays.toString(nn.feedForward(arr3)));
		double[] arr4 = {1.0, -1.0};
		System.out.println(Arrays.toString(arr4) + " " + Arrays.toString(nn.feedForward(arr4)));
		double[] arr5 = {1.0, 1.0};
		System.out.println(Arrays.toString(arr5) + " " + Arrays.toString(nn.feedForward(arr5)));
		System.exit(0);

	}
	
	private static void nnn(){
		Scanner waitForUser = new Scanner(System.in);
		int[] arr = {24};//{4*6};//{4*6*3};
		NeuralNetwork nn = new NeuralNetwork(new Cube(2), 4*6*3, 4, 1, arr);
		ArrayList<TrainingData> training = CSVHandler.read("data.csv");
		ArrayList<TrainingData> testing = CSVHandler.read("data.csv");
//		System.out.println(StochasticBackPropagation.runForMinimum(nn, training, testing, 0.01, 100000, 100));
		System.out.println(StochasticBackPropagation.runForThreshold(nn, training, testing));
		createCube(2);
		waitForUser.next();
		
		Random r = new Random();
		while(true){
			
			cube.scrambleCube(r.nextInt(3) + 1);
//			cube.turn("F'");
			
			while(!cube.isSolved()){
				String cubeState = cube.getKey().replaceAll(" |,|\\[|\\]", "");
				double[] moveArr = nn.feedForward( cube.mapTrainingInput(cubeState) );
				
				for (TrainingData test : testing) {
					if(cubeState.equals(test.getInput())){
						// convert training data to scaled input/output, ie -> [-1, 1]
						double[] inputs = cube.mapTrainingInput(test.getInput());
						double[] expectedOutput = cube.mapTrainingOutput(test.getOutput());
						inputs[0] = 100000;
					}
				}
				
				String move = cube.getMoveString(moveArr);
				System.out.println("");
				System.out.println("Neural Network Says " + move);
				waitForUser.next();
				cube.turn(move);
			}
			System.out.println("SOLVED! GO AGAIN?");
			if(!waitForUser.next().trim().toUpperCase().equals("Y")){
				break;
			}
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
	
	//Runs experiment and prints training data to file
	private static void createExperimentData(){
		File f = new File("data.csv");
		try {
			PrintWriter pw = new PrintWriter(f);
			String[] move = {"R", "R'", "L", "L'", "U", "U'", "D", "D'", "F", "F'", "B", "B'"};
			HashMap<String, Boolean> visited = new HashMap<String, Boolean>();
			for(int i = 0; i < 12; i++){
				Cube ex = new Cube(2);
				ex.turn(move[i]);
				
				String state = ex.getKey().replaceAll(" |,|\\[|\\]", "");
				visited.put(state, true);
				pw.print(state + ", ");
				SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, ex, new Cube(2));
				String moveSequence = getPath(sn);
				Scanner temp = new Scanner(moveSequence);
				pw.println(temp.next());
				temp.close();
			}
			for(int i = 0; i < 12; i++){
				for(int j = 0; j < 12; j++){
					Cube ex = new Cube(2);
					ex.turn(move[i]);
					ex.turn(move[j]);
					if(!ex.isSolved()){
						String state = ex.getKey().replaceAll(" |,|\\[|\\]", "");
						//no duplicates
						if(!visited.containsKey(state)){
							visited.put(state, true);
							pw.print(state + ", ");
							SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, ex, new Cube(2));
							String moveSequence = getPath(sn);
							Scanner temp = new Scanner(moveSequence);
							pw.println(temp.next());
							temp.close();
						}
					}
				}
			}
			for(int i = 0; i < 12; i++){
				for(int j = 0; j < 12; j++){
					for(int k = 0; k < 12; k++){
						Cube ex = new Cube(2);
						ex.turn(move[i]);
						ex.turn(move[j]);
						ex.turn(move[k]);
						if(!ex.isSolved()){
							String state = ex.getKey().replaceAll(" |,|\\[|\\]", "");
							//no duplicates
							if(!visited.containsKey(state)){
								visited.put(state, true);
								pw.print(state + ", ");
								SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, ex, new Cube(2));
								String moveSequence = getPath(sn);
								Scanner temp = new Scanner(moveSequence);
								pw.println(temp.next());
								temp.close();
							}
						}
						
					}
				}
			}
			//TODO
//			for(int k = 1; k < 8; k++){
//				System.out.println(k);
//				for(int i = 0; i < 50; i++){
//					Cube experimentCube = new Cube(2);
//					experimentCube.scrambleCube(k);
//					if(experimentCube.isSolved()){
//						continue;
//					}
//					//replace unnecessary characters: ' ' ',' '[' ']
//					String state = experimentCube.getKey().replaceAll(" |,|\\[|\\]", "");
//					pw.print(state + ", ");
//					
//					SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, experimentCube, new Cube(2));
//					String moveSequence = getPath(sn);
//					//get first move
//					Scanner temp = new Scanner(moveSequence);
//					pw.println(temp.next());
//					temp.close();
//				}
//			}
			pw.flush();
			pw.close();
			System.out.println("data.csv successfully written");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//TODO you forgot unit tests
	private static boolean unitTest(){
		Cube c = new Cube(2);
		c.turn("U R R");
		SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, c, new Cube(2));
		String answer = getPath(sn);
		if(!answer.equals("R R U'")){
			return false;
		}
		
		c = new Cube(3);
		c.turn("U R R");
		sn = SearchFacade.runSearch(SearchType.ASTAR, c, new Cube(3));
		answer = getPath(sn);
		if(!answer.equals("R R U'")){
			return false;
		}
		
		return true;
	}
	
	private static void printOptions() {
		System.out.println("CPSC 371 - Project Phase #1: AStarSearch of Rubik's Cube"); 
		System.out.println("------------------------------------------------------");
		System.out.println("");
		System.out.println("Enter 'create 3' 	to create a 3x3x3 cube in new window");
		System.out.println("Enter 'scramble 10'		to scramble the cube with 10 random moves");
		System.out.println("Enter 'move F U' 	to apply an F and U move on the cube. ");
		System.out.println("Enter 'solve'	 	to search for a solution");
		System.out.println("Enter 'unit' 		to run Unit Test Suite");
		System.out.println("Enter 'train' 		to run the experiment. Produces data.csv");
		System.out.println("Enter 'help' 		to receive a list of possible moves");
		System.out.println("Enter 'quit' 		to quit the program");
	}
}