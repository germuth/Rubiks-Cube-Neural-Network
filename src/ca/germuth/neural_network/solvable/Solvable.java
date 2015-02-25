package ca.germuth.neural_network.solvable;

public interface Solvable {
	public double[] mapTrainingInput(String trainInput);
	public double[] mapTrainingOutput(String trainOutput);
}
