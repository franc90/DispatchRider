package algorithm.AdaptiveLargeNeighbourhoodSearch;

import java.util.Random;

import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.GreedyInsert;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.RegretInsert;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RandomRemoval;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.ShawRemoval;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.WorstRemoval;

public class RouletteWheel {

	public static InsertMethod getInsertMethod(){
		Random r = new Random();
		int y = r.nextInt(10);
		if (y < 5) return new RegretInsert();
		else return new GreedyInsert();
	}
	
	public static RemovalMethod getRemovalMethod(){
		Random r = new Random();
		int y = r.nextInt(10);
		if (y < 7) return new ShawRemoval();
		else if (y < 9) return new WorstRemoval();
		else return new RandomRemoval();

	}

}
