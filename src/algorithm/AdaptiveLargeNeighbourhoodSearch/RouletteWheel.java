package algorithm.AdaptiveLargeNeighbourhoodSearch;

import util.selector.RandomItemSelector;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.GreedyForbiddenInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.GreedyInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.GreedyPerturbedInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.RegretInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.RandomRemovalItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalBalancedItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalDistanceItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalTimeItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalCapacityItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.WorstRemovalItem;

public class RouletteWheel {

	private static RandomItemSelector<InsertMethod> insertMethodSelector = new RandomItemSelector<InsertMethod>();

	private static RandomItemSelector<RemovalMethod> removalMethodSelector = new RandomItemSelector<RemovalMethod>();

	static {
		insertMethodSelector.addItem(new RegretInsertItem(3));
		insertMethodSelector.addItem(new GreedyInsertItem(1));
		insertMethodSelector.addItem(new GreedyPerturbedInsertItem(1));
		insertMethodSelector.addItem(new GreedyForbiddenInsertItem(1));

		removalMethodSelector.addItem(new ShawRemovalBalancedItem(3));
		removalMethodSelector.addItem(new ShawRemovalTimeItem(2));
		removalMethodSelector.addItem(new ShawRemovalDistanceItem(2));
		removalMethodSelector.addItem(new ShawRemovalCapacityItem(2));
		removalMethodSelector.addItem(new WorstRemovalItem(1));
		removalMethodSelector.addItem(new RandomRemovalItem(1));
	}

	public static InsertMethod getInsertMethod() {
		return insertMethodSelector.getRandomItem();
	}

	public static RemovalMethod getRemovalMethod() {
		return removalMethodSelector.getRandomItem();
	}

}
