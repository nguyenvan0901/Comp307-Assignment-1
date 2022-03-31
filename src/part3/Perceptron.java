package part3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Perceptron {
	
	private File data_file;
	private int numAtts = 0;
	private ArrayList<Instance> instances = new ArrayList<>();
	
	private double[] best_weights;
	
	
	public Perceptron(String file_name){
		data_file  = new File(file_name);
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
			
		//--------------------------initualial set of weights as a list of 0.5s---------------
		best_weights = new double[numAtts+1];
	
		for(int i=0; i<best_weights.length; i++) {
			best_weights[i] = 0.5;
		}	
		//------------------------------------------------------------------------------------
		
		//--------------------------Spliting train set and test set 80/20---------------------
		ArrayList<Instance> train_set;
		ArrayList<Instance> test_set;
	
		//------------------------------------------------------------------------------------

		System.out.println("Perceptron training and testing the same data.\n ");
		this.adjustWeights(instances);
		this.predict(instances);
		System.out.print("Final set of weight: ");
		for(int i=0; i<best_weights.length; i++) {
			System.out.printf("%.2f,", best_weights[i]);
		}
		System.out.println();
		System.out.println("\n ");
		
		System.out.println("Perceptron training and testing on different data.\n ");
		int portion = instances.size() / 100 * 90;
		Collections.shuffle(instances);		
		train_set = new ArrayList<> (instances.subList(0, portion));
		test_set = new ArrayList<> (instances.subList(portion, instances.size()));	
		this.adjustWeights(train_set);
		this.predict(test_set);
		System.out.println("\n ");
//		
		System.out.println("Perceptron with 5-fold.\n ");
		this.performKfold();
				
	}
	
	public void adjustWeights(ArrayList<Instance> train_set) {
		
		// weight set that is used to do all the weight updating.
		// if a weight set is good, will update the best_weight set.
		double[] weights = new double[best_weights.length];
		
		for(int i=0; i<weights.length; i++) {
			weights[i] = 0.5;
		}
			
		int iteration = 0;
		boolean stop_training = false;
		
		int best_iteration = 0;
		double best_accuracy =0.0;
		int best_correct = 0;
		
		
		while(iteration < 500 && stop_training == false) {
			
			for(Instance ins: train_set) {
				 
				 double prediction = 1;
				 double weighted_sum = calculateWeightedSum(ins.getAllFeatures(), weights);
				 
				 // if weighted sum < 0 then predict 0 otherwise predict 1
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
			double correct = accuracy*train_set.size();
			

			if(accuracy == 1.0) {
				
				System.out.println("Achieve 100% accuracy after " + iteration + " epochs");
				// everything is correct with this weight, stop training.
				stop_training = true;
				
			}
			
			if(accuracy > best_accuracy) {
				best_correct = (int)correct;
				best_iteration = iteration;
				best_accuracy = accuracy;
				for(int i=0; i<weights.length; i++) {
					
					best_weights[i] = weights[i];
					
				}
				
			}
			
			iteration ++;
		}
		System.out.println(best_correct + " correct prediction out of "
						 + train_set.size() + " instances (" + (train_set.size() - best_correct)
						 + " incorrect predictions)");
		
		System.out.println("Accuracy on train set: " + best_accuracy 
						 + " after iterating through " + best_iteration
						 + " epoch");
		
			
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
	
	
	public double predict(ArrayList<Instance> test_set) {
		int correct_count = 0;
		
		double accuracy = getAccuracy(best_weights, test_set);
		
		System.out.println("Accuracy on test sett: " + accuracy);
		return accuracy;
	}
	
	public void performKfold() {
		
		double average = 0.0;
		int test_portion = instances.size() / 100 * 20;
			
		for(int i=0; i<5; i++) {
			System.out.print("FOLD " + (i+1) + " ");
			ArrayList<Instance> test_set = new ArrayList<>();
			ArrayList<Instance> train_set = new ArrayList<>();
			train_set.addAll(instances);
			
			int start_index = test_portion*i;
			int end_index = start_index + test_portion;

			for(int j=start_index; j<end_index; j++) {
				
				test_set.add(train_set.get(j));
	
			}
			
			for(int j=start_index; j<end_index; j++) {
				train_set.remove(start_index);
			}
			
			this.adjustWeights(train_set);
			double accuracy = this.predict(test_set);
			average = average + accuracy;
			
			System.out.println("\n");
		}
		
		average = average/5;
		
		System.out.println("\nAverage accuracy after doing 5-fold cross validate: " + average);
		
	}

	public static void main(String[] args) {
		if(args.length != 1) {
			new Perceptron("src/part3/ionosphere.data");
		}
		else {
			new Perceptron(args[0]);	
		}
	}
	
}