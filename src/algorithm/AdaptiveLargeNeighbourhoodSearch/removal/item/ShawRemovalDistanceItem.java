package algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item;

import util.selector.Item;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.method.ShawRemoval;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.method.shawcomparators.AllComparator;

public class ShawRemovalDistanceItem extends Item<RemovalMethod> {

	public ShawRemovalDistanceItem(int probability) {
		super(probability);
	}

	@Override
	public RemovalMethod getIncludedValue() {
		return new ShawRemoval(new AllComparator(0.0, 1.0, 0.0));
	}

}
