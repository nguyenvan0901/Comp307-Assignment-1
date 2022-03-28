package part2;

public class Node {
	private String best_att = "";
	private Node left;
	private Node right;
	private boolean isLeaf = false;
	private String node_class;
	private double prob;
	
	// constructor for normal node (with a best attribute, left and right node)
	public Node(String best_att, Node left, Node right) {
		this.best_att = best_att;
		this.left = left;
		this.right = right;
	}
	
	// constructor for a leaf node (with category, probablity)
	public Node(String node_class, double prob) {
		
		isLeaf = true;
		//System.out.println("node category: " + node_class);
		this.node_class = node_class;
		this.prob = prob;
		
	}
	
	public boolean isLeafNode() {
		return isLeaf;
	}
	
	public Node getLeft() {
		return left;
	}
	
	public Node getRight() {
		return right;
	}
	
	public String getNodeClass() {
		return node_class;
	}
	
	public String getBestAtt() {
		return best_att;
	}
	
	public double getProb() {
		return prob;
	}
	
}
