package ca.germuth.neural_network.gui;

/**
 * Represents a colour in the openGL 3d model
 * 
 * @author Administrator
 *
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

	/**
	 * Testes whether two colours are equal by their red, green, blue and alpha values
	 */
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

	/**
	 * @return the red
	 */
	public float getRed() {
		return red;
	}

	/**
	 * @return the green
	 */
	public float getGreen() {
		return green;
	}

	/**
	 * @return the blue
	 */
	public float getBlue() {
		return blue;
	}
}
