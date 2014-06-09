package machineLearning.clustering;

import jade.core.AID;

import java.util.Map;

import measure.Measure;

public class GlobalRewardFunction extends RewardFunction {

	private static final long serialVersionUID = -2338734285613466123L;

	public GlobalRewardFunction(String function) {
		super(function);
	}

	@Override
	protected String insertMeasures(String fun, Map<String, Measure> measures,
			AID aid) {
		aggregatorManager.setMeasures(measures);

		fun = aggregatorManager.insertAggregateValues(fun);

		aggregatorManager.aggregationFinished();
		return fun;
	}

}
