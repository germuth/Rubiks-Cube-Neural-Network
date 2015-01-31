package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * AStarSearch
 * 
 * An Implementation of A* Search on any searchable object.
 * 
 * @author Aaron Germuth
 */
public class AStarSearch {
	public static SearchNode runSearch(Searchable start, Searchable goal){
		//Openlist is constantly sorted by natural ordering (searchnode.compareTo)
		PriorityQueue<SearchNode> openList = new PriorityQueue<SearchNode>();
		HashMap<String, SearchNode> closedList = new HashMap<String, SearchNode>();
		//add start state
		SearchNode startingNode = new SearchNode(start, 0, null);
		openList.add(startingNode);
		String goalKey = goal.getKey();
		
		while(!openList.isEmpty()){
			SearchNode current = openList.poll();
			String currKey = current.getSearchable().getKey();
			closedList.put(currKey, current);
			
			if(currKey.equals(goalKey)){
				return current;
			}
			
			ArrayList<Searchable> children = current.getSearchable().getChildren();
			//search all children
			for(Searchable child: children){
				String childKey = child.getKey();
				SearchNode childNode = new SearchNode(child, current.getHValue() + 1, current);
				
				//if we already visited that node
				if(closedList.containsKey(childKey)){
					double childG = childNode.getGValue();
					double closedListG = closedList.get(childKey).getGValue();
					//check our previous distance to new distance
					if(childG < closedListG){
						//found shorter path to some node already found
						closedList.remove(childKey);
						openList.add(childNode);
					}
				}else{
					//add unvisited node
					openList.add(childNode);
				}
			}
		}
		
		//goal state not found!
		return null;
	}
}
