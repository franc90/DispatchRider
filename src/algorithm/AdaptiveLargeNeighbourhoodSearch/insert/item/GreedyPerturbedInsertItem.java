package algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item;

import util.selector.Item;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.method.GreedyPerturbedInsert;

public class GreedyPerturbedInsertItem extends Item<InsertMethod> {

	public GreedyPerturbedInsertItem(int probability) {
		super(probability);
	}

	@Override
	public InsertMethod getIncludedValue() {
		return new GreedyPerturbedInsert();
	}

}
