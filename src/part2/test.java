package part2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class test {

	public static void majority_check(ArrayList<String> instances){
		HashMap<String, Integer> catego_occurence = new HashMap<>();
		
		// Getting the majority class of the dataset for baseline prediction
		for(String instance: instances) {
					
			if(!catego_occurence.keySet().contains(instance)) {
				catego_occurence.put(instance, 1);				
			}
			
			else {
				catego_occurence.put(instance, catego_occurence.get(instance)+1);
			}			
		}
		
		int most_occured = 0;
		String most_occured_catego = "";
		
		for(String key: catego_occurence.keySet()) {
			int val = catego_occurence.get(key);
			
			if(val > most_occured) {
				most_occured = val;
				most_occured_catego = key;
			}
		}
		
		System.out.println("Most occur catego: " + most_occured_catego);
		System.out.println("frequency: " + most_occured);
		
	}
	
	public static void main(String[] args) {
		ArrayList<String> test = new ArrayList<>();
		test.add("a");
		test.add("a");
		test.add("a");
		test.add("b");
		test.add("c");
		test.add("d");
		test.add("d");
		test.add("a");
		
		majority_check(test);
	}
}
