package machineLearning.qlearning;

import jade.core.AID;

import java.util.Map;

import measure.Measure;
import dtp.jade.transport.Calculator;

public class MLTableGlobalStates extends MLTableStates {

	private static final long serialVersionUID = 1214240735166609605L;

	@Override
	public String getCurrentState(Map<String, Measure> measures, AID aid) {
		aggregatorManager.setMeasures(measures);
		String fun;
		for (String state : values.keySet()) {
			fun = aggregatorManager.insertAggregateValues(values.get(state));
			if (Calculator.calculateBoolExpr(fun) == true) {
				aggregatorManager.aggregationFinished();
				return state;
			}
		}
		aggregatorManager.aggregationFinished();
		throw new IllegalStateException("No state found");
	}
}
