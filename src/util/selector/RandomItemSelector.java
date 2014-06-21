package util.selector;

import java.util.Random;

public class RandomItemSelector<T> extends ItemSelector<T>{

	private Random random = new Random();

	public void addItem(Item<T> item) {
		super.addItem(item);
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
	 * Select random {@link Item}.
	 * 
	 * @return {@link Item}
	 */
	@Override
	public Item<T> getItem() {
		Item<T> selectedItem = items.get(random.nextInt(items.size()));

		return selectedItem;
	}
}
