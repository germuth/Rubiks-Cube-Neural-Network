package ca.germuth.neural_network;

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

	//order them based off their f value, smallest first
	@Override
	public int compareTo(SearchNode o) {
		return new Double(this.getFValue()).compareTo(o.getFValue());
	}	
}
