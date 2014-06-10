package algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item;

import util.selector.Item;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.method.RegretInsert;

public class RegretInsertItem extends Item<InsertMethod> {

	public RegretInsertItem(int probability) {
		super(probability);
	}

	@Override
	public InsertMethod getIncludedValue() {
		return new RegretInsert();
	}

}
