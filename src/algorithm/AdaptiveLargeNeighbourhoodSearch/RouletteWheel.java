package algorithm.AdaptiveLargeNeighbourhoodSearch;

import util.selector.RandomItemSelector;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.GreedyInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.GreedyPerturbedInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.RegretInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.RandomRemovalItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.WorstRemovalItem;

public class RouletteWheel {

	private static RandomItemSelector<InsertMethod> insertMethodSelector = new RandomItemSelector<InsertMethod>();

	private static RandomItemSelector<RemovalMethod> removalMethodSelector = new RandomItemSelector<RemovalMethod>();

	static {
		insertMethodSelector.addItem(new RegretInsertItem(2));
		insertMethodSelector.addItem(new GreedyInsertItem(1));
		insertMethodSelector.addItem(new GreedyPerturbedInsertItem(1));

		removalMethodSelector.addItem(new ShawRemovalItem(6));
		removalMethodSelector.addItem(new WorstRemovalItem(2));
		removalMethodSelector.addItem(new RandomRemovalItem(2));
	}

	public static InsertMethod getInsertMethod() {
		return insertMethodSelector.getRandomItem();
	}

	public static RemovalMethod getRemovalMethod() {
		return removalMethodSelector.getRandomItem();
	}

}
