package measure;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import dtp.commission.Commission;
import dtp.simmulation.SimInfo;

/**
 * Container for MeasureCalculators. It uses reflection to create calculators by
 * their name. It simplifies adding new calculators (more is described in
 * MeasureCalculator class)
 */
public class MeasureCalculatorsHolder implements Serializable {

	private static final long serialVersionUID = -1180417906877993843L;
	private final List<MeasureCalculator> calculators = new LinkedList<MeasureCalculator>();
	private final List<String> visualizationMeasuresNames = new LinkedList<String>();
	private int timeGap;
	protected int timestamp;

	public void addVisualizationMeasuresNames(String name) {
		visualizationMeasuresNames.add(name);
	}

	public List<String> getVisualizationMeasuresNames() {
		return visualizationMeasuresNames;
	}

	public void addCalculator(String name) throws IllegalArgumentException {
		try {
			MeasureCalculator calculator = (MeasureCalculator) Class.forName(
					"measure." + name).newInstance();
			calculators.add(calculator);
		} catch (Exception e) {
			throw new IllegalArgumentException("Wrong measure name: " + name);
		}
	}

	public List<MeasureCalculator> getCalculators() {
		return calculators;
	}

	public int getTimeGap() {
		return timeGap;
	}

	public void setTimeGap(int timeGap) {
		this.timeGap = timeGap;
	}

	public void setSimInfo(SimInfo info) {
		for (MeasureCalculator calc : calculators)
			calc.setSimInfo(info);
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
		for (MeasureCalculator calc : calculators)
			calc.setTimestamp(timestamp);
	}

	public void setCommissions(List<Commission> commissions) {
		for (MeasureCalculator calc : calculators)
			calc.setCommissions(commissions);
	}
}
