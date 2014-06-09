package machineLearning.clustering;

import jade.core.AID;

import java.util.Map;

import measure.Measure;

import org.apache.log4j.Logger;

public class ClusTableGlobalStates extends ClusTableStates {

	private static final long serialVersionUID = 1214240735166609605L;

	private static final Logger logger = Logger
			.getLogger(ClusTableGlobalStates.class);

	@Override
	public String getCurrentState(Map<String, Measure> measures, AID aid) {
		Map<String, Double> currentMeasures = clusTableMeasures
				.getCurrentMeasuresVector(measures);
		String currentState = predictCurrentStateByR(currentMeasures, aid);

		logger.info("Current global state: " + currentState);

		measurmentsHistory.add(currentMeasures);

		return currentState;
	}

}
