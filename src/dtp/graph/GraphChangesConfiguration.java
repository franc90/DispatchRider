package dtp.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphChangesConfiguration {
	private class GraphChange {
		public int sPointId;
		public int ePointId;
		public double cost;
		public boolean both;

		public GraphChange(int sPointId, int ePointId, double cost, boolean both) {
			this.sPointId = sPointId;
			this.ePointId = ePointId;
			this.cost = cost;
			this.both = both;
		}
	}

	private Graph graph;
	private final Map<Integer, List<GraphChange>> changes = new HashMap<Integer, List<GraphChange>>();

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public void addChange(Integer sPointId, Integer ePointId, double cost,
			boolean isBoth, int timestamp) {

		GraphChange change = new GraphChange(sPointId, ePointId, cost, isBoth);
		List<GraphChange> changes = this.changes.get(timestamp);
		if (changes == null) {
			changes = new LinkedList<GraphChange>();
			this.changes.put(timestamp, changes);
		}
		changes.add(change);
	}

	public Object[] changeGraph(int timestamp) {
		List<Integer> keys = new LinkedList<Integer>(this.changes.keySet());
		if (keys.size() == 0)
			return null;
		Collections.sort(keys);
		Integer key = keys.get(0);
		if (key > timestamp)
			return null;
		List<GraphChange> changes = this.changes.get(key);
		this.changes.remove(key);
		GraphPoint sPoint;
		GraphPoint ePoint;
		GraphLink link;
		for (GraphChange change : changes) {
			sPoint = graph.getPointById(change.sPointId);
			ePoint = graph.getPointById(change.ePointId);
			link = sPoint.getLinkTo(ePoint);
			link.setCost((int) change.cost);
			if (change.both) {
				link = ePoint.getLinkTo(sPoint);
				link.setCost((int) change.cost);
			}
		}
		return new Object[] { graph, key };
	}

}
