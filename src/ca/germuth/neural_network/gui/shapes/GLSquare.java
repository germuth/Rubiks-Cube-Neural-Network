package ca.germuth.neural_network.gui.shapes;

import java.util.ArrayList;

import ca.germuth.neural_network.Color;
import ca.germuth.neural_network.gui.GLColor;

public class GLSquare {
	private ArrayList<GLVertex> verticies;
	private GLColor color;
	public GLSquare(GLVertex one, GLVertex two, GLVertex three, GLVertex four) {
		this.verticies = new ArrayList<GLVertex>();
		this.verticies.add(one);
		this.verticies.add(two);
		this.verticies.add(three);
		this.verticies.add(four);
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
	public void setColor(GLColor col) {
		this.color = col;
	}
	public GLColor getColor() {
		return color;
	}
}
