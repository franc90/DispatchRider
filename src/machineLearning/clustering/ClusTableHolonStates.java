package machineLearning.clustering;

import jade.core.AID;

import java.util.Map;

import measure.Measure;

import org.apache.log4j.Logger;

public class ClusTableHolonStates extends ClusTableStates {

	private static final long serialVersionUID = -425137327517286409L;
	private static final Logger logger = Logger
			.getLogger(ClusTableHolonStates.class);

	@Override
	public String getCurrentState(Map<String, Measure> measures, AID aid) {
		Map<String, Double> currentMeasures = clusTableMeasures
				.getCurrentMeasuresVector(measures, aid);

		String currentState = predictCurrentStateByR(currentMeasures, aid);

		logger.info("Current holon state: " + currentState);

		measurmentsHistory.add(currentMeasures);

		return currentState;
	}
}
