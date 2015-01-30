package ca.germuth.neural_network;
/**
 * Color.java
 * 
 * Holds each possible color value at each tile of a rubik's cube.  
 * 
 * January 30th, 2015 
 * @author Aaron Germuth
 */
public enum Color {
	WHITE("W"), 
	YELLOW("Y"), 
	BLUE("B"), 
	RED("R"), 
	GREEN("G"), 
	ORANGE("O");
	
	private String mName;
	
	private Color(String name){
		mName = name;
	}

	@Override
	public String toString() {
		return mName;
	}
}
