package util.selector;

public abstract class Item<T> {

	private int relativeProbability;

	public Item(int relativeProb) {
		this.relativeProbability = relativeProb;
	}

	public abstract T getIncludedValue();

	public int getRelativeProbability() {
		return relativeProbability;
	}

	public void setRelativeProbability(int relativeProbability) {
		this.relativeProbability = relativeProbability;
	}

}
