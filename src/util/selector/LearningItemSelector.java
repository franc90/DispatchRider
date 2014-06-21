package util.selector;

import java.util.Random;

public class LearningItemSelector<T> extends ItemSelector<T>{

	/**
	 * Select the best {@link Item}.
	 * 
	 * @return {@link Item}
	 */
	
	@Override
	public Item<T> getItem() {
		Item<T> tmp = items.get(0);
		//System.err.format("ITEM: %s GRADE: %f\n", tmp.getIncludedValue().getClass().getName(), tmp.getGrade());
		for (Item<T> item : items){
			//System.err.format("ITEM: %s GRADE: %f\n", item.getIncludedValue().getClass().getName(), item.getGrade());
			if (item.getGrade() > tmp.getGrade()){
				tmp = item;
			}
		}
		//System.err.format("SELECTED ITEM: %s GRADE: %f\n", tmp.getIncludedValue().getClass().getName(), tmp.getGrade());
		return tmp;
	}
}
