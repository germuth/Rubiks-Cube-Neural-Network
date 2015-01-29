package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
	
	//TODO this should be object?
	public static Color[][] copy(Color[][] arr){
		int m = arr.length;
		int n = arr[0].length;
		Color[][] copy = new Color[m][n];
		for(int i = 0; i < m; i++)
			for(int j = 0; j < n; j++)
				//TODO not guaranteed to be deep copy
				copy[i][j] = arr[i][j];
		return copy;
	}
	
	public static Object[][] rotateMatrixClockwise(Object[][] arr){
		final int M = arr.length;
		final int N = arr[0].length;

		Object[][] arrCopy = new Color[N][M];
		// create copy
		for (int i = 0; i < arr.length; i++) {
			Object[] row = arr[i];
			int length = row.length;
			arrCopy[i] = new Color[length];
			System.arraycopy(row, 0, arrCopy[i], 0, length);
		}

		if (M == N) {
			// r designates row
			for (int r = 0; r < M; r++) {
				// c designates column
				for (int c = 0; c < N; c++) {
					arr[c][M - 1 - r] = arrCopy[r][c];
				}
			}
		} else {
			// reverse each row
			for (int i = 0; i < M; i++) {
				for (int j = 0; j < N / 2; j++) {
					arr[i][j] = arrCopy[i][N - j - 1];
				}
			}
			// then each column
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M / 2; j++) {
					arr[j][i] = arrCopy[M - j - 1][i];
				}
			}
		}
		return arrCopy;
	}
	
	public static String encodeCube(Cube c){
		String key = "";
		int size = c.getsize();
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				key += c.getColor(Face.BACK, i, j).toString() +
						c.getColor(Face.FRONT, i, j).toString() +
						c.getColor(Face.LEFT, i, j).toString() +
						c.getColor(Face.RIGHT, i, j).toString() +
						c.getColor(Face.UP, i, j).toString() + 
						c.getColor(Face.DOWN, i, j).toString();
			}
		}
		return key;
//		key = Arrays.deepToString(this.back) + //blue 
//				Arrays.deepToString(this.front) + //green
//				Arrays.deepToString(this.left) + //orange
//				Arrays.deepToString(this.right) + //red
//				Arrays.deepToString(this.up) + //white
//				Arrays.deepToString(this.down); //yellow
	}
	//we are effectively trying to count the number of inversions it takes
	//to sort the array. we can recursively break down the array in halfs, and 
	//then merge each half, just like merge-sort. 
	//when merging, if there was no inversions, we would simply add the first array
	//and then the second to merge. Everytime we have to grab the next smallest value
	//from the second array, it represents multiple inversions. One inversion for each 
	//value left in the left array, which equals the number of swaps it would take to 
	//sort that value
	
	public static long inversionCount(char[] arr){
		return inversionCount(arr, 0, arr.length);
	}
	
	private static long inversionCount(char[] arr, int start, int end) {
		if (end - start <= 1)
			return 0;

		long answer = 0;
		int mid = (start + end) / 2;
		answer += inversionCount(arr, start, mid) + inversionCount(arr, mid, end);

		int index1 = 0, index2 = 0;
		//index2 counts the number of arrL > arrR
		
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
	
	public static int numberColours(Color[][] face){
		int number = 0;
		ArrayList<Color> colorsSoFar = new ArrayList<Color>();
		for(int i = 0; i < face.length; i++){
			for(int j = 0; j < face[i].length; j++){
				if(!colorsSoFar.contains(face[i][j])){
					colorsSoFar.add(face[i][j]);
					number++;
				}
			}
		}
		return number;
	}
	
//	private static int numBlocks(Color[][] face){
//		class State{
//			int x;
//			int y;
//			public State(int xx, int yy){
//				x = xx;
//				y = yy;
//			}
//		}
//		
//		int M = face.length;
//		int N = face[0].length;
//		LinkedList<State> openList = new LinkedList<State>();
//		Boolean[][] closedList = new Boolean[M][N];
//		for(Boolean[] l: closedList){
//			Arrays.fill(l, false);
//		}
//		
//		for(int i = 0; i < M; i++){
//			for(int j = 0; j < N; j++){
//				openList.add(new State(i, j));
//			}
//		}
//		
//		while(!openList.isEmpty()){
//			State curr = openList.pop();
//			
//		}
//	}
}
