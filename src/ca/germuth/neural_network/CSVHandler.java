package ca.germuth.neural_network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVHandler {
	private static final String FILE_NAME = "data.csv";
	
	public static void write(ArrayList<TrainingData> tobeWritten){
		File f = null;
		PrintWriter pw = null;
		try {
			f = new File(FILE_NAME);
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
	
	public static ArrayList<TrainingData> read(String fileName){
		ArrayList<TrainingData> data = new ArrayList<TrainingData>();
		File f = new File(FILE_NAME);
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
}
