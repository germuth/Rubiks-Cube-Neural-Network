package ca.germuth.neural_network.trainable;

import java.util.ArrayList;
import java.util.Random;

import ca.germuth.neural_network.solvable.Solvable;
/**
 * Neural Network
 * 
 * @author Aaron
 */
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
	
	//TODO what am i doing with my life, this should be its own object
	/**
	 * Contains edge weights for every edge in the network, except the bias neuron
	 * For example:
	 * 	Edge between input neuron i, and first layer of hidden neuron j
	 *		netWorkLayers.get(0)[i][j]
	 *	Edge between hidden neuron j1 in hidden layer 2, and hidden neuron j2 in hidden layer 3
	 *		netWorkLayers.get(2)[j1][j2]
	 */
	private ArrayList<double[][]> networkEdgeWeights;
	//TODO this as well probably
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
	
	/**
	 * Store the latest NET and ACT values for each node from the most
	 * recent feed Forward
	 */
	private ArrayList<double[]> latestNET;
	private ArrayList<double[]> latestACT;
	
	private Random rng;
	private Solvable solveable;
	
	public NeuralNetwork(Solvable solvable, int numInput, int numOutput, int numHiddenLayer, int[] hiddenLayerSize){
		this.solveable = solvable;
		
		init(numInput,numOutput, numHiddenLayer, hiddenLayerSize);
	}
	
	public NeuralNetwork(Solvable solvable, ArrayList<Integer> layerSizes, 
			ArrayList<Double> edgeWeights){
		int numInput = layerSizes.get(0);
		int numOutput = layerSizes.get( layerSizes.size() - 1);
		int numHiddenLayer = layerSizes.size() - 2;
		int[] hiddenLayerSize = new int[numHiddenLayer];
		for(int i = 1; i < layerSizes.size() - 1; i++){
			hiddenLayerSize[i - 1] = layerSizes.get(i);
		}
		
		this.solveable = solvable;
		init(numInput, numOutput, numHiddenLayer, hiddenLayerSize);
		
		updateEdgeWeights(edgeWeights);
	}
	
	public void init(int numInput, int numOutput, int numHiddenLayer, int[] hiddenLayerSize){
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
		
		latestNET = new ArrayList<double[]>();
		latestACT = new ArrayList<double[]>();
		for(int a = 0; a < getNumLayers(); a++){
			latestNET.add(new double[getLayerSize(a)]);
			latestACT.add(new double[getLayerSize(a)]);
		}
	}

	@Override
	public double[] feedForward(double[] input) {
		//make sure it is exactly the same length
		if(input.length != this.getNumInputNeurons()){
			throw new IllegalArgumentException("More input features than input neurons");
		}
		
		//for input NET == ACT == input
		for(int i = 0; i < input.length; i++){
			latestNET.get(0)[i] = input[i];
			latestACT.get(0)[i] = input[i];
		}
		
		//repeatedly calculate ACT and NET values for each layer
		//already have ACT values for input layer 
		for(int currentLayer = 1; currentLayer < getNumLayers(); currentLayer++){
			for(int neuron = 0; neuron < getLayerSize(currentLayer); neuron++){
				//calculate NET for each neuron
				double NET = 0;
				
				//sum edge weights from last layer
				for(int prevNeuron = 0; prevNeuron < getLayerSize(currentLayer - 1); prevNeuron++){
					//TODO temp for debug
					double partOne = networkEdgeWeights.get(currentLayer - 1)[prevNeuron][neuron];
					partOne *= latestACT.get(currentLayer -1 )[prevNeuron];
					NET += partOne;
				}
				//TODO: change variable names: NET is only added once, it is considered as an extra input neuron
				double partTwo = toBiasEdgeWeights.get(currentLayer - 1)[neuron];
				partTwo *= BIAS_ACT;
				NET += partTwo;
				
				latestNET.get(currentLayer)[neuron] = NET;
				latestACT.get(currentLayer)[neuron] = A_CONSTANT * Math.tanh(B_CONSTANT * NET);
			}
		}
		
		return latestACT.get(latestACT.size() - 1);
	}
	
	
	public ArrayList<String> toFile(){
		ArrayList<String> file = new ArrayList<String>();
		file.add("Neural Network:");
		String layers = "";
		for(int i = 0; i < this.getNumLayers(); i++){
			layers += this.getLayerSize(i) + ",";
		}
		file.add(layers);
		
		//TODO use weightList()
		String weights = "";
		for(int i = 0; i < networkEdgeWeights.size(); i++){
			double[][] arr = networkEdgeWeights.get(i);
			for(int j = 0; j < arr.length; j++){
				for(int k = 0; k < arr[j].length; k++){
					weights += arr[j][k] + ",";
				}
			}
		}
		for(int i = 0; i < toBiasEdgeWeights.size(); i++){
			double[] toBias = toBiasEdgeWeights.get(i);
			for(int j = 0; j < toBias.length; j++){
				weights += toBias[j] + ",";
			}
		}
		file.add(weights);
		return file;
	}
	
	public ArrayList<Integer> layerSizes(){
		ArrayList<Integer> layerSizes = new ArrayList<Integer>();
		for(int i = 0; i < this.getNumLayers(); i++){
			layerSizes.add( this.getLayerSize(i) );
		}
		return layerSizes;
	}
	
	public int[] hiddenLayerSizes(){
		int[] layerSizes = new int[getNumLayers() - 2];
		int index = 0;
		for(int i = 1; i < this.getNumLayers() - 1; i++){
			layerSizes[index++] = this.getLayerSize(i);
		}
		return layerSizes;
	}
	
	public void updateEdgeWeights(ArrayList<Double> edgeWeights){
		int index = 0;
		//load edge values
		for(int i = 0; i < networkEdgeWeights.size(); i++){
			double[][] arr = networkEdgeWeights.get(i);
			for(int j = 0; j < arr.length; j++){
				for(int k = 0; k < arr[j].length; k++){
					arr[j][k] = edgeWeights.get(index++);
				}
			}
		}
		for(int i = 0; i < toBiasEdgeWeights.size(); i++){
			double[] toBias = toBiasEdgeWeights.get(i);
			for(int j = 0; j < toBias.length; j++){
				toBias[j] = edgeWeights.get(index++);
			}
		}
	}
	
	public ArrayList<Double> weightList(){
		ArrayList<Double> weights = new ArrayList<Double>();
		for(int i = 0; i < networkEdgeWeights.size(); i++){
			double[][] arr = networkEdgeWeights.get(i);
			for(int j = 0; j < arr.length; j++){
				for(int k = 0; k < arr[j].length; k++){
					weights.add(arr[j][k]);
				}
			}
		}
		for(int i = 0; i < toBiasEdgeWeights.size(); i++){
			double[] toBias = toBiasEdgeWeights.get(i);
			for(int j = 0; j < toBias.length; j++){
				weights.add(toBias[j]);
			}
		}
		return weights;
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
	public double getOuputToHiddenEW(int k, int j) {
		double[][] hiddenToOutputEdges = this.networkEdgeWeights.get(this.networkEdgeWeights.size() - 1);
		return hiddenToOutputEdges[j][k];
	}

	@Override
	public double getOutputToBiasEW(int k) {
		double[] outputToBiasEdges = this.toBiasEdgeWeights.get(this.toBiasEdgeWeights.size() - 1);
		return outputToBiasEdges[k];
	}

	@Override
	public double getHiddenToInputEW(int j, int i) {
		double[][] inputToHiddenEdges = this.networkEdgeWeights.get(0);
		return inputToHiddenEdges[i][j];
	}
	
	@Override
	public double getBiasToHiddenEW(int j, int jLayer) {
		double[] hiddenToBiasEdges = this.toBiasEdgeWeights.get(jLayer);
		return hiddenToBiasEdges[j];
	}
	
	@Override
	public double getHiddenToHiddenEW(int j1, int j1Layer, int j2, int j2Layer){
		double [][] hiddenToHiddenEdges = this.networkEdgeWeights.get(j1Layer);
		return hiddenToHiddenEdges[j1][j2];
	}
	
	@Override
	public double getEW(int a, int aLayer, int b, int bLayer) {
		if(aLayer + 1 != bLayer){
			throw new IllegalArgumentException("Layers must satisify the following: A + 1 = B");
		}
		//input to hidden
		if(aLayer == 0){
			return getHiddenToInputEW(b, a);
		}
		//last hidden to output
		if(bLayer == this.getNumLayers() - 1){
			return getOuputToHiddenEW(b, a);
		}
		//hidden to hidden
		else{
			return getHiddenToHiddenEW(a, aLayer, b, bLayer);
		}
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
	
	@Override
	public int getLayerSize(int layer){
		//if output layer
		if(layer == networkEdgeWeights.size()){
			return networkEdgeWeights.get(networkEdgeWeights.size()-1)[0].length;
		}else{
			return networkEdgeWeights.get(layer).length;			
		}
	}
	
	@Override
	public int getNumLayers(){
		return networkEdgeWeights.size() + 1;
	}
	
	@Override
	public double inputNET(int i) {
		return latestNET.get(0)[i];
	}

	@Override
	public double hiddenNET(int j, int layer) {
		return latestNET.get(layer)[j];
	}

	@Override
	public double outputNET(int k) {
		return latestNET.get(getNumLayers() - 1)[k];
	}

	@Override
	public double inputACT(int i) {
		return latestACT.get(0)[i];
	}

	@Override
	public double hiddenACT(int j, int layer) {
		return latestACT.get(layer)[j];
	}

	@Override
	public double outputACT(int k) {
		return latestACT.get(getNumLayers() - 1)[k];
	}

	@Override
	public double getNET(int i, int layer) {
		int last = getNumLayers();
		if(i == 0){
			return inputNET(i);
		}else if(i == last - 1){
			return outputNET(i);
		}else{
			return hiddenNET(i, layer);
		}
	}

	@Override
	public double getACT(int i, int layer) {
		int last = getNumLayers();
		if(i == 0){
			return inputACT(i);
		}else if(i == last - 1){
			return outputACT(i);
		}else{
			return hiddenACT(i, layer);
		}
	}

	@Override
	public void offsetOuputToHiddenEW(int k, int j, double offset) {
		this.networkEdgeWeights.get(this.networkEdgeWeights.size() - 1)[j][k] += offset;
	}

	@Override
	public void offsetOutputToBiasEW(int k, double offset) {
		this.toBiasEdgeWeights.get(this.toBiasEdgeWeights.size() - 1)[k] += offset;
	}

	@Override
	public void offsetHiddenToInputEW(int j, int i, double offset) {
		this.networkEdgeWeights.get(0)[i][j] += offset;
	}

	@Override
	public void offsetBiasToHiddenEW(int j, int jLayer, double offset) {
		this.toBiasEdgeWeights.get(jLayer)[j] += offset;
	}

	@Override
	public void offsetHiddenToHiddenEW(int j1, int j1Layer, int j2, int j2Layer, double offset) {
		this.networkEdgeWeights.get(j1Layer)[j1][j2] += offset;
	}
	
	@Override
	public void offsetEW(int a, int aLayer, int b, int bLayer, double offset) {
		if(aLayer + 1!= bLayer){
			throw new IllegalArgumentException("Layers must satisify the following: A + 1 = B");
		}
		//input to hidden
		if(aLayer == 0){
			offsetHiddenToInputEW(b, a, offset);
		}
		//last hidden to output
		else if(bLayer == this.getNumLayers() - 1){
			offsetOuputToHiddenEW(b, a, offset);
		}
		//hidden to hidden
		else{
			offsetHiddenToHiddenEW(a, aLayer, b, bLayer, offset);
		}
	}

	@Override
	public Solvable getSolvable() {
		return this.solveable;
	}
	public void setSolveable(Solvable solveable) {
		this.solveable = solveable;
	}
}
