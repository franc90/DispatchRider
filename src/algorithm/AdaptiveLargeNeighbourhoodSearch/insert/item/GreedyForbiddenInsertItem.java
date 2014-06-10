package algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item;

import util.selector.Item;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.method.GreedyForbiddenInsert;

public class GreedyForbiddenInsertItem extends Item<InsertMethod> {

	public GreedyForbiddenInsertItem(int priority) {
		super(priority);
	}

	@Override
	public InsertMethod getIncludedValue() {
		return new GreedyForbiddenInsert();
	}

}
