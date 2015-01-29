package ca.germuth.neural_network.gui;

import java.util.ArrayList;

import ca.germuth.neural_network.Color;
import ca.germuth.neural_network.Cube;
import ca.germuth.neural_network.Face;
import ca.germuth.neural_network.gui.shapes.GLSquare;
import ca.germuth.neural_network.gui.shapes.GLVertex;

public class RubixCubeDrawer {
	
	public static GLColor toGLColor(Color col){
		switch(col){
			case BLUE: return new GLColor(0f, 0f, 1f); 
			case GREEN: return new GLColor(0f, 1f, 0f); 
			case RED: return new GLColor(1f, 0f, 0f); 
			case WHITE: return new GLColor(1f, 1f, 1f); 
			case ORANGE: return new GLColor(1f, 0.5f, 0f); 
			case YELLOW: return new GLColor(1f, 1f, 0f);
			default:
				return null;
		}
	}
	
	public static ArrayList<GLSquare> createPuzzleModel(Cube cube) {
		ArrayList<GLSquare> myFaces = new ArrayList<GLSquare>();

		myFaces.addAll(drawFace(cube.getFace(Face.UP), cube.getsize()));
//		ArrayList<GLShape> front = drawFace(cube.getFace(Face.FRONT), cube.getsize());
//		
//		myFaces.addAll();
//		myFaces.addAll(drawFace(cube.getFace(Face.BACK), cube.getsize()));
//		myFaces.addAll(drawFace(cube.getFace(Face.LEFT), cube.getsize()));
//		myFaces.addAll(drawFace(cube.getFace(Face.RIGHT), cube.getsize()));
//		myFaces.addAll(drawFace(cube.getFace(Face.DOWN), cube.getsize()));
		

//		ArrayList<GLShape> frontF = drawFace(front, height, width, depth);
//		Square.rotateAll(frontF, 'X', (float) Math.PI / 2);
//		Square.finalizeAll(frontF);
//		myFaces.addAll(frontF);
//
//		ArrayList<GLShape> backF = drawFace(back, height, width, depth);
//		Square.rotateAll(backF, 'X', (float) -Math.PI / 2);
//		Square.finalizeAll(backF);
//		myFaces.addAll(backF);
//
//		ArrayList<GLShape> leftF = drawFace(left, depth, height, width);
//		Square.rotateAll(leftF, 'Z', (float) (Math.PI / 2));
//		Square.finalizeAll(leftF);
//		myFaces.addAll(leftF);
//
//		ArrayList<GLShape> rightF = drawFace(right, depth, height, width);
//		Square.rotateAll(rightF, 'Z', (float) -(Math.PI / 2));
//		Square.finalizeAll(rightF);
//		myFaces.addAll(rightF);
//
//		// bottom can't be rotated while preserving orientation
//		// must translate directly down
//		// this might not be true somehow
//		// definetly not true somehow
//		ArrayList<GLShape> bottomF = drawFace(down, depth, width, height);
//		// Square.translateAll(mBottom, 'Y', -1.40);
//		Square.rotateAll(bottomF, 'X', (float) (Math.PI));
//		Square.finalizeAll(bottomF);
//		myFaces.addAll(bottomF);

		return myFaces;
	}

	private static ArrayList<GLSquare> drawFace(Color[][] face, int size) {
		ArrayList<GLSquare> faceShapes = new ArrayList<GLSquare>();

		
		float faceLength = (240f / size);
		// divide by 100 since entire model is with 1 of origin
		faceLength /= 100;

		float spaceLength = 40f / (size + 1);
		spaceLength /= 100;

		float height = 1.40f;
		// adjust height based on smaller of width or height
		height = 1.40f;

		// actual topCorner is only 1.4 if current side we are drawing has maximum amount of pieces
		float topCornerX = -1.40f;
		float topCornerZ = -1.40f;

		// adjust topCorner depending on Local height and width
		// largest height
		// 1.4 x
		topCornerX = -1.40f;
		topCornerZ = -1.40f;

		topCornerX += spaceLength;
		topCornerZ += spaceLength;

		float currentTopRightX = topCornerX;
		float currentTopRightZ = topCornerZ;

		// iterate down
		for (int i = 0; i < size; i++) {
			// reset top right coordinates after each row
			currentTopRightX = topCornerX;

			// iterate across
			for (int j = 0; j < size; j++) {

				// make face
				GLSquare current = new GLSquare(
						new GLVertex(currentTopRightX, height, currentTopRightZ), new GLVertex(
								currentTopRightX, height, currentTopRightZ + faceLength),
						new GLVertex(currentTopRightX + faceLength, height, currentTopRightZ
								+ faceLength), new GLVertex(currentTopRightX + faceLength, height,
								currentTopRightZ));
				current.setColor(toGLColor(face[i][j]));
				faceShapes.add(current);

				currentTopRightX += faceLength + spaceLength;
			}

			// once we finish a row, we need to move down to next row
			currentTopRightZ += faceLength + spaceLength;
		}

//		for (int i = 0; i < side.mFace.length; i++) {
//			for (int j = 0; j < side.mFace[i].length; j++) {
//				int index = i * side.mFace[i].length + j;
//				Tile t1 = side.mFace[i][j];
//				t1.setmGLShape(face.get(index));
//			}
//		}

		return faceShapes;
	}
}
