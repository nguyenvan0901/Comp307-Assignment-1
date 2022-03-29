package part3;

import java.util.ArrayList;

public class Instance {
	
	int category = -1;
	
	ArrayList<Double> vals = new ArrayList<>();
	public Instance() {
		
		// first feature will be 1 for the bias
		vals.add(1.0);
		
	}
	
	public void setCategory(String catego) {	
		
		if(catego.equals("g")) {
			
			category = 1;
			
		}
		else if(catego.equals("b")) {
			
			category = 0;
			
		}	
		
	}
	
	public void addToVals(double val) {
		
		vals.add(val);
		
	}
	
	public int getCategory() {
		
		return category;
		
	}
	
	public ArrayList<Double> getAllFeatures(){
		
		return vals;
		
	}
	
	public double getAttAt(int index) {
		
		return vals.get(index);
		
	}
	
	
	
	
}
