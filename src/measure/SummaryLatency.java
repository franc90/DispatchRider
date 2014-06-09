package measure;

import jade.core.AID;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import algorithm.Helper;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

public class SummaryLatency extends MeasureCalculator {

	private static final long serialVersionUID = -73692372488890269L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {
		Map<AID, Schedule> schedules;
		if (newSchedules == null)
			schedules = oldSchedules;
		else
			schedules = newSchedules;
		Measure result = new Measure();
		for (AID aid : schedules.keySet()) {
			result.put(aid, calculateLatency(schedules.get(aid), info));
		}
		return result;
	}

	@Override
	public String getName() {
		return "SummaryDelay";
	}

	private double calculateLatency(Schedule schedule, SimInfo info) {
		double time = schedule.getCreationTime();
		Point2D.Double currentLocation = info.getDepot();
		Point2D.Double nextLocation;
		Commission com;
		double dist;
		double latency = 0.0;
		List<Commission> commissions = schedule.getAllCommissions();
		for (int i = 0; i < commissions.size(); i++) {
			com = commissions.get(i);
			if (schedule.isPickup(i)) {
				nextLocation = new Point2D.Double(com.getPickupX(),
						com.getPickupY());
				dist = Helper.calculateDistance(currentLocation, nextLocation);
				if (time + dist > com.getPickupTime2())
					latency += time + dist - com.getPickupTime2();

				if (time + dist < com.getPickupTime1())
					time = com.getPickupTime1();
				else
					time += dist;
				time += com.getPickUpServiceTime();
			} else {
				nextLocation = new Point2D.Double(com.getDeliveryX(),
						com.getDeliveryY());
				dist = Helper.calculateDistance(currentLocation, nextLocation);
				if (time + dist > com.getDeliveryTime2())
					latency += time + dist - com.getDeliveryTime2();

				if (time + dist < com.getDeliveryTime1())
					time = com.getDeliveryTime1();
				else
					time += dist;
				time += com.getDeliveryServiceTime();
			}
			currentLocation = nextLocation;
		}
		return latency;
	}

}
