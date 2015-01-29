package ca.germuth.neural_network.gui.shapes;

/**
 * Represents a vertex in openGL
 * 
 * @author Administrator
 *
 */
public class GLVertex {
	/**
	 * X coordinate
	 */
	private float x;
	/**
	 * Y coordinate
	 */
	private float y;
	/**
	 * Z coordinate
	 */
	private float z;

	public GLVertex() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public GLVertex(GLVertex other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	public GLVertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Compares two verticies by their coordinate
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof GLVertex) {
			GLVertex g = (GLVertex) o;
			if (this.x == g.x && this.y == g.y && this.z == g.z) {
				return true;
			}
		}
		return false;
	}

	public void translate(char axis, double distance) {
		if (axis == 'X') {
			this.x += distance;
		} else if (axis == 'Y') {
			this.y += distance;
		} else if (axis == 'Z') {
			this.z += distance;
		}
	}

	/**
	 * Rotates a single point
	 * 
	 * @param face
	 * @param axis
	 * @param radians
	 */
	public void rotate(char axis, float radians) {
		float x = this.x;
		float y = this.y;
		float z = this.z;

		float x2 = x;
		float y2 = y;
		float z2 = z;

		if (axis == 'X') {
			// y' = y*cos q - z*sin q
			// z' = y*sin q + z*cos q
			// x' = x
			y2 = (float) (y * Math.cos(radians) - z * Math.sin(radians));
			z2 = (float) (y * Math.sin(radians) + z * Math.cos(radians));
		} else if (axis == 'Y') {
			// z' = z*cos q - x*sin q
			// x' = z*sin q + x*cos q
			// y' = y
			z2 = (float) (z * Math.cos(radians) - x * Math.sin(radians));
			x2 = (float) (z * Math.sin(radians) + x * Math.cos(radians));
		} else if (axis == 'Z') {
			// x' = x*cos q - y*sin q
			// y' = x*sin q + y*cos q
			// z' = z
			x2 = (float) (x * Math.cos(radians) - y * Math.sin(radians));
			y2 = (float) (x * Math.sin(radians) + y * Math.cos(radians));
		}

		// set new coordinates
		this.x = x2;
		this.y = y2;
		this.z = z2;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the z
	 */
	public float getZ() {
		return z;
	}

	/**
	 * @param z
	 *            the z to set
	 */
	public void setZ(float z) {
		this.z = z;
	}
}
