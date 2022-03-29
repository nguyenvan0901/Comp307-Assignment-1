package part3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Perceptron {
	
	public final File data_file  = new File("src/part3/ionosphere.data");
	private int numAtts = 0;
	private ArrayList<Instance> instances = new ArrayList<>();
	
	private double[] best_weights;
	
	
	public Perceptron(){
		
		try {
			
			// Loading the instances from the dataset.
			Scanner sc = new Scanner(data_file);
			
			Scanner s = new Scanner(sc.nextLine());
 
            while (s.hasNext()) {
            	
            	s.next();          	
            	numAtts++;
            	
            }
            
            // excluding the last feature as it is the variable's class
            numAtts = numAtts - 1;
            //System.out.println(numAtts);
 			
			while(sc.hasNextDouble()) {
				
				Instance instance = new Instance();
				
				for(int i=0; i<numAtts; i++) {
					double val = sc.nextDouble();
					instance.addToVals(val);
				}
				
				String catego = sc.next();
				instance.setCategory(catego);
				
				instances.add(instance);
				
			}
	
			sc.close();
			
		}catch(IOException e) {
			
		}
			
		// weights for all features and bias
		best_weights = new double[numAtts+1];
		
		// initualising the weights as a list of 0.5s
		for(int i=0; i<best_weights.length; i++) {
			best_weights[i] = 0.5;
		}	
		
		ArrayList<Instance> train_set;
		ArrayList<Instance> test_set;
		System.out.println(instances.size());
		int portion = instances.size() / 100 * 80;
		
		train_set = new ArrayList<> (instances.subList(0, 200));
		test_set = new ArrayList<> (instances.subList(portion, instances.size()));

		System.out.println("Perceptron training and testing the same data.\n ");
		this.adjustWeights(instances);
		this.predict(instances);
		System.out.println("\n ");
		
		System.out.println("Perceptron training and testing on different data.\n ");
		this.adjustWeights(train_set);
		this.predict(test_set);
		System.out.println("\n ");
				
	}
	
	public void adjustWeights(ArrayList<Instance> train_set) {
		
		// weight set that is used to do all the weight updating.
		// if a weight set is good, will update the best_weight set.
		double[] weights = new double[best_weights.length];
		
		for(int i=0; i<weights.length; i++) {
			weights[i] = best_weights[i];
		}
			
		int iteration = 0;
		boolean stop_training = false;
		
		int best_iteration = 0;
		double best_accuracy =0.0;
		
		System.out.println("Start training here.\n");
		
		while(iteration < 5000 && stop_training == false) {
			
			for(Instance ins: train_set) {
				 
				 double prediction = 1;
				 double weighted_sum = calculateWeightedSum(ins.getAllFeatures(), weights);
				 
				 if(weighted_sum < 0) {
					 prediction = 0;
				 }
				
				 // -ve example and wrong, weights on features too high need to reduce the weights
				 if(prediction == 1 && ins.getCategory() == 0) {
					 
					 // wi = wi + 1*(0-1) * xi (weight vector = weight vector - feature vector)
					 for(int i=0; i<weights.length; i++) {
						 weights[i] = weights[i] - ins.getAttAt(i);
					 }
					 
				 }
				 
				 // +ve example and wrong, weights on active features are too low.
				 else if(prediction == 0 && ins.getCategory() == 1) {
					 
					 // wi = wi + 1*(1-0) * xi (weight vector = weight vector + feature vector)
					 for(int i=0; i<weights.length; i++) {
						 weights[i] = weights[i] + ins.getAttAt(i);
					 }
				 }
				 
			}
			
			double accuracy = getAccuracy(weights, train_set);

			if(accuracy == 1.0) {
				System.out.println("Achieve 100% accuracy after " + iteration + " epochs");
				// everything is correct with this weight, stop training.
				stop_training = true;
				
			}
			
			if(accuracy > best_accuracy) {
	
				best_accuracy = accuracy;
				for(int i=0; i<weights.length; i++) {
					
					best_weights[i] = weights[i];
					
				}
				
				System.out.println("Current best accuracy: " + best_accuracy);
				System.out.println("After " + (iteration+1) + " epochs \n");
				
			}
			
			iteration ++;
		}
		
		System.out.println("Finish training");
		
	}
	
	public double getAccuracy(double[] weights, ArrayList<Instance> instances) {
		int correct_count = 0;
		
		for(Instance ins: instances) {
			int instance_category = ins.getCategory();
			int instance_prediction  = 0;
			
			double weighted_sum = calculateWeightedSum(ins.getAllFeatures(), weights);
			
			if(weighted_sum > 0) {
				instance_prediction = 1;
			}
			
			if(instance_category == instance_prediction) {
				correct_count ++;			
			}
	
		}
		
		return (double) correct_count / (double) instances.size();
	}
	
	
	public double calculateWeightedSum(ArrayList<Double> features, double[] weights) {
		
		double weighted_sum = 0.0;
		
		for(int i=0; i<features.size(); i++) {
			weighted_sum = weighted_sum + features.get(i)*weights[i];
		}
		
		return weighted_sum;
	}
	
	
	public void predict(ArrayList<Instance> test_set) {
		int correct_count = 0;
		
		double accuracy = getAccuracy(best_weights, test_set);
		
		System.out.println("Accuracy on test sett: " + accuracy);
		
	}

	public static void main(String[] args) {

		new Perceptron();	
		
	}
}
