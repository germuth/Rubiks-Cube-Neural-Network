package ca.germuth.neural_network;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import ca.germuth.neural_network.gui.MyGLCanvas;
import ca.germuth.neural_network.search.SearchFacade;
import ca.germuth.neural_network.search.SearchNode;
import ca.germuth.neural_network.search.SearchType;
/**
 * Main.java
 * 
 * January 30th, 2015 
 * @author Aaron Germuth
 */
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
				case "train": 
					createExperimentData(); break;
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
		}
		
		s.close();
		System.exit(0);
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
			for(int k = 1; k < 8; k++){
				for(int i = 0; i < 10; i++){
					Cube experimentCube = new Cube(2);
					experimentCube.scrambleCube(k);
					//replace unnecessary characters: ' ' ',' '[' ']
					String state = experimentCube.getKey().replaceAll(" |,|\\[|\\]", "");
					pw.print(state + ", ");
					
					SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, experimentCube, new Cube(2));
					String moveSequence = getPath(sn);
					//get first move
					Scanner temp = new Scanner(moveSequence);
					pw.println(temp.next());
					temp.close();
				}
			}
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
		System.out.println("Enter 'scramble 10' to scramble the cube with 10 random moves");
		System.out.println("Enter 'move F U' 	to apply an F and U move on the cube. ");
		System.out.println("Enter 'solve'	 	to search for a solution");
		System.out.println("Enter 'unit' 		to run Unit Test Suite");
		System.out.println("Enter 'train 		to run the experiment. Produces data.csv");
		System.out.println("Enter 'help' 		to receive a list of possible moves");
		System.out.println("Enter 'quit' 		to quit the program");
	}
}