package dtp.graph.predictor;

import dtp.graph.GraphLink;

public class StandardGraphLinkPredictor extends GraphLinkPredictor {

	private static final long serialVersionUID = 7905899675645325599L;

	@Override
	public double getCost(GraphLink link) {
		return link.getCost();
	}

}
