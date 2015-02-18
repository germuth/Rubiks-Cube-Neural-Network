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
	
	private Double[][] firstHiddenToInput;
	private ArrayList<Double[][]> hiddenToHidden;
	private Double[][] outputToLastHidden;
	private ArrayList<Double[]> biasToHidden;
	private Double[] biasToOutput;
	private Random rng;
	//input    * hidden
	//hidden_i * hidden_j
	//hidden_j * output
	//hidden_i * bias
	//hidden_j * bias
	//output   * bias
	
	public NeuralNetwork(int numInput, int numOutput, int numHiddenLayer, int[] hiddenLayerSize){
		this.firstHiddenToInput = new Double[hiddenLayerSize[0]][numInput];
		
		this.hiddenToHidden = new ArrayList<Double[][]>(numHiddenLayer);
		for(int secondLayer = 1; secondLayer < numHiddenLayer; secondLayer++){
			int secondLayerSize = hiddenLayerSize[secondLayer];
			int firstLayerSize = hiddenLayerSize[secondLayer - 1];
			this.hiddenToHidden.add(new Double[secondLayerSize][firstLayerSize]);
		}
		
		this.outputToLastHidden = new Double[numOutput][hiddenLayerSize[numHiddenLayer - 1]];
		
		this.biasToHidden = new ArrayList<Double[]>(numHiddenLayer);
		for(int hidden = 0; hidden < numHiddenLayer; hidden++){
			this.biasToHidden.add(new Double[hiddenLayerSize[hidden]]);
		}
		
		this.biasToOutput = new Double[numOutput];
		
		this.rng = new Random();
		this.randomizeAllEdgeWeights();
	}
	
	@Override
	public double[] feedForward(double[] input){
		//make sure it is exactly the same length
		if(input.length != this.getNumInputNeurons()){
			throw new IllegalArgumentException("More input features than input neurons");
		}
		
		//net input = input
		//act input = net input
		
		//net hidden = sum( Wji*ACTi + Wjb*Bias)
		double[] NET_hiddenFirstLayer = new double[this.getHiddenLayerSize(0)];
		
		//for each neuron in first layer, calculate NET
		for(int j = 0; j < this.getHiddenLayerSize(0); j++){
			//calculate NET_hidden for this neuron in the first layer
			//by calculating a sum over each input neuron
			for(int i = 0; i < this.getNumInputNeurons(); i++){
				//Wji * ACTi
				NET_hiddenFirstLayer[j] += this.HiddenToInputEdge(j, i) * input[i];
				NET_hiddenFirstLayer[j] += this.BiasToHiddenEdge(j) * BIAS_ACT;
			}
		}
		
		//act hidden = A * tanh(B * NETj)
		
		//net output = sum( Wkj*ACTj + Wkb*Bias)
		//act output = A * tanh(B * NETk)
		
		//return act output
		return null;
	}
	
	@Override
	public void randomizeAllEdgeWeights() {

		randomizeAll(this.firstHiddenToInput);

		for(int k = 0; k < hiddenToHidden.size(); k++){
			randomizeAll(this.hiddenToHidden.get(k));
		}
		
		randomizeAll(this.outputToLastHidden);
		
		for(int j = 0; j < biasToHidden.size(); j++){
			randomizeAll(this.biasToHidden.get(j));
		}
		
		randomizeAll(this.biasToOutput);
	}

	@Override
	public double OuputToHiddenEdge(int k, int j) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double OutputToBiasEdge(int k) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double HiddenToInputEdge(int j, int i) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public double BiasToHiddenEdge(int j) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	//Returns random value in [-1, 1]
	private double getRandomEdgeWeight(){
		return (this.rng.nextDouble() * 2) - 1.0;
	}
	
	private void randomizeAll(Double[] weights){
		for(int i = 0; i < weights.length; i++){
			weights[i] = getRandomEdgeWeight();
		}
	}
	
	private void randomizeAll(Double[][] weights){
		for(int i = 0; i < weights.length; i++){
			randomizeAll(weights[i]);
		}
	}
	
	public int getNumInputNeurons(){
		return this.firstHiddenToInput[0].length;
	}
	
	public int getNumOutputNeurons(){
		return this.outputToLastHidden.length;
	}
	
	public int getNumHiddenLayers(){
		return this.hiddenToHidden.size() - 1;
	}
	
	public int getHiddenLayerSize(int layer){
		return this.hiddenToHidden.get(layer).length;
	}
	
	public int[] getHiddenLayerSizes(){
		int arr[] = new int[getNumHiddenLayers()];
		for(int i = 0; i < arr.length; i++){
			arr[i] = this.hiddenToHidden.get(i).length;
		}
		return arr;
	}
}
