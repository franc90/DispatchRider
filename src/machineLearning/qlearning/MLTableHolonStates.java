package machineLearning.qlearning;

import jade.core.AID;

import java.util.Map;

import measure.Measure;
import dtp.jade.transport.Calculator;

public class MLTableHolonStates extends MLTableStates {

	private static final long serialVersionUID = -425137327517286409L;

	@Override
	public String getCurrentState(Map<String, Measure> measures, AID aid) {
		aggregatorManager.setMeasures(measures);
		String fun;
		for (String state : values.keySet()) {
			fun = aggregatorManager.insertAggregateValues(values.get(state));
			fun = insertHolonMeasures(fun, measures, aid);
			if (Calculator.calculateBoolExpr(fun) == true) {
				aggregatorManager.aggregationFinished();
				return state;
			}
		}
		aggregatorManager.aggregationFinished();
		throw new IllegalStateException("No state found");
	}

	private String insertHolonMeasures(String fun,
			Map<String, Measure> measures, AID aid) {

		for (String measure : measures.keySet()) {
			if (measures.get(measure).getValues().get(aid) != null)
				fun = fun.replace(measure, measures.get(measure).getValues()
						.get(aid).toString());
		}

		return fun;
	}
}
