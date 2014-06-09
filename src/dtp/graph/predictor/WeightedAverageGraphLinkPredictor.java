package dtp.graph.predictor;

import java.util.LinkedList;
import java.util.List;

import measure.MeasureHelper;
import dtp.graph.GraphLink;

public class WeightedAverageGraphLinkPredictor extends GraphLinkPredictor {

	private static final long serialVersionUID = -9037294430862658851L;

	@Override
	public double getCost(GraphLink link) {
		if (history.size() == 0)
			return link.getCost();
		double previousValue = getLink(history.get(history.size() - 1), link)
				.getCost();
		if (history.size() == 1)
			return previousValue;
		List<Double> values = new LinkedList<Double>();
		for (int i = 0; i < history.size() - 1; i++)
			values.add(getLink(history.get(i), link).getCost());
		return 0.5 * previousValue + 0.5 * MeasureHelper.average(values);
	}

}
