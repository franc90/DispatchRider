package machineLearning.aggregator;

import java.util.LinkedList;

import measure.Measure;
import measure.MeasureHelper;

public class StDevAggregator extends MLAggregator {

	private static final long serialVersionUID = -413539151014374865L;

	@Override
	public Double aggregate(String name) {
		if (values.containsKey(name))
			return values.get(name);
		Measure measure = measures.get(name);
		Double result = MeasureHelper.standardDeviation(new LinkedList<Double>(
				measure.getValues().values()));
		if (measure.getValues().size() == 0) {
			values.put(name, 0.0);
			return 0.0;
		}
		values.put(name, result);
		return result;
	}

	@Override
	public String getName() {
		return "stdev";
	}

}
