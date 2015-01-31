package ca.germuth.neural_network.gui;

/**
 * GLColor
 * 
 * Represents a Color in OpenGL
 * 
 * Red green blue are float values from 0 -> 1.0
 * 
 * @author Administrator
 */
public class GLColor {

	private final float red;
	private final float green;
	private final float blue;

	public GLColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GLColor) {
			GLColor g = (GLColor) o;
			if (this.red == g.red && this.green == g.green && this.blue == g.blue) {
				return true;
			}
		}
		return false;
	}

	//getters
	public float getRed() {
		return red;
	}
	public float getGreen() {
		return green;
	}
	public float getBlue() {
		return blue;
	}
}
