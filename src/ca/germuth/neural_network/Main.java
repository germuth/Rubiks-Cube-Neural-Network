package ca.germuth.neural_network;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Scanner;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import ca.germuth.neural_network.gui.MyGLCanvas;
/**
 * Profiler results as of 2pm 27th
 * 52 % is getKey
 * 47 % is getChildren
 * 	30 % is turn
 * 		~15 % is Utils.rotateMatrix
 * 	17 % is constructor
 * 		16 % is Utils.copy
 * 
 * @author Aaron
 */
public class Main {
	final static int X = 640;
	final static int Y = 480;
	public static void main(String[] args){
//		final int CUBE_SIZE = 3;
//		Cube c = new Cube(CUBE_SIZE);
//		GLProfile glprofile = GLProfile.getDefault();
//        GLCapabilities capabilities = new GLCapabilities( glprofile );
//        MyGLCanvas rd = new MyGLCanvas(capabilities, X, Y, c);
//
//        final JFrame jframe = new JFrame( "One Triangle Swing GLCanvas" ); 
//        jframe.addWindowListener( new WindowAdapter() {
//            public void windowClosing( WindowEvent windowevent ) {
//                jframe.dispose();
//                System.exit( 0 );
//            }
//        });
//
//        jframe.getContentPane().add( rd, BorderLayout.CENTER );
//        jframe.setSize( X, Y );
//        jframe.setVisible( true );
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Scanner s = new Scanner(System.in);
		String scramble = "R' F' D' B' F D L D U";
//		String scramble = "R U R' U' R U R'";
		final int CUBE_SIZE = 3;
		Cube c = new Cube(CUBE_SIZE);
		
////		System.out.println(c.calcHeuristic());
////		c.turn("L'");
////		System.out.println(c.calcHeuristic());
////		c.turn("D'");
////		System.out.println(c.calcHeuristic());
////		c.turn("U");
////		System.out.println(c.calcHeuristic());
////		c.turn("B");
////		System.out.println(c.calcHeuristic());
////		c.turn("B");
////		System.out.println(c.calcHeuristic());
////		c.turn("F");
////		System.out.println(c.calcHeuristic());
////		c.turn("F");
////		System.out.println(c.calcHeuristic());
////		c.turn("R");
////		System.out.println(c.calcHeuristic());
////		c.turn("D");
////		System.out.println(c.calcHeuristic());
////		c.turn("U");
////		System.out.println(c.calcHeuristic());
////		c.turn("L");
////		System.out.println(c.calcHeuristic());
////		c.turn("B");
////		System.out.println(c.calcHeuristic());
////		c.turn("R");
////		System.out.println(c.calcHeuristic());
////		c.turn("U");
////		System.out.println(c.calcHeuristic());
////		c.turn("L'");
////		System.out.println(c.calcHeuristic());
////		c.turn("R");
////		System.out.println(c.calcHeuristic());
////		c.turn("R");
////		System.out.println(c.calcHeuristic());
////		c.turn("U");
////		System.out.println(c.calcHeuristic());
////		c.turn("U");
////		System.out.println(c.calcHeuristic());
////		c.turn("B");
////		System.out.println(c.calcHeuristic());
////		c.turn("B");
////		System.out.println(c.calcHeuristic());
////		c.turn("L");
////		System.out.println(c.calcHeuristic());
////		c.turn("D");
////		System.out.println(c.calcHeuristic());
////		c.turn("L'");
////		System.out.println(c.calcHeuristic());
////		c.turn("F'");
////		System.out.println(c.calcHeuristic());
////		c.turn("D");
////		System.out.println(c.calcHeuristic());
////		c.turn("L2");
////		System.out.println(c.calcHeuristic());
////		c.turn("B'");
////		System.out.println(c.calcHeuristic());
////		c.turn("D'");
////		System.out.println(c.calcHeuristic());
//		
//		
		c.turn(scramble);
		
		c.setMoveTaken(null);
		System.out.println("Scramble: " + scramble);
		
		s.next();
		
		long start = System.currentTimeMillis();
		SearchNode sn = SearchFacade.runSearch(SearchType.ASTAR, c, new Cube(CUBE_SIZE));
////		SearchNode sn = SearchFacade.runSearch(SearchType.BFS, c, new Cube(3));
		long end = System.currentTimeMillis();
		System.out.println("Solution: " + getPath(sn));
		System.out.println("Duration: " + (end - start) / 1000.0);
	}
	
	public static String getPath(SearchNode lastNode){
		ArrayList<String> moves = new ArrayList<String>();
		SearchNode currNode = lastNode;
		while(currNode != null){
			String move = currNode.getSearchable().getMoveTaken();
			//initial state will have no move taken
			if(move != null){
				//TODO is this the right place for replace
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
}