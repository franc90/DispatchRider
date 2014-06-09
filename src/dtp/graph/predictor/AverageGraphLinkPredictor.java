package dtp.graph.predictor;

import java.util.LinkedList;
import java.util.List;

import measure.MeasureHelper;
import dtp.graph.Graph;
import dtp.graph.GraphLink;

public class AverageGraphLinkPredictor extends GraphLinkPredictor {

	private static final long serialVersionUID = 1619208377903860306L;

	@Override
	public double getCost(GraphLink link) {
		if (history.size() == 0)
			return link.getCost();
		GraphLink gLink;
		List<Double> values = new LinkedList<Double>();
		for (Graph graph : history) {
			gLink = getLink(graph, link);
			values.add(gLink.getCost());
		}
		return MeasureHelper.average(values);
	}

}
