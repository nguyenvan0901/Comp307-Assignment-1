package part1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Knn_classifier {
//	private File test_file  = new File("src/part1/wine-test");
//	private File train_file = new File("src/part1/wine-training");
	
	private File test_file;
	private File train_file;
	
	// each arraylist is a column of the dataset.
	private ArrayList<ArrayList<Double>> att_values = new ArrayList<>();
	
	private ArrayList<Double> wine_class = new ArrayList<>();	
	private ArrayList<Double> ranges = new ArrayList<>();
	
	private ArrayList<Integer> neighbours = new ArrayList<>();;

	private int number_of_attributes = 0;
	
	public Knn_classifier (String train_file_name, String test_file_name) {
		
		train_file = new File(train_file_name);
		test_file = new File(test_file_name);
		
		neighbours.add(1);
		neighbours.add(3);
		neighbours.add(5);
		neighbours.add(7);
			
//        ArrayList<Double> prediction_1nn = new ArrayList<>();
		try {
			Scanner sc_train = new Scanner(train_file);
			
			// scanning the first line to see how many attribute there are in the dataset.
			Scanner s = new Scanner(sc_train.nextLine());
            
            while (s.hasNext()) {
                s.next();
                number_of_attributes ++;           
            }
            
            number_of_attributes = number_of_attributes - 1;
	        
    		for(int i=0; i<number_of_attributes; i++) {
    			
    			att_values.add(new ArrayList<Double>());
    			
    		}
    		
	        // Start scanning through all the values of each wine object.
	        while(sc_train.hasNextDouble()) {
	        	

	        	for(int i=0; i<13; i++) {
	        		att_values.get(i).add(sc_train.nextDouble());
	        	}
	        	
	        	wine_class.add(sc_train.nextDouble());
	        	
	        }
	        
	        sc_train.close();
	        
	        for(int i=0; i<13; i++) {
	        	 ranges.add(Collections.max(att_values.get(i)) - Collections.min(att_values.get(i)));
	        }
	        
	        //------------------------------------------------------------------------------------------------------------

	        for(int neighbour: neighbours) {
	        	
	        	Scanner sc_test = new Scanner(test_file);
		        sc_test.nextLine();
		        
		        // attributes_test contains the features of 1 instance in the test set.
		        ArrayList<Double> attributes_test = new ArrayList<>();
		        ArrayList<Double> predictions_test = new ArrayList<>();
		        ArrayList<Double> predictions_actual = new ArrayList<>();
	        	while(sc_test.hasNextDouble()) {
		        	
		        	for(int i=0; i<13; i++) {
		        		attributes_test.add(sc_test.nextDouble());
		        	}
		        	
		        	predictions_actual.add(sc_test.nextDouble());
		        	
		        	//-----------------------------------------------
		        	
		        	double prediction = classify(attributes_test, neighbour);
		        	predictions_test.add(prediction);
	        	
		        	// clear it so that it can be used for the next instacne 
		        	attributes_test.clear();
		        	        	
		        }
	        	
//	        	if(neighbour == 1) {
//	        		prediction_1nn.addAll(predictions_test);
//	        	}
		        
		        sc_test.close();
		        double correct_prediction = 0;
		        for(int i=0; i<predictions_actual.size(); i++) {

		        	if(predictions_actual.get(i) - predictions_test.get(i) == 0.0) {
		
		        		correct_prediction ++;
		        	}
		        	
		        }
		        
		        System.out.println("Accuracy for " + neighbour + "-NN: " + correct_prediction/predictions_actual.size());
		        System.out.println("correct preddictions: " + correct_prediction);
		        System.out.println("total predictions: " + predictions_actual.size() + "\n");
	        }
	        	       
		}catch(IOException e) {
			System.out.println("Something is wrong");
		}
		
//		String predictions = "";
		
//		for(double i: prediction_1nn) {
//			int a = (int)i;
//			predictions = predictions + a + ",";
//		}
		
			
//		System.out.println(predictions);
	}
	
	public double classify(ArrayList<Double> attributes, int neighbours) {
		// total obsevation in the training data.
		int total_observations = att_values.get(1).size();
						
		ArrayList<Double> predictions = new ArrayList<>();
		ArrayList<Double> distances = new ArrayList<>();
		
		// incase a 2 or more observations have the same distance
		HashMap<Double, ArrayList<Integer>> map = new HashMap<>();
		
		

		for(int i=0; i<total_observations; i++) {
			
			double distance = 0;
			
			for(int j=0; j<13; j++) {
				distance = distance + Math.pow(attributes.get(j) - att_values.get(j).get(i), 2) / Math.pow(ranges.get(j),2);
			}

			distances.add(distance);
			
			if(!map.keySet().contains(distance)) {
				ArrayList<Integer> indexes = new ArrayList<>();
				indexes.add(i);
				map.put(distance, indexes);
			}
			else {
				ArrayList<Integer> indexes = map.get(distance);
				indexes.add(i);
				map.put(distance, indexes);
			}
					
		}
		
		Collections.sort(distances);
		
		for(int i=0; i<neighbours; i++) {
			
			// once have gathered enough nearest neighbours
			if(predictions.size() == neighbours) {
				break;
			}
			
			double distance = distances.get(i);
			ArrayList<Integer> indexes = map.get(distance);
			
			for(int index: indexes) {
				if(predictions.size() != neighbours) {
					predictions.add(wine_class.get(index));
				}
			}

		}
	
		return check_most_frequent(predictions);
	}
	
	
	
	
	public double check_most_frequent(ArrayList<Double> predictions) {

		HashMap<Double, Integer> dictionary = new HashMap<>();
	
		// Inputting into the map the prediction and the frequency of that prediction happening.
		for(double prediction: predictions) {
			if(!dictionary.keySet().contains(prediction)) {
				dictionary.put(prediction, 1);
			}
			else {
				dictionary.put(prediction, dictionary.get(prediction) + 1);
			}
		}
		
		// Getting all the frequencies to find the prediction that has the highest frequency.
		Collection<Integer> frequencies = dictionary.values();

		int max = Collections.max(frequencies);
		double answer = 0;
		
		for(double key: dictionary.keySet()) {
			if(dictionary.get(key) == max) {
				answer = key;
			}
		}

		return answer;
		
	}
	
	public static void main(String[] args) {
		if(args.length != 2) {
			new Knn_classifier("src/part1/wine-training", "src/part1/wine-test");
		}
		else {
			String train_file_name = args[0];
			String test_file_name = args[1];
		
			new Knn_classifier(train_file_name, test_file_name);
		}
	}
}
