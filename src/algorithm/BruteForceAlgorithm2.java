package algorithm;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgent;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

/**
 * This algorithm uses BruteForceAlgorithm. It differs only at the beginning. In
 * BruteForceAlgorithm new commission is being inserted to current schedule. In
 * this algorithm, every commissions are inserted once again (including new
 * commission)
 */
public class BruteForceAlgorithm2 extends Algorithm {

	private static final long serialVersionUID = 1L;
	private double maxLoad;
	private Point2D.Double depot;
	// private int deadline;
	private SimInfo simInfo;

	public BruteForceAlgorithm2(double maxLoad, SimInfo simInfo) {
		this.maxLoad = maxLoad;
		this.simInfo = simInfo;
		// this.deadline = (int) simInfo.getDeadline();
		this.depot = simInfo.getDepot();
	}

	public BruteForceAlgorithm2() {
	}

	@Override
	public SimInfo getSimInfo() {
		return simInfo;
	}

	@Override
	public void init(double maxLoad, SimInfo simInfo) {
		this.maxLoad = maxLoad;
		this.simInfo = simInfo;
		// this.deadline = (int) simInfo.getDeadline();
		this.depot = simInfo.getDepot();
	}

	@Override
	public Point2D.Double getDepot() {
		return depot;
	}

	/*
	 * It works like makeSchedule in BruteForceAlgorithm
	 */
	private Schedule addCommissionToSchedule(Commission commissionToAdd,
			Point2D.Double currentLocation, Schedule currentSchedule,
			int timestamp) {
		Schedule schedule = simInfo.createSchedule(null);
		if (currentSchedule != null) {
			schedule = simInfo.createSchedule(currentSchedule.getAlgorithm(),
					currentSchedule.getCurrentCommissionNr(),
					currentSchedule.getCreationTime());
			schedule.initSchedule(currentSchedule);
			for (int i = 0; i < currentSchedule.size(); i++) {
				schedule.addCommission(
						Commission.copy(currentSchedule.getCommission(i)),
						currentSchedule.getCommission(i).isPickup());
				schedule.addOriginalCommission(currentSchedule.getCommission(i));
			}
		}

		double load;

		int bestIndex = -1;
		int bestIndex2 = -1;
		// double bestDistance = Double.MAX_VALUE;
		// double tmpDistance;
		double bestCost = Double.MAX_VALUE;
		double tmpCost;

		int begin = currentSchedule.getNextLocationId(depot, timestamp);
		for (int i = begin; i < schedule.size(); i++) {
			schedule.addCommission(i, commissionToAdd, true);
			for (int j = i + 1; j < schedule.size(); j++) {
				schedule.addCommission(j, commissionToAdd, false);
				// tmpDistance = schedule.getDistance(depot);
				tmpCost = schedule.calculateCost(simInfo);
				load = schedule.isLoadOK(maxLoad);
				// if (tmpDistance < bestDistance
				// && schedule.calculateTime(depot) <= deadline
				// && schedule.calculateTime(depot) > 0 && load == 0.0) {
				// bestDistance = tmpDistance;
				if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
					bestCost = tmpCost;
					bestIndex = i;
					bestIndex2 = j;
				}
				schedule.removeCommission(j);
			}
			schedule.addCommission(commissionToAdd, false);
			// tmpDistance = schedule.getDistance(depot);
			tmpCost = schedule.calculateCost(simInfo);
			load = schedule.isLoadOK(maxLoad);
			// if (tmpDistance < bestDistance
			// && schedule.calculateTime(depot) <= deadline
			// && schedule.calculateTime(depot) > 0 && load == 0.0) {
			// bestDistance = tmpDistance;
			if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
				bestCost = tmpCost;
				bestIndex = i;
				bestIndex2 = -2;
			}
			schedule.removeCommission(schedule.size() - 1);
			schedule.removeCommission(i);
		}

		schedule.addCommission(commissionToAdd, true);
		schedule.addCommission(commissionToAdd, false);
		// tmpDistance = schedule.getDistance(depot);
		tmpCost = schedule.calculateCost(simInfo);
		load = schedule.isLoadOK(maxLoad);
		// if (tmpDistance < bestDistance
		// && schedule.calculateTime(depot) <= deadline
		// && schedule.calculateTime(depot) > 0 && load == 0.0) {
		if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
			return schedule;
		}

		schedule.removeCommission(schedule.size() - 1);
		schedule.removeCommission(schedule.size() - 1);

		if (bestIndex == -1 || bestIndex2 == -1)
			return null;
		schedule.addCommission(bestIndex, commissionToAdd, true);
		if (bestIndex2 == -2)
			schedule.addCommission(commissionToAdd, false);
		else
			schedule.addCommission(bestIndex2, commissionToAdd, false);
		return schedule;

	}

	/*
	 * Main method which insert commissions into schedule
	 */
	@Override
	public Schedule makeSchedule(AlgorithmAgentParent agent,
			Commission commissionToAdd, Point2D.Double currentLocation,
			Schedule currentSchedule, int timestamp) {
		Schedule schedule = simInfo.createSchedule(null);
		List<Commission> commissions = new LinkedList<Commission>();

		if (currentSchedule != null) {
			/*
			 * We calculate from which index, we can do changes in schedule
			 */
			schedule = simInfo.createSchedule(currentSchedule.getAlgorithm());
			int begin = currentSchedule.getNextLocationId(depot, timestamp);
			Commission com;
			for (int i = 0; i < begin; i++) {
				com = currentSchedule.getCommission(i);
				schedule.addCommission(com, currentSchedule.isPickup(i));
			}
			/*
			 * Commissions which will be inserting is being stored in
			 * commissions list
			 */
			for (int i = begin; i < currentSchedule.size(); i++) {
				if (currentSchedule.isPickup(i)) {
					commissions.add(Commission.copy(currentSchedule
							.getCommission(i)));
					schedule.addOriginalCommission(Commission
							.copy(currentSchedule.getCommission(i)));
				}
			}
		}
		commissions.add(commissionToAdd);
		schedule.addOriginalCommission(commissionToAdd);
		Schedule tmpSchedule;
		/*
		 * We can sort commissions before inserting them
		 */
		// Collections.sort(commissions, new TimeComparator(true,null));
		// Collections.sort(commissions, new DistanceComparator(true,depot));
		// Collections.sort(commissions, new CommissionsComparator(depot));
		// Collections.reverse(commissions);
		for (Commission com : commissions) {
			tmpSchedule = addCommissionToSchedule(com, null, schedule,
					timestamp);
			if (tmpSchedule == null) {
				return null;

			}
			schedule = tmpSchedule;
		}

		return schedule;
	}

	@Override
	public void setMaxLoad(double maxLoad) {
		this.maxLoad = maxLoad;
	}

	@Override
	public List<Class<? extends AlgorithmAgent>> getHelperAgentsClasses() {
		return new LinkedList<Class<? extends AlgorithmAgent>>();
	}
}
