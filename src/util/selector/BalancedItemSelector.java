package util.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BalancedItemSelector<T> extends ItemSelector<T>{

	private List<Item<T>> itemsCopy = new ArrayList<Item<T>>();

	private int totalProbabilitiesSum = 0;

	public void addItem(Item<T> item) {
		super.addItem(item);
		totalProbabilitiesSum += item.getRelativeProbability();
	}

	/**
	 * Select random {@link Item} every item is used the same amount of time.
	 * 
	 * @return {@link Item}
	 */
	@Override
	public Item<T> getItem() {
		if (itemsCopy.size() == 0){
			itemsCopy.addAll(items);
			Collections.shuffle(itemsCopy);
		}
		
		return itemsCopy.remove(0);
	}
}
