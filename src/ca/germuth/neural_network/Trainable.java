package ca.germuth.neural_network;

public interface Trainable {
	
	public double[] feedForward(double[] input);
	
	//NET and ACT methods??
	
	
	public void randomizeAllEdgeWeights();
	public double OuputToHiddenEdge(int k, int j);
	public double OutputToBiasEdge(int k);
	public double HiddenToInputEdge(int j, int i);
	public double BiasToHiddenEdge(int j);
}
