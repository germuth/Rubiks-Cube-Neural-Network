package ca.germuth.neural_network.gui;

import java.util.ArrayList;

import ca.germuth.neural_network.Color;
import ca.germuth.neural_network.Cube;
import ca.germuth.neural_network.Face;
import ca.germuth.neural_network.gui.shapes.GLSquare;
import ca.germuth.neural_network.gui.shapes.GLVertex;

public class RubixCubeDrawer {
	
	public static ArrayList<GLSquare> createPuzzleModel(Cube cube) {
		ArrayList<GLSquare> myFaces = new ArrayList<GLSquare>();

		myFaces.addAll(drawFace(Face.UP, cube));
		
		ArrayList<GLSquare> front = drawFace(Face.FRONT, cube);
		GLSquare.rotateAll(front, 'X', (float) Math.PI / 2);
		myFaces.addAll(front);
		
		ArrayList<GLSquare> back = drawFace(Face.BACK, cube);
		GLSquare.rotateAll(back, 'X', (float) -Math.PI / 2);
		myFaces.addAll(back);
		
		ArrayList<GLSquare> left = drawFace(Face.LEFT, cube);
		GLSquare.rotateAll(left, 'Z', (float) (Math.PI / 2));
		myFaces.addAll(left);
		
		ArrayList<GLSquare> right = drawFace(Face.RIGHT, cube);
		GLSquare.rotateAll(right, 'Z', (float) (-Math.PI / 2));
		myFaces.addAll(right);
		
		ArrayList<GLSquare> bot = drawFace(Face.DOWN, cube);
		GLSquare.rotateAll(bot, 'X', (float) Math.PI);
		myFaces.addAll(bot);

		return myFaces;
	}

	private static ArrayList<GLSquare> drawFace(Face face, Cube cube) {
		Color[][] faceArr = cube.getFace(face);
		int size = cube.getsize();
		
		ArrayList<GLSquare> faceShapes = new ArrayList<GLSquare>();
		
		float faceLength = (240f / size);
		// divide by 100 since entire model is with 1 of origin
//		faceLength /= 100;
		faceLength /= 10;

		float spaceLength = 40f / (size + 1);
//		spaceLength /= 100;
		spaceLength /= 10;

		float height = 14.0f;

		// actual topCorner is only 1.4 if current side we are drawing has maximum amount of pieces
		float topCornerX = -14.0f;
		float topCornerZ = -14.0f;

		// adjust topCorner depending on Local height and width
		// largest height
		// 1.4 x
		topCornerX = -14.0f;
		topCornerZ = -14.0f;

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
								currentTopRightZ), face, i, j);
				faceShapes.add(current);

				currentTopRightX += faceLength + spaceLength;
			}

			// once we finish a row, we need to move down to next row
			currentTopRightZ += faceLength + spaceLength;
		}

		return faceShapes;
	}
}
