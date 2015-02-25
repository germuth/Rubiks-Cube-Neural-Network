package ca.germuth.neural_network;

import ca.germuth.neural_network.solvable.Solvable;

public interface Trainable {
	
	public double[] feedForward(double[] input);
	
	public Solvable getSolveable();
	
	public int getNumLayers();
	public int getLayerSize(int layer); 
	
	//maybe this???
	public double getNET(int i, int layer);
	public double getACT(int i, int layer);
	
	public double inputNET(int i);
	public double hiddenNET(int j, int layer);
	public double outputNET(int k);
	public double inputACT(int i);
	public double hiddenACT(int j, int layer);
	public double outputACT(int k);
	
	//getters and setters(while technically offsetters) for each edge weight
	public double getOuputToHiddenEW(int k, int j);
	public double getOutputToBiasEW(int k);
	public double getHiddenToInputEW(int j, int i);
	public double getBiasToHiddenEW(int j, int jLayer);
	public double getHiddenToHiddenEW(int j1, int j1Layer, int j2, int j2Layer);
	public void offsetOuputToHiddenEW(int k, int j, double offset);
	public void offsetOutputToBiasEW(int k, double offset);
	public void offsetHiddenToInputEW(int j, int i, double offset);
	public void offsetBiasToHiddenEW(int j, int jLayer, double offset);
	public void offsetHiddenToHiddenEW(int j1, int j1Layer, int j2, int j2Layer, double offset);
	
	public void randomizeAllEdgeWeights();
}
