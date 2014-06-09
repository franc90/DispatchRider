package measure;

import jade.core.AID;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import algorithm.Helper;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class PercentageOfDelayComs extends MeasureCalculator {

	private static final long serialVersionUID = 4180098371997717995L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {
		Map<AID, Schedule> schedules;
		if (newSchedules == null)
			schedules = oldSchedules;
		else
			schedules = newSchedules;
		Measure result = new Measure();
		for (AID aid : schedules.keySet())
			result.put(aid, calculatePercentageOfDelayComs(schedules.get(aid)));
		return result;
	}

	@Override
	public String getName() {
		return "PercentageOfDelayComs";
	}

	private double calculatePercentageOfDelayComs(Schedule schedule) {
		int delayComs = 0;
		double time = schedule.getCreationTime();
		Point2D.Double currentLocation = info.getDepot();
		Point2D.Double nextLocation;
		Commission com;
		double dist;
		List<Commission> commissions = schedule.getAllCommissions();
		for (int i = 0; i < commissions.size(); i++) {
			com = commissions.get(i);
			if (schedule.isPickup(i)) {
				nextLocation = new Point2D.Double(com.getPickupX(),
						com.getPickupY());
				dist = Helper.calculateDistance(currentLocation, nextLocation);
				if (time + dist > com.getPickupTime2()) {
					delayComs++;
				}
				if (time + dist < com.getPickupTime1())
					time = com.getPickupTime1();
				else
					time += dist;
				time += com.getPickUpServiceTime();
			} else {
				nextLocation = new Point2D.Double(com.getDeliveryX(),
						com.getDeliveryY());
				dist = Helper.calculateDistance(currentLocation, nextLocation);
				if (time + dist > com.getDeliveryTime2()) {
					delayComs++;
				}
				if (time + dist < com.getDeliveryTime1())
					time = com.getDeliveryTime1();
				else
					time += dist;
				time += com.getDeliveryServiceTime();
			}
			currentLocation = nextLocation;
		}
		if (commissions.size() == 0) return 0;
		return delayComs / commissions.size() * 100;
	}
}
