package ca.germuth.neural_network.solvable;
/**
 * Solvable
 * 
 * A solvable object is one that can be mapped to a input and output, such as 
 * the input and output neurons of a neural network. For example, A Rubix's cube 
 * or XOR can both be mapped so they are both solvable
 * @author Aaron
 */
public interface Solvable {
	public double[] mapTrainingInput(String trainInput);
	public double[] mapTrainingOutput(String trainOutput);
}
