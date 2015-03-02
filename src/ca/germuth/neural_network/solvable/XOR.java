package ca.germuth.neural_network.solvable;

public class XOR implements Solvable{
	@Override
	public double[] mapTrainingInput(String trainInput) {
		String[] inputs = trainInput.split(" ");
		double[] in = new double[inputs.length];
		for(int i = 0; i < inputs.length; i++){
			in[i] = Double.parseDouble(inputs[i]);
		}
		return in;
	}

	@Override
	public double[] mapTrainingOutput(String trainOutput) {
		double[] out = new double[1];
		out[0] = Double.parseDouble(trainOutput);
		return out;
	}
}
