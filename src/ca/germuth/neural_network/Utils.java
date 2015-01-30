package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Utils.java
 * 
 * Utility class
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
	
	
	
	//Counts the number of inverted pairs or swaps in the provided array
	public static long inversionCount(char[] arr){
		return inversionCount(arr, 0, arr.length);
	}
	
	//we are effectively trying to count the number of inversions it takes
	//to sort the array. we can recursively break down the array in halfs, and 
	//then merge each half, just like merge-sort. 
	//In a pre-sorted array (ie 0 inversions) we would simply add the first half
	//to the second half to accomplish a merge. 
	//Everytime we have to grab the next smallest value from the second array, it 
	//represents multiple inversions. One inversion for each value remaining
	//in the left array, which equals the number of swaps it would take to 
	//sort that value
	private static long inversionCount(char[] arr, int start, int end) {
		if (end - start <= 1)
			return 0;

		long answer = 0;
		int mid = (start + end) / 2;
		answer += inversionCount(arr, start, mid) + inversionCount(arr, mid, end);

		int index1 = 0, index2 = 0;	
		while(start + index1 < mid && mid + index2 <= end - 1){
			if(arr[start + index1] > arr[mid + index2]){
				//number at mid+index2 is smaller than start+index1 and every other element in left array
				answer += (mid - start - index1);
				index2++;
			}else{
				index1++;
			}
		}

		Arrays.sort(arr, start, end);
		return answer;
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
