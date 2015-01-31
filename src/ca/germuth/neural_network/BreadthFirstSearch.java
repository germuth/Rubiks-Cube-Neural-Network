package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
/**
 * BreadthFirstSearch
 * 
 * Implements standard BFS. One minor change is
 * Rather than check the whether every child is in the openlist
 * we can add it anyway and then check the closed list at the beginning of each loop.
 * Checking closed list is constant time, where as checking a openlist
 * could be made O(log(n)) at best
 * 
 * @author Aaron Germuth
 */
public class BreadthFirstSearch {
	public static SearchNode runSearch(Searchable start, Searchable goal){
		LinkedList<SearchNode> openList = new LinkedList<SearchNode>();
		HashMap<String, SearchNode> closedList = new HashMap<String, SearchNode>();
		//add start state
		SearchNode startingNode = new SearchNode(start, 0, null);
		openList.add(startingNode);
		String goalKey = goal.getKey();
		
		while(!openList.isEmpty()){
			SearchNode current = openList.pop();
			String currKey = current.getSearchable().getKey();
			
			//Skip this state if we already visited it
			if(closedList.containsKey(currKey)){
				continue;
			}
			closedList.put(currKey, current);
			
			if(currKey.equals(goalKey)){
				return current;
			}
			
			ArrayList<Searchable> children = current.getSearchable().getChildren();
			//search all child states
			for(Searchable child: children){
				String childKey = child.getKey();
				
				//except the ones we've already explored
				if(!closedList.containsKey(childKey)){
					SearchNode childNode = new SearchNode(child, current.getHValue() + 1, current);
					openList.add(childNode);
				}
			}
		}
		
		//goal state not found!
		return null;
	}
}
