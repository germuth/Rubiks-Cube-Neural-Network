package ca.germuth.neural_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class BreadthFirstSearch {
	public static SearchNode runSearch(Searchable start, Searchable goal){
		LinkedList<SearchNode> openList = new LinkedList<SearchNode>();
		HashMap<String, Boolean> closedList = new HashMap<String, Boolean>();
		SearchNode startingNode = new SearchNode(start, 0, null);
		openList.add(startingNode);
		String goalKey = goal.getKey();
		
		while(!openList.isEmpty()){
			SearchNode current = openList.pop();
			String currKey = current.getSearchable().getKey();
			
			//have we already been to this state
			if(closedList.containsKey(currKey)){
				continue;
			}
			closedList.put(currKey, true);
			
			if(currKey.equals(goalKey)){
				return current;
			}
			
			ArrayList<Searchable> children = current.getSearchable().getChildren();
			for(Searchable child: children){
				String childKey = child.getKey();
				
//				boolean addChild = true;
				//can't do because i don't have equals method
//				if(openList.contains(child)){
//					addChild = false;
//				}
				
				//rather than check whole open list
				//which is O(n) or maybe O(log(n)) with tree data strucuter
				//can just add it anyway
				//and remove it at start
//				for(SearchNode s: openList){
//					if(s.getSearchable().getKey().equals(childKey)){
//						addChild = false;
//						break;
//					}
//				}
				
//				if(addChild){
//					if(closedList.containsKey(child.getKey())){
//						addChild = false;
//					}
//				}else{
//				if(addChild){
				if(!closedList.containsKey(child.getKey())){
					SearchNode childNode = new SearchNode(child, current.getHValue() + 1, current);
					openList.add(childNode);
				}
			}
		}
		
		//goal state not found!
		return null;
	}
}
