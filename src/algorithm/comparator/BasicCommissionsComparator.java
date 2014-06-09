package algorithm.comparator;

import jade.core.Agent;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import algorithm.Helper;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgent;
import dtp.simmulation.SimInfo;

public class BasicCommissionsComparator extends CommissionsComparator implements
		Comparator<Commission> {

	private static final long serialVersionUID = 4200296374440999230L;
	private SimInfo simInfo;

	public int compare(Commission com1, Commission com2) {
		Point2D.Double depot = simInfo.getDepot();
		double dist1;
		double dist2;
		double tmp;
		dist1 = Helper.calculateDistance(depot,
				new Point2D.Double(com1.getPickupX(), com1.getPickupY()));
		tmp = Helper.calculateDistance(depot,
				new Point2D.Double(com1.getDeliveryX(), com1.getDeliveryY()));
		if (tmp > dist1)
			dist1 = tmp;
		dist2 = Helper.calculateDistance(depot,
				new Point2D.Double(com2.getPickupX(), com2.getPickupY()));
		tmp = Helper.calculateDistance(depot,
				new Point2D.Double(com2.getDeliveryX(), com2.getDeliveryY()));
		if (tmp > dist2)
			dist2 = tmp;
		if (dist1 > dist2)
			return -1;
		else
			return 1;
	}

	@Override
	public List<Class<? extends AlgorithmAgent>> getHelperAgentsClasses() {
		return new LinkedList<Class<? extends AlgorithmAgent>>();
	}

	@Override
	public List<Commission> sort(List<Commission> commissions, Agent agent,
			SimInfo simInfo) {
		this.simInfo = simInfo;
		Collections.sort(commissions, this);
		return commissions;
	}
}
