package util.selector;

import java.util.List;
import java.util.Random;

public class DefaultItemSelector<T> extends ItemSelector<T>{

	private Random random = new Random();

	private int totalProbabilitiesSum = 0;
	
	public void removeItem(Item<T> item) {
		if (items.remove(item)) {
			totalProbabilitiesSum -= item.getRelativeProbability();
		}
	}

	public void clearItems() {
		totalProbabilitiesSum = 0;
		items.clear();
	}

	public List<Item<T>> getItems() {
		return items;
	}

	public void addItem(Item<T> item) {
		super.addItem(item);
		totalProbabilitiesSum += item.getRelativeProbability();
	}

	/**
	 * Select random {@link Item} with relative probability.
	 * 
	 * @return {@link Item}
	 */
	@Override
	public Item<T> getItem() {
		int index = random.nextInt(totalProbabilitiesSum) + 1;
		int sum = 0;
		int i = 0;

		while (sum < index) {
			sum += items.get(i++).getRelativeProbability();
		}
		Item<T> selectedItem = items.get(i - 1);

		return selectedItem;
	}
}
