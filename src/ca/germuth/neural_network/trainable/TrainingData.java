package ca.germuth.neural_network.trainable;

public class TrainingData {
	private String input;
	private String output;
	
	public TrainingData(String in, String out){
		input = in;
		output = out;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
}
