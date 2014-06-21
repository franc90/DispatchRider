package util.selector;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class ItemSelector<T> {

	protected List<Item<T>> items = new LinkedList<Item<T>>();

	public void addItem(Item<T> item) {
		items.add(item);
	}

	/**
	 * Select {@link Item}.
	 * 
	 * @return {@link Item}
	 */
	
	public abstract Item<T> getItem();
}
