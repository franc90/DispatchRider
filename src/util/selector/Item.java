package util.selector;

public abstract class Item<T> {

	private int relativeProbability;
	private double grade = 0.0;

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
	
	public double getGrade(){
		return grade;
	}
	
	public void setGrade(double grade){
		this.grade = grade;
	}

}
