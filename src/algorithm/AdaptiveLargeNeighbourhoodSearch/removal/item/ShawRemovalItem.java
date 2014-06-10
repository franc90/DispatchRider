package algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item;

import util.selector.Item;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.method.ShawRemoval;

public class ShawRemovalItem extends Item<RemovalMethod> {

	public ShawRemovalItem(int probability) {
		super(probability);
	}

	@Override
	public RemovalMethod getIncludedValue() {
		return new ShawRemoval();
	}

}
