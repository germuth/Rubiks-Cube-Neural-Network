package ca.germuth.neural_network.gui;

import java.util.ArrayList;

import ca.germuth.neural_network.Color;
import ca.germuth.neural_network.Cube;
import ca.germuth.neural_network.Face;
/**
 * GLSquare
 * 
 * Utility class for openGL to hold a square
 * 
 * @author Aaron Germuth
 */
public class GLSquare {
	
	//which tile of the cube this square represents
	private Face face;
	private int row;
	private int col;
	//list of four verticies
	private ArrayList<GLVertex> verticies;
	
	public GLSquare(GLVertex one, GLVertex two, GLVertex three, GLVertex four, Face f, int r, int c) {
		this.verticies = new ArrayList<GLVertex>();
		this.verticies.add(one);
		this.verticies.add(two);
		this.verticies.add(three);
		this.verticies.add(four);
		face = f;
		row = r;
		col = c;
	}
	
	public GLVertex getTopLeft(){
		return verticies.get(0);
	}
	public GLVertex getBottomLeft(){
		return verticies.get(1);
	}
	public GLVertex getBottomRight(){
		return verticies.get(2);
	}
	public GLVertex getTopRight(){
		return verticies.get(3);
	}
	public GLColor getCurrentColor(Cube cube) {
		return toGLColor(cube.getColor(face, row, col));
	}
	
	/**
	 * Rotates this square around either the x y or z axis, by an amount in radians
	 * @param axis, the axis you are rotating around, may be 'X', 'Y', or 'Z'
	 * @param radians, the degree in radians you want to rotate around it
	 */
	public void rotate(char axis, float radians){
		for(int i = 0; i < this.verticies.size(); i++){
			GLVertex current = this.verticies.get(i);
			current.rotate(axis, radians);
		}	
	}
	
	public static void rotateAll(ArrayList<GLSquare> face, char axis, float radians){
		for(int i = 0; i < face.size(); i++){
			GLSquare s = face.get(i);
			s.rotate(axis, radians);
		}
	}
	
	//convert from Color Enum to OpenGL
	public static GLColor toGLColor(Color col){
		switch(col){
			case RED: return new GLColor(1f, 0, 0);
			case GREEN: return new GLColor(0, 1f, 0);
			case BLUE: return new GLColor(0, 0, 1f);
			case WHITE: return new GLColor(1f, 1f, 1f);
			case YELLOW: return new GLColor(1f, 1f, 0f);
			case ORANGE: return new GLColor(1f, 0.5f, 0f);
			default:
				return null;
		}
	}
}
