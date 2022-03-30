package part2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import part2.DataReader.Instance;

public class Decision_Tree_classifier {
	
	private ArrayList<Instance> instances;
	private ArrayList<Instance> test_instances;
	private ArrayList<String> atts;	
	private ArrayList<String> categories_names;
	private Node root;
	
	private String majority_category;
	private double prob_most_frequent;
	
	public Decision_Tree_classifier(String train_file_name, String test_file_name) {
		
		this.initualise();
		
		DataReader dr = new DataReader();
		dr.readDataFile("src/part2/hepatitis-training");
		//dr.readDataFile("src/part2/golf-training");
		
		instances = (ArrayList<Instance>) dr.allInstances;
		atts = (ArrayList<String>) dr.attNames;
				
		HashSet<String> a =  (HashSet<String>) dr.categoryNames;
		categories_names = new ArrayList<String>(a);
		ArrayList<String> copy = new ArrayList<>();
		copy.addAll(atts);
		
		Map.Entry<String, Integer> entry = majority_check(instances);
		
		majority_category = entry.getKey();
			
		prob_most_frequent = (double)entry.getValue() / (double)instances.size();
		
		root = buildTree(instances, copy);
	
		//printTree("", root);
		
		//System.out.println("\n\n\n");
		
//		System.out.println("majority category: '" + majority_category + "' with probability: " + prob_most_frequent);
//		System.out.println("count: " + entry.getValue());
		// -----------------------------------------------------------------------------------------------------------
		
	
		DataReader dr2 = new DataReader();
		dr2.readDataFile("src/part2/hepatitis-test");
		//dr2.readDataFile("src/part2/golf-test");
		
		
		test_instances = (ArrayList<Instance>) dr2.allInstances;
		ArrayList<String> actual_class = new ArrayList<>();
		ArrayList<String> predict_class = new ArrayList<>();
		
		for(Instance i: test_instances) {		
			actual_class.add(i.getCategory());
			predict_class.add(predict(i));
		}
		
		int correct = 0;
		for(int i=0; i<actual_class.size(); i++) {
			if(actual_class.get(i).equals(predict_class.get(i))) {
				correct ++;
			}
		}
			
		double accuracy = (double)correct / (double)actual_class.size(); 
		System.out.println("\n ACCURACY: " + accuracy + " with " + correct + " correct predictions");
		
		this.performKFold();
	}
	
	public void initualise() {

		DataReader dr = new DataReader();
		dr.readDataFile("src/part2/hepatitis-training");
		//dr.readDataFile("src/part2/golf-training");
		
		instances = (ArrayList<Instance>) dr.allInstances;
		atts = (ArrayList<String>) dr.attNames;
				
		HashSet<String> a =  (HashSet<String>) dr.categoryNames;
		categories_names = new ArrayList<String>(a);
		ArrayList<String> copy = new ArrayList<>();
		copy.addAll(atts);
		
		Map.Entry<String, Integer> entry = majority_check(instances);
		
		majority_category = entry.getKey();
			
		prob_most_frequent = (double)entry.getValue() / (double)instances.size();
		
		root = buildTree(instances, copy);
	
		//printTree("", root);
		
		//System.out.println("\n\n\n");
		
//		System.out.println("majority category: '" + majority_category + "' with probability: " + prob_most_frequent);
//		System.out.println("count: " + entry.getValue());
		// -----------------------------------------------------------------------------------------------------------
		
	
		DataReader dr2 = new DataReader();
		dr2.readDataFile("src/part2/hepatitis-test");
		//dr2.readDataFile("src/part2/golf-test");
		
		
		test_instances = (ArrayList<Instance>) dr2.allInstances;
	}
	
	public Node buildTree(ArrayList<Instance> instances, ArrayList<String> attributes) {

		String bestAtt = "";
		HashSet<String> instance_classes = new HashSet<>();
		
		Node left; 
		Node right;
		
		for(Instance instance: instances) {
			instance_classes.add(instance.getCategory());			
		}
		
		// if instance is empty return a leaf node with baseline predictor
		if(instances.isEmpty()) {	
			//System.out.println("instance is empty, " + majority_category + " with prob: " + prob_most_frequent);
			return new Node(majority_category, prob_most_frequent);		 
		}
		
		// if instances are pure return a leaf node with 1 category and prob = 1
		else if(instance_classes.size() == 1) {	
			//System.out.println("instance has all 1 type, " + instances.get(0).getCategory());
			return new Node(instances.get(0).getCategory(), 1);		
		}
		
		// if attributes is empty return a leaf node with category = majority of instance left and prob
		else if(attributes.isEmpty()) {	
			//System.out.println("no more attributes");
			Map.Entry<String, Integer> entry = majority_check(instances);
			String majority_category_2 = entry.getKey();
			double prob_majority = (double)entry.getValue() / (double)instances.size();
			
			return new Node(majority_category_2, prob_majority);			
		}
		
		// find the best attribute
		else {
			double smallest_impurity = 100;
			
			ArrayList<Instance> bestIntsTrue = new ArrayList<>();
			ArrayList<Instance> bestIntsFalse = new ArrayList<>();
			
			// iterating through all the attributes
			for(String attribute: attributes) {
				
				// seperating instances based on true or false

				ArrayList<Instance> true_set = new ArrayList<>();
				ArrayList<Instance> false_set = new ArrayList<>();
				
				for(Instance ins: instances) {
					if(ins.getAtt(atts.indexOf(attribute)) == true) {
						true_set.add(ins);
					}
					else {
						false_set.add(ins);
					}
				}
				
				// -------------------------------calculating the impurity value-------------------------------
//				if(true_set.isEmpty() || false_set.isEmpty()) {
//					bestAtt = attribute;
//					break;
//				}
				
				double true_impurity = calculateImpurity(true_set);	
				true_impurity = true_impurity * (double)true_set.size() / (double)instances.size();
					
				double false_impurity = calculateImpurity(false_set);
				false_impurity = false_impurity * (double)false_set.size()/(double)instances.size();
	
				double average_impurity = true_impurity + false_impurity;

				// --------------------------------------------------------------------------------------------
				
				if(average_impurity < smallest_impurity) {

					smallest_impurity = average_impurity;
					bestAtt = attribute;
					
					bestIntsTrue.clear();
					bestIntsTrue.addAll(true_set);
					
					bestIntsFalse.clear();
					bestIntsFalse.addAll(false_set);
				}
				
			}
			
			// build subtree using the remaining attributes

			attributes.remove(bestAtt);
			
			ArrayList<String> copy = new ArrayList<>();
			copy.addAll(attributes);
			
			ArrayList<String> copy2 = new ArrayList<>();
			copy2.addAll(attributes);

			left  = buildTree(bestIntsTrue, copy);
			right = buildTree(bestIntsFalse, copy2);
		}

		return new Node(bestAtt, left, right);
	}
	
	
	public String predict(Instance instance) {
		// while we haven't traversed down to a leaf node yet.
		Node current = root;
		
		while(true) {
			
			if(current.isLeafNode()) {
			
				return current.getNodeClass();

			}
			
			String current_node_best_att = current.getBestAtt();
				
			int att_index = atts.indexOf(current_node_best_att);
				
			boolean val = instance.getAtt(att_index);
				
			if(val) {
				current = current.getLeft();				
			}
				
			else {
				current = current.getRight();	
			}
				
		}
	}
			
	public void printTree(String indent, Node node) {
		if(!node.isLeafNode()) {
			System.out.println(indent + node.getBestAtt() + " = True:");
			printTree(indent+"   ", node.getLeft());
			System.out.println(indent + node.getBestAtt() + " = False:");
			printTree(indent+"   ", node.getRight());
		}
		else {
			System.out.println(indent + "Class " + node.getNodeClass() + ", prob = " + node.getProb());
		}
	}
	
	
	public Map.Entry<String, Integer> majority_check(ArrayList<Instance> instances){
		HashMap<String, Integer> catego_occurence = new HashMap<>();
		
		// Getting the majority class of the dataset for baseline prediction
		for(Instance instance: instances) {
			String catego = instance.getCategory();
			
			if(!catego_occurence.keySet().contains(catego)) {
				catego_occurence.put(catego, 1);				
			}
			
			else {
				catego_occurence.put(catego, catego_occurence.get(catego)+1);
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
			
		for(Map.Entry<String, Integer> entry: catego_occurence.entrySet()) {
			if(entry.getKey().equals(most_occured_catego)) {
				return entry;
			}
		}

		return null;
	}
	
	
	public void performKFold() {
		for(int i=0; i<10; i++) {
			String train = "src/part2/hepatitis-train-run-" + i;
			String test  = "src/part2/hepatitis-test-run-" + i;
		
		}
	}
	
	
	public double calculateImpurity(ArrayList<Instance> instances) {
		
		if(instances.isEmpty()) {		
			return 0.0;			
		}
		
		double impurity = 0.0;
		int cate1_count = 0;
		int cate2_count = 0;
		
		for(Instance instance: instances) {
			if(instance.getCategory().equals(categories_names.get(0))) {
				cate1_count ++;
			}
			else if(instance.getCategory().equals(categories_names.get(1))){
				cate2_count ++;
			}

		}
		
		impurity = (double)(cate1_count*cate2_count)/(Math.pow((double)instances.size(),2));
		
		return impurity;
		
		
	}
	
	
	public static void main(String[] args) {
		if(args.length != 2) {
			new Decision_Tree_classifier("src/part2/hepatitis-training", "src/part2/hepatitis-test");
		}
		else {
			String train_file_name = args[0];
			String test_file_name  = args[1];
		
			new Decision_Tree_classifier(train_file_name, test_file_name);
		}
	}
}
