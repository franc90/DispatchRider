package machineLearning.clustering;

import jade.core.AID;

import java.util.Map;

import measure.Measure;

public class HolonRewardFunction extends RewardFunction {

	private static final long serialVersionUID = 2196638348515048536L;

	public HolonRewardFunction(String function) {
		super(function);
	}

	@Override
	protected String insertMeasures(String fun, Map<String, Measure> measures,
			AID aid) {
		aggregatorManager.setMeasures(measures);
		fun = aggregatorManager.insertAggregateValues(fun);
		for (String name : measures.keySet()) {

			try {
				fun = fun.replace(name, measures.get(name).getValues().get(aid)
						.toString());
			} catch (NullPointerException e) {
				fun = fun.replace(name, "0");
			}
		}
		aggregatorManager.aggregationFinished();
		return fun;
	}

}
