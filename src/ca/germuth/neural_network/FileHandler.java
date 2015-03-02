package ca.germuth.neural_network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import ca.germuth.neural_network.solvable.Solvable;
import ca.germuth.neural_network.trainable.NeuralNetwork;
import ca.germuth.neural_network.trainable.TrainingData;
/**
 * FileHandler
 * 
 * Handles writing and reading from files
 * 
 * @author Aaron
 */
public class FileHandler {
	private static final String TRAINING_DATA_FILE_NAME = "data.csv";
	private static final String SAVED_NEURAL_NETWORK_FILE_NAME = "NeuralNetwork.csv";
	
	public static void writeTrainingData(String type, ArrayList<TrainingData> tobeWritten){
		File f = null;
		PrintWriter pw = null;
		try {
			f = new File(type + TRAINING_DATA_FILE_NAME);
			f.createNewFile();
			pw = new PrintWriter(f);
			for(TrainingData d: tobeWritten){
				pw.println(d.getInput() + "," + d.getOutput());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.flush();
		pw.close();
	}
	
	public static ArrayList<TrainingData> readTrainingData(String type){
		ArrayList<TrainingData> data = new ArrayList<TrainingData>();
		File f = new File(type + TRAINING_DATA_FILE_NAME);
		try {
			Scanner s = new Scanner(f);
			while(s.hasNextLine()){
				String line = s.nextLine();
				String[] split = line.split(",");
				if(split.length == 2){
					data.add(new TrainingData(split[0], split[1]));
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static void writeNeuralNetwork(NeuralNetwork nn, String type){
		File f = null;
		PrintWriter pw = null;
		try {
			f = new File(type + SAVED_NEURAL_NETWORK_FILE_NAME);
			f.createNewFile();
			pw = new PrintWriter(f);
			
			ArrayList<String> lines = nn.toFile();
			for(String s: lines){
				pw.println(s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.flush();
		pw.close();
	}
	
	public static NeuralNetwork readNeuralNetwork(String type){
		NeuralNetwork nn = null;
		Solvable solv = null;
		File f = new File(type + SAVED_NEURAL_NETWORK_FILE_NAME);
		try {
			Scanner s = new Scanner(f);
			//skip "neural network"
			s.nextLine(); 
			
			//get layer sizes
			String[] sizes = s.nextLine().split(",");
			ArrayList<Integer> size = new ArrayList<Integer>();
			for(String str: sizes){
				size.add(Integer.parseInt(str));
			}
			
			//get edge weights
			String[] edge= s.nextLine().split(",");
			ArrayList<Double> weights = new ArrayList<Double>();
			for(String str: edge){
				weights.add( Double.parseDouble(str) );
			}
			nn = new NeuralNetwork(solv, size, weights);
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return nn;
	}
}
