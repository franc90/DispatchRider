package util.selector;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomItemSelector<T> {

	private List<Item<T>> items = new LinkedList<Item<T>>();

	private Random random = new Random();

	private int totalProbabilitiesSum = 0;

	public void addItem(Item<T> item) {
		totalProbabilitiesSum += item.getRelativeProbability();
		items.add(item);
	}

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

	/**
	 * Select random {@link Item} with relative probability.
	 * 
	 * @return {@link Item}
	 */
	public T getRandomItem() {
		int index = random.nextInt(totalProbabilitiesSum) + 1;
		int sum = 0;
		int i = 0;

		while (sum < index) {
			sum += items.get(i++).getRelativeProbability();
		}
		Item<T> selectedItem = items.get(i - 1);

		return selectedItem.getIncludedValue();
	}
}
