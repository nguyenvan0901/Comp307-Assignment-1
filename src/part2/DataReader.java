package part2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class DataReader {
	private int numCategories;
    private int numAtts;
    private Set<String> categoryNames;
    private List<String> attNames;
    private List<Instance> allInstances;

    public void readDataFile(String fname) {
        /* format of names file:
         * names of attributes (the first one should be the class/category)
         * category followed by true's and false's for each instance
         */
       // System.out.println("Reading data from file " + fname);
        try {
            Scanner din = new Scanner(new File(fname));

            attNames = new ArrayList<>();
            Scanner s = new Scanner(din.nextLine());
            // Skip the "Class" attribute.
            s.next();
            while (s.hasNext()) {
                attNames.add(s.next());
            }
            numAtts = attNames.size();
            //System.out.println(numAtts + " attributes");

            allInstances = readInstances(din);
            din.close();

            categoryNames = new HashSet<>();
            for (Instance i : allInstances) {
                categoryNames.add(i.getCategory());
            }
            numCategories = categoryNames.size();
            //System.out.println(numCategories + " categories");

        } catch (IOException e) {
            throw new RuntimeException("Data File caused IO exception");
        }
    }

    private List<Instance> readInstances(Scanner din) {
        /* instance = classname and space separated attribute values */
        List<Instance> instances = new ArrayList<>();
        while (din.hasNext()) {
            Scanner line = new Scanner(din.nextLine());
            instances.add(new Instance(line.next(), line));
        }
        //System.out.println("Read " + instances.size() + " instances");
        return instances;
    }
    
    public int getNumCategories() {
    	return numCategories;
    }
    
    public int getNumAtts() {
    	return numAtts;
    }
    
    public Set<String> getCategoryNames(){
    	return categoryNames;
    }
    
    public List<String> getAttNames(){
    	return attNames;
    }
    
    public List<Instance> getAllInstances(){
    	return allInstances;
    }
}
