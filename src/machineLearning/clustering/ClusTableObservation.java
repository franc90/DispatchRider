package machineLearning.clustering;

import jade.util.leap.Serializable;

import java.util.Map;
import java.util.TreeMap;

public class ClusTableObservation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stateName;
	// maesure name -> value, e.g. M1 -> 17.37
	private Map<String, Double> measure = new TreeMap<String, Double>();

	public ClusTableObservation(String stateName, Map<String, Double> measure) {
		super();
		this.stateName = stateName;
		this.measure = measure;
	}

	public ClusTableObservation(String stateName) {
		super();
		this.stateName = stateName;
	}

	public void addMeasureElement(String measureName, double value) {
		measure.put(measureName, value);
	}

	public String getStateName() {
		return stateName;
	}

	public Map<String, Double> getMeasure() {
		return measure;
	}
}
