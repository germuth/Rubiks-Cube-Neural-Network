package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.Arrays;

import ca.germuth.neural_network.solvable.cube.Color;
/**
 * Utils.java
 * 
 * January 30th, 2015 
 * @author Aaron Germuth
 */
public class Utils {
	
	//Returns a deep copy of the 2D array provided
	//Matrix must be square
	public static Color[][] copy(Color[][] arr){
		int n = arr.length;
		Color[][] copy = new Color[n][n];
		for(int i = 0; i < n; i++)
			copy[i] = Arrays.copyOf(arr[i], n);
		return copy;
	}
	
	//rotates matrix 90 degrees clockwise
	//Matrix must be square
	public static Color[][] rotateMatrixClockwise(Color[][] arr){
		final int n = arr.length;
		Color[][] arrCopy = new Color[n][n];
		
		for (int i = 0; i < n; ++i)
	        for (int j = 0; j < n; ++j) 
	        	arrCopy[i][j] = arr[n-j-1][i];

	    return arrCopy;
	}
	
	//Returns the number of distinct colors in the provided array
	public static int numberColours(Color[][] face){
		int number = 0;
		ArrayList<Color> colorsSoFar = new ArrayList<Color>();
		for(int i = 0; i < face.length; i++)
			for(int j = 0; j < face[i].length; j++)
				if(!colorsSoFar.contains(face[i][j])){
					colorsSoFar.add(face[i][j]);
					number++;
				}

		return number;
	}
}
