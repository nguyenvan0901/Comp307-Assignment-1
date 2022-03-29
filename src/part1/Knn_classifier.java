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
	public final static File test_file  = new File("src/part1/wine-test");
	public final static File train_file = new File("src/part1/wine-training");
	
	private ArrayList<ArrayList<Double>> att_values = new ArrayList<>();
	
	private ArrayList<Double> wine_class = new ArrayList<>();	
	private ArrayList<Double> ranges = new ArrayList<>();
	
	private int neighbours = 3;


	
	public Knn_classifier () {
		
		for(int i=0; i<13; i++) {
			
			att_values.add(new ArrayList<Double>());
			
		}
		
		try {
			Scanner sc_train = new Scanner(train_file);
			
			// Scanning the first line of the file.
	        sc_train.nextLine();
	        
	        
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
	        
	        
	        Scanner sc_test = new Scanner(test_file);
	        sc_test.nextLine();
	        ArrayList<Double> attributes_test = new ArrayList<>();
	        ArrayList<Double> predictions_test = new ArrayList<>();
	        ArrayList<Double> predictions_actual = new ArrayList<>();
	        
	        while(sc_test.hasNextDouble()) {
	        	
	        	for(int i=0; i<13; i++) {
	        		attributes_test.add(sc_test.nextDouble());
	        	}
	        	
	        	predictions_actual.add(sc_test.nextDouble());
	        	
	        	//-----------------------------------------------
	        	
	        	double prediction = classify(attributes_test);
	        	predictions_test.add(prediction);
	        	
	        	//System.out.println("prediction: " + prediction);
	        	attributes_test.clear();
	        	        	
	        }
	        
	        sc_test.close();
	        double correct_prediction = 0;
	        for(int i=0; i<predictions_actual.size(); i++) {

	        	if(predictions_actual.get(i) - predictions_test.get(i) == 0.0) {
	
	        		correct_prediction ++;
	        	}
	        	
	        }
	        
	        double ratio = correct_prediction / predictions_actual.size();
	        System.out.println("correct preddictions: " + correct_prediction);
	        System.out.println("total predictions: " + predictions_actual.size());
	        System.out.println("ratio: " + correct_prediction/predictions_actual.size());

        	       

		}catch(IOException e) {
			System.out.println("Something is wrong");
		}
				
	}
	
	public double classify(ArrayList<Double> attributes) {
		int total_observations = att_values.get(1).size();
						
		ArrayList<Double> predictions = new ArrayList<>();
		ArrayList<Double> distances = new ArrayList<>();
		HashMap<Double, ArrayList<Integer>> map = new HashMap<>();
		
		

		for(int i=0; i<total_observations; i++) {
			
			double distance = 0;
			
			for(int j=0; j<13; j++) {
				distance = distance + Math.pow(attributes.get(j) - att_values.get(j).get(i), 2) / Math.pow(ranges.get(j),2);
			}
			//System.out.println("distance: " + distance);
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
//		System.out.println("All values:");
//		for(double a: predictions) {
//			System.out.println(a);
//		}
		HashMap<Double, Integer> dictionary = new HashMap<>();
	
		for(double prediction: predictions) {
			if(!dictionary.keySet().contains(prediction)) {
				dictionary.put(prediction, 1);
			}
			else {
				dictionary.put(prediction, dictionary.get(prediction) + 1);
			}
		}
		
		Collection<Integer> frequencies = dictionary.values();
		//System.out.println("size: " + frequencies.size());
		int max = Collections.max(frequencies);
		double answer = 0;
		
		for(double key: dictionary.keySet()) {
			if(dictionary.get(key) == max) {
				answer = key;
			}
		}
		//System.out.println("answer: " + answer);
		return answer;
		
	}
	
	public static void main(String[] args) {
		new Knn_classifier();
	}
}



/*
public double classify(ArrayList<Double> attributes) {
	int total_observations = att_values.get(1).size();
			
	PriorityQueue<Double> distances = new PriorityQueue<>();
	HashMap<Double, ArrayList<Integer>> map = new HashMap<>();
	
	for(int i=0; i<total_observations; i++) {
		
		double distance = 0;
		
		for(int j=0; j<13; j++) {
			distance = distance + Math.pow(attributes.get(j), 2) / ranges.get(j);
		}
		
		distance = -Math.sqrt(distance);
		
		if(!map.keySet().contains(distance)) {
			
			map.put(distance, new ArrayList<Integer>(i));
			
		}
		
		else {
			
			map.get(distance).add(i);
			
		}
		
		// if the distances list is empty, add the fist 3 distances in it.
		if(distances.size() < neighbours) {
						
			distances.add(distance);
			
		}
		
		else {
			// if the list already has enough number of neighbours, check if the 
			// new distance is smaller than the last element in the queue.
			
			if(distance > distances.peek()) {
				
				distances.poll();
				distances.add(distance);
		
			}
		}
		
	}
*/
