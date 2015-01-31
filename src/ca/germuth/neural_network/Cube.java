package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Cube.java
 * 
 * Implements any size NxNxN rubik's cube. All turns of the cube
 * using the SiGN notation.
 * 
 * @author Germuth
 */
public class Cube implements Searchable {

	//Creates a new 2d array of length size*size
	//All elements initialized to Color c
	private static Color[][] createCubeFace(Color c, int size) {
		Color[][] arr = new Color[size][size];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				arr[i][j] = c;
			}
		}
		return arr;
	}

	private int size;
	private Color[][] up;
	private Color[][] front;
	private Color[][] left;
	private Color[][] right;
	private Color[][] down;
	private Color[][] back;
	private String moveTaken;
	
	//these values are saved to avoid recalculating
	private String key = null;
	private double heuristic = 0.0;

	public Cube(int size) {
		this.size = size;
		this.moveTaken = null;
		this.setSolved();
	}

	public Cube(Cube another){
		this.size = another.size;
		this.up = Utils.copy(another.up);
		this.front = Utils.copy(another.front);
		this.left = Utils.copy(another.left);
		this.right = Utils.copy(another.right);
		this.down = Utils.copy(another.down);
		this.back = Utils.copy(another.back);
		this.moveTaken = another.moveTaken;
		this.key = another.key;
		this.heuristic = another.heuristic;
	}

	public void setSolved() {
		this.up = createCubeFace(Color.WHITE, size);
		this.front = createCubeFace(Color.GREEN, size);
		this.left = createCubeFace(Color.ORANGE, size);
		this.right = createCubeFace(Color.RED, size);
		this.down = createCubeFace(Color.YELLOW, size);
		this.back = createCubeFace(Color.BLUE, size);
	}

	public Color getColor(Face f, int row, int col){
		return faceToArray(f)[row][col];
	}
	
	private void setColor(Face f, int row, int col, Color color){
		faceToArray(f)[row][col] = color;
	}

	//Performs a horizontal rotation
	//when layer == 0, a U turn is done
	//when layer == size, a D' turn is done 
	private void horizontalRotation(int layer) {
		for(int j = 0; j < size; j++){
			Color temp = getColor(Face.FRONT, layer, j);
			setColor(Face.FRONT, layer, j, 
					getColor(Face.RIGHT, size - j - 1, layer));
			setColor(Face.RIGHT, size - j - 1, layer, 
					getColor(Face.BACK, size - layer - 1, size - j - 1));
			setColor(Face.BACK, size - layer - 1, size - j - 1, 
					getColor(Face.LEFT, j, size - layer - 1));
			setColor(Face.LEFT, j, size - layer - 1, temp);
		}
	}

	//Performs a vertical rotation
	//when layer == 0, an R turn
	//when layer == size, L' turn
	private void verticalRotation(int layer) {
		for(int j = 0; j < size; j++){
			Color temp = getColor(Face.UP, j, layer);
			setColor(Face.UP, j, layer, 
					getColor(Face.FRONT, j, layer));
			setColor(Face.FRONT, j, layer, 
					getColor(Face.DOWN, j, layer));
			setColor(Face.DOWN, j, layer, 
					getColor(Face.BACK, j, layer));
			setColor(Face.BACK, j, layer, temp);
		}
	}
	
	//Performs a front rotation
	//when layer == 0, F turn
	//when layer == size, B turn
	private void intoScreenRotation(int layer) {
		for(int j = 0; j < size; j++){
			Color temp = getColor(Face.UP, layer, j);
			setColor(Face.UP, layer, j, 
					getColor(Face.LEFT, layer, j));
			setColor(Face.LEFT, layer, j, 
					getColor(Face.DOWN, size - layer - 1, size - j - 1));
			setColor(Face.DOWN, size - layer - 1, size - j - 1, 
					getColor(Face.RIGHT, layer, j));
			setColor(Face.RIGHT, layer, j, temp);
		}
	}

	//rotates a face of the cube 90 degrees clockwise
	private void rotateFace(Color[][] face) {
		face = Utils.rotateMatrixClockwise(face);
	}
	
	//Performs a turn based a string sequence of moves,
	//encoded as per SiGN specification with spaces in between
	public void turn(String moveSequence){
		String[] moves = moveSequence.split(" ");
		for(String move: moves){
			Direction d = move.endsWith("\'") ? Direction.CCW : Direction.CW;
			Face f = null;
			int depth;
			char currChar = move.charAt(0);
			if(Character.isAlphabetic(currChar)){
				depth = 0;
			}else{
				String depthStr = currChar + "";
				int i = 1;
				while(!Character.isAlphabetic(move.charAt(i))){
					depthStr += move.charAt(i);
					i++;
				}
				depth = Integer.parseInt(depthStr);
				currChar = move.charAt(i);
			}
			
			switch(currChar){
				case 'U': f = Face.UP; break;
				case 'D': f = Face.DOWN; break;
				case 'F': f = Face.FRONT; break;
				case 'B': f = Face.BACK; break;
				case 'L': f = Face.LEFT; break;
				case 'R': f = Face.RIGHT; break;
			}
			
			this.turn(f, d, depth);			
		}
		
	}
	
	//Turns the cube based on a Face, Direction, and depth
	public void turn(Face move, Direction d, int depth){
		//do 1 move if CW, 3 moves if CCW
		int repeats = (d == Direction.CW) ? 1 : 3;
		for (int i = 0; i < repeats; i++) {
			switch (move) {
				case LEFT : LTurn(depth); break;
				case RIGHT : RTurn(depth); break;
				case UP : UTurn(depth); break;
				case DOWN : DTurn(depth); break;
				case FRONT : FTurn(depth); break;
				case BACK : BTurn(depth); break;
			}
		}
		if(d == Direction.CCW){
			this.moveTaken += "'";
		}
		//key is no longer valid
		this.key = null;
		this.heuristic = 0.0;
	}
	
	//Randomly applies moves to the cube
	//returns the moves perform in String sequence as per SiGN specification
	public String scrambleCube(int length){
		Random r = new Random();
		String moveSequence = "";
		for(int i = 0; i < length; i++){
			switch(r.nextInt(12)){
				case 0: moveSequence += "F "; break;
				case 1: moveSequence += "F' "; break;
				case 2: moveSequence += "U "; break;
				case 3: moveSequence += "U' "; break;
				case 4: moveSequence += "D "; break;
				case 5: moveSequence += "D' "; break;
				case 6: moveSequence += "L "; break;
				case 7: moveSequence += "L' "; break;
				case 8: moveSequence += "R "; break;
				case 9: moveSequence += "R' "; break;
				case 10: moveSequence += "B "; break;
				case 11: moveSequence += "B' "; break;
			}
		}
		
		this.turn(moveSequence);
		this.eraseMoveHistory();
		return moveSequence;
	}

	//Below Methods Performs each turn of SiGN
	public void RTurn(int depth) {
		this.verticalRotation(size - depth - 1);
		
		if (depth == 0) {
			this.rotateFace(this.right);
		}
		this.moveTaken = (depth + 1) + "R";
	}
	
	public void LTurn(int depth){
		for(int i = 0; i < 3; i++){
			this.verticalRotation(depth);
		}
		if(depth == 0){
			this.rotateFace(this.left);
		}
		this.moveTaken = (depth + 1) + "L";
	}

	public void UTurn(int depth) {
		this.horizontalRotation(depth);

		if (depth == 0) {
			this.rotateFace(this.up);
		}
		this.moveTaken = (depth + 1) + "U";
	}

	public void DTurn(int depth){
		for(int i = 0; i < 3; i++){
			this.horizontalRotation(size - depth - 1);
		}
		if(depth == 0){
			this.rotateFace(this.down);
		}
		this.moveTaken = (depth + 1) + "D";
	}
	
	public void FTurn(int depth) {
		this.intoScreenRotation((size - 1) - depth);
		
		this.rotateFace(this.front);
		this.moveTaken = (depth + 1) + "F";
	}

	public void BTurn(int depth){
		for(int i = 0; i < 3; i++){
			this.intoScreenRotation(depth);
		}
		
		if(depth == 0){
			this.rotateFace(this.back);
		}
		this.moveTaken = (depth + 1) + "B";
	}
	
	//Generates all possible moves that could be taken from the current state, 
	//and creates a new cube with this moved applied. Returns the list of cubes
	@Override
	public ArrayList<Searchable> getChildren() {
		ArrayList<Searchable> children = new ArrayList<Searchable>();
		Direction[] directions = {Direction.CW, Direction.CCW};
		Face[] faces = {Face.UP, Face.DOWN, Face.LEFT, Face.RIGHT, Face.FRONT, Face.BACK};
		for(Face f: faces){
			for(Direction d: directions){
				for(int depth = 0; depth < size / 2; depth++){
					Cube child = new Cube(this);
					child.turn(f, d, depth);
					children.add(child);
				}
			}
		}
		return children;
	}

	//Returns a unique string representation of the current state of this rubik's cube
	@Override
	public String getKey() {
		if(key == null){
			key = Arrays.deepToString(this.back) + //blue 
					Arrays.deepToString(this.front) + //green
					Arrays.deepToString(this.left) + //orange
					Arrays.deepToString(this.right) + //red
					Arrays.deepToString(this.up) + //white
					Arrays.deepToString(this.down); //yellow
		}
		return key;
	}

	//Calculates a heuristic estimating the number of moves required to 
	//solve the cube. Finds the average amount of distinct colors on
	//each side to estimate completeness
	@Override
	public double calcHeuristic() {
		if(heuristic == 0.0){
			heuristic = Utils.numberColours(this.up) +
					Utils.numberColours(this.down) +
					Utils.numberColours(this.left) +
					Utils.numberColours(this.right) +
					Utils.numberColours(this.front) +
					Utils.numberColours(this.back);
			heuristic = (heuristic / 6) - 1;
		}
		return heuristic;
	}

	//used to erase the moves taken before searhcing the cube
	public void eraseMoveHistory(){
		this.moveTaken = null;
	}
	
	//Returns the last move performed by this cube
	@Override
	public String getMoveTaken() {
		return moveTaken;
	}
	public int getsize() {
		return size;
	}

	private Color[][] faceToArray(Face f){
		switch(f){
			case UP: return this.up;
			case DOWN: return this.down;
			case LEFT: return this.left;
			case RIGHT: return this.right;
			case FRONT: return this.front;
			case BACK: return this.back;
			default:
				return null;
		}
	}
	
	public Color[][] getFace(Face f){
		return this.faceToArray(f);
	}
}