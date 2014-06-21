package util.selector;

import java.util.Random;

public class RandomItemSelector<T> extends ItemSelector<T>{

	private Random random = new Random();

	public void addItem(Item<T> item) {
		super.addItem(item);
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
