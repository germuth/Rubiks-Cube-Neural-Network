package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
//f = g + h
//g = distance so far
//h = heuristic guess
//http://theory.stanford.edu/~amitp/GameProgramming/
public class AStarSearch {
	public static SearchNode runSearch(Searchable start, Searchable goal){
		PriorityQueue<SearchNode> openList = new PriorityQueue<SearchNode>();
		//different from BFS
		//also, perhaps add integer to it? hrmm
		HashMap<String, SearchNode> closedList = new HashMap<String, SearchNode>();
		SearchNode startingNode = new SearchNode(start, 0, null);
		openList.add(startingNode);
		String goalKey = goal.getKey();
		
		while(!openList.isEmpty()){
			SearchNode current = openList.poll();
			String currKey = current.getSearchable().getKey();
			
			//have we already been to this state
//			if(closedList.containsKey(currKey)){
//				continue;
//			}
			closedList.put(currKey, current);
			
			if(currKey.equals(goalKey)){
				return current;
			}
			
			ArrayList<Searchable> children = current.getSearchable().getChildren();
			for(Searchable child: children){
				String childKey = child.getKey();
				SearchNode childNode = new SearchNode(child, current.getHValue() + 1, current);
				
				if(closedList.containsKey(childKey)){
					double childG = childNode.getGValue();
					double closedListG = closedList.get(childKey).getGValue();
					if(childG < closedListG){
						//found shorter path to some node already found
						closedList.remove(childKey);
						openList.add(childNode);
					}
				}else{
					openList.add(childNode);
				}
			}
		}
		
		//goal state not found!
		return null;
	}
}
