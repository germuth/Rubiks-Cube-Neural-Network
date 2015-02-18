package ca.germuth.neural_network;

public class StochasticBackPropagation {
	private static final int TRAINING_EPOCHS = 1000;
	private static final int TRAINING_ITERATIONS = 10;
	private static final double LEARNING_RATE = 0.2;
	private static final double ERROR_THRESHOLD = 0.1;
	
	public void run(Trainable trainable){
		
		for(int epoch = 0; epoch < TRAINING_EPOCHS; epoch++){
			//randomly initialize nn
			trainable.randomizeAllEdgeWeights();
			for(int trainingIter = 0; trainingIter < TRAINING_ITERATIONS; trainingIter++){
				//randomly chose training tuple
				
				//find delta k
				//find delta Wkj
				
				//find delta Wkbias
				
				//find delta j
				//find delta Wji
				
				//find delta Wjbias
				
				//apply all deltaW
			}
			
			//calculate network error across testing date
			int testingError = 0;
			//for each testing tuple
			//	testingError += 0.5* || t-z ||^2
			
			if(testingError <= ERROR_THRESHOLD){
				return;
			}
		}
	}
}
