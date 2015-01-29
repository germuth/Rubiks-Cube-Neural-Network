package ca.germuth.neural_network;

public class SearchFacade {
	public static SearchNode runSearch(SearchType st, Searchable start, Searchable goal){
		switch(st){
			case ASTAR:
				return AStarSearch.runSearch(start,  goal);
			case BFS:
				return BreadthFirstSearch.runSearch(start, goal);
			default:
				return BreadthFirstSearch.runSearch(start, goal);
		}
	}
}
