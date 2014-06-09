package algorithm.AdaptiveLargeNeighbourhoodSearch;

import java.util.Random;

import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.GreedyInsert;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.GreedyPerturbedInsert;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.RegretInsert;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RandomRemoval;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.ShawRemoval;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.WorstRemoval;

public class RouletteWheel {

	private static final int NUMER_OF_ALGORITHMS = 3;

	private static final int REGRET = 0;

	private static final int GREEDY = 1;

	private static final int GREEDY_PERTURBED = 2;

	public static InsertMethod getInsertMethod() {
		Random random = new Random();
		int algorithmNumber = random.nextInt(NUMER_OF_ALGORITHMS);

		switch (algorithmNumber) {
		case REGRET:
			return new RegretInsert();
		case GREEDY:
			return new GreedyInsert();
		case GREEDY_PERTURBED:
			return new GreedyPerturbedInsert();
		default:
			return null;
		}

	}

	public static RemovalMethod getRemovalMethod() {
		Random r = new Random();
		int y = r.nextInt(10);
		if (y < 7)
			return new ShawRemoval();
		else if (y < 9)
			return new WorstRemoval();
		else
			return new RandomRemoval();

	}

}
