package machineLearning.qlearning;

import java.io.Serializable;

public class MLTableCell implements Serializable {

	private static final long serialVersionUID = -6185146081148117263L;
	private Integer useCount = 0;
	private Double value = 0.0;
	private double probability;
	private String state;
	private String action;

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public Integer getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
