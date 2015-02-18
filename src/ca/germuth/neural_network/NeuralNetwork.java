package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.Random;

public class NeuralNetwork implements Trainable{
	//Network squashes all values to inbetween -A and A
	//allows the network to handle massive inputs
	private static final double A_CONSTANT = 1.716;
	//B controls steepness
	//as B smaller, need larger activation to fire neuron
	//controls sensitivity of network
	private static final double B_CONSTANT = 0.667;
	//value for bias neuron
	private static final double BIAS_ACT = 1.0;
	
	/**
	 * Contains edge weights for every edge in the network, except the bias neuron
	 * For example:
	 * 	Edge between input neuron i, and first layer of hidden neuron j
	 *		netWorkLayers.get(0)[i][j]
	 *	Edge between hidden neuron j1 in hidden layer 2, and hidden neuron j2 in hidden layer 3
	 *		netWorkLayers.get(2)[j1][j2]
	 */
	private ArrayList<double[][]> networkEdgeWeights;
	/**
	 * Contains edge weights for every edge from each hidden layer to bias neuron, 
	 * and output layer to bias neuron
	 * For example:
	 * 	Edge between first hidden layer neuron j and bias neuron
	 * 		toBiasNeuron.get(0)[j]
	 *	Edge between output layer neuron k and bias neuron
	 *		toBiasNeuron.get(last_index)[k]
	 */
	private ArrayList<double[]> toBiasEdgeWeights;
	
	private Random rng;
	
	public NeuralNetwork(int numInput, int numOutput, int numHiddenLayer, int[] hiddenLayerSize){
		
		this.networkEdgeWeights = new ArrayList<double[][]>();
		//add input to first hidden layer
		this.networkEdgeWeights.add(new double[numInput][hiddenLayerSize[0]]);
		//add hidden layer A to hidden layer B
		for(int a = 1; a < numHiddenLayer; a++){
			this.networkEdgeWeights.add(new double[hiddenLayerSize[a-1]][hiddenLayerSize[a]]);
		}
		//add last hidden layer to output layer
		this.networkEdgeWeights.add(new double[hiddenLayerSize[numHiddenLayer-1]][numOutput]);
		
		this.toBiasEdgeWeights = new ArrayList<double[]>();
		//add edge from each hidden layer to bias
		for(int a = 0; a < numHiddenLayer; a++){
			this.toBiasEdgeWeights.add(new double[hiddenLayerSize[a]]);
		}
		//add edges from output to bias layer
		this.toBiasEdgeWeights.add(new double[numOutput]);
		
		this.rng = new Random();
		this.randomizeAllEdgeWeights();
	}
	
	// net input = input
	// act input = net input

	// net hidden = sum( Wji*ACTi + Wjb*Bias)
	// act hidden = A * tanh(B * NETj)

	// net output = sum( Wkj*ACTj + Wkb*Bias)
	// act output = A * tanh(B * NETk)

	// return act output
	@Override
	public double[] feedForward(double[] input) {
		//make sure it is exactly the same length
		if(input.length != this.getNumInputNeurons()){
			throw new IllegalArgumentException("More input features than input neurons");
		}
		
		ArrayList<double[]> ACT_values = new ArrayList<double[]>();
		for(int a = 0; a < getTotalLayers(); a++){
			ACT_values.add(new double[getLayerSize(a)]);
		}
		
		//for input NET == ACT == input
		for(int i = 0; i < input.length; i++){
			ACT_values.get(0)[i] = input[i];
		}
		
		//repeatedly calculate ACT and NET values for each layer
		//already have ACT values for input layer 
		for(int currentLayer = 1; currentLayer < getTotalLayers(); currentLayer++){
			for(int neuron = 0; neuron < getLayerSize(currentLayer); neuron++){
				//calculate NET for each neuron
				double NET = 0;
				
				//sum edge weights from last layer and bias neuron
				for(int prevNeuron = 0; prevNeuron < getLayerSize(currentLayer - 1); prevNeuron++){
					//TODO temp for debug
					double partOne = networkEdgeWeights.get(currentLayer - 1)[prevNeuron][neuron];
					partOne *= ACT_values.get(currentLayer -1 )[prevNeuron];
					double partTwo = toBiasEdgeWeights.get(currentLayer - 1)[neuron];
					partTwo *= BIAS_ACT;
					
					NET += (partOne + partTwo);
//					NET += networkEdgeWeights.get(currentLayer - 1)[prevNeuron][neuron] * ACT_values.get(currentLayer -1 )[prevNeuron] 
//							+ toBiasEdgeWeights.get(currentLayer - 1)[neuron] * BIAS_ACT;
				}
				
				ACT_values.get(currentLayer)[neuron] = A_CONSTANT * Math.tanh(B_CONSTANT * NET);
			}
		}
		
		return ACT_values.get(ACT_values.size() - 1);
	}
	
	@Override
	public void randomizeAllEdgeWeights() {
		for(double[][] layerEdges: this.networkEdgeWeights){
			randomizeAll(layerEdges);
		}
		for(double[] toBiasEdges: this.toBiasEdgeWeights){
			randomizeAll(toBiasEdges);
		}
	}

	@Override
	public double OuputToHiddenEdge(int k, int j) {
		double[][] hiddenToOutputEdges = this.networkEdgeWeights.get(this.networkEdgeWeights.size());
		return hiddenToOutputEdges[j][k];
	}

	@Override
	public double OutputToBiasEdge(int k) {
		double[] outputToBiasEdges = this.toBiasEdgeWeights.get(this.toBiasEdgeWeights.size());
		return outputToBiasEdges[k];
	}

	@Override
	public double HiddenToInputEdge(int j, int i) {
		double[][] inputToHiddenEdges = this.networkEdgeWeights.get(0);
		return inputToHiddenEdges[i][j];
	}
	
	@Override
	public double BiasToHiddenEdge(int j, int jLayer) {
		double[] hiddenToBiasEdges = this.toBiasEdgeWeights.get(jLayer);
		return hiddenToBiasEdges[j];
	}
	
	//TODO j2Layer not even required though...
	//TODO also are my hidden layers backwards here...
	@Override
	public double HiddenToHiddenEdge(int j1, int j1Layer, int j2, int j2Layer){
		double [][] hiddenToHiddenEdges = this.networkEdgeWeights.get(j1Layer);
		return hiddenToHiddenEdges[j1][j2];
	}
	
	// Returns random value in [-1, 1]
	private double getRandomEdgeWeight() {
		return (this.rng.nextDouble() * 2) - 1.0;
	}

	private void randomizeAll(double[] weights) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = getRandomEdgeWeight();
		}
	}

	private void randomizeAll(double[][] weights) {
		for (int i = 0; i < weights.length; i++) {
			randomizeAll(weights[i]);
		}
	}
	
	
	public int getNumInputNeurons(){
		return getLayerSize(0);
	}
	
	public int getNumOutputNeurons(){
		return getLayerSize(networkEdgeWeights.size());
	}
	
	public int getLayerSize(int layer){
		//if output layer
		if(layer == networkEdgeWeights.size()){
			return networkEdgeWeights.get(networkEdgeWeights.size()-1)[0].length;
		}else{
			return networkEdgeWeights.get(layer).length;			
		}
	}
	
	public int getTotalLayers(){
		return networkEdgeWeights.size() + 1;
	}
	
	
}
