package ca.germuth.neural_network;

import java.util.ArrayList;

public interface Searchable{
	public ArrayList<Searchable> getChildren();
	public String getKey();
	public String getMoveTaken();
	public double calcHeuristic();
//	public boolean equals();
	//TODO not sure if i want equals and getKey...
}
