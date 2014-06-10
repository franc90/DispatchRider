package algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item;

import util.selector.Item;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.method.GreedyInsert;

public class GreedyInsertItem extends Item<InsertMethod> {

	public GreedyInsertItem(int probability) {
		super(probability);
	}

	@Override
	public InsertMethod getIncludedValue() {
		return new GreedyInsert();
	}

}
