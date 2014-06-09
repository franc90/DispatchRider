package machineLearning.aggregator;

import measure.Measure;

public class SumAggregator extends MLAggregator {

	private static final long serialVersionUID = 4312282194631470179L;

	@Override
	public Double aggregate(String name) {
		if (values.containsKey(name))
			return values.get(name);
		Measure measure = measures.get(name);
		Double result = new Double(0.0);
		for (double value : measure.getValues().values()) {
			result += value;
		}
		if (measure.getValues().size() == 0) {
			values.put(name, 0.0);
			return 0.0;
		}
		values.put(name, result);
		return result;
	}

	@Override
	public String getName() {
		return "sum";
	}

}
