package ca.germuth.neural_network;
/**
 * SearchNode.java
 * 
 * SearchNode is the object the search algorithms work with. It contains 
 * 		the searchable object
 * 		the parent, to remember the pathing information
 * 		g and h Value from AStarSearch 
 * 
 * January 30th, 2015 
 * @author Aaron Germuth
 */
public class SearchNode implements Comparable<SearchNode>{
	private Searchable searchable;
	private SearchNode parent;
	private double gValue;
	private double hValue;
	
	public SearchNode(Searchable sable, double hValue, SearchNode parent){
		this.searchable = sable;
		this.hValue = hValue;
		this.gValue = sable.calcHeuristic();
		this.parent = parent;
	}

	//order them based off their F value, smallest first
	@Override
	public int compareTo(SearchNode o) {
		return new Double(this.getFValue()).compareTo(o.getFValue());
	}	
	
	//getters
	public Searchable getSearchable() {
		return searchable;
	}
	public double getGValue() {
		return gValue;
	}
	public double getHValue() {
		return hValue;
	}
	public double getFValue() {
		return gValue + hValue;
	}
	public SearchNode getParent() {
		return parent;
	}
}

