package algorithm;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgent;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

/**
 * This algorithm uses full review of possibilities of insertion commissions.
 * Each commission part (pickup and delivery) is insert in any possible place in
 * schedule, then the best combination is chosen.
 * 
 */
public class BruteForceAlgorithm extends Algorithm {

	private static final long serialVersionUID = 1L;
	private double maxLoad;
	private Point2D.Double depot;
	// private int deadline;
	private SimInfo simInfo;

	public BruteForceAlgorithm(double maxLoad, SimInfo simInfo) {
		this.maxLoad = maxLoad;
		this.simInfo = simInfo;
		depot = simInfo.getDepot();
		// deadline = (int) simInfo.getDeadline();
	}

	public BruteForceAlgorithm() {
	}

	@Override
	public SimInfo getSimInfo() {
		return simInfo;
	}

	@Override
	public Point2D.Double getDepot() {
		return depot;
	}

	@Override
	public void init(double maxLoad, SimInfo simInfo) {
		this.maxLoad = maxLoad;
		this.simInfo = simInfo;
		depot = simInfo.getDepot();
		// deadline = (int) simInfo.getDeadline();
	}

	@Override
	public Schedule makeSchedule(AlgorithmAgentParent agent,
			Commission commissionToAdd, Point2D.Double currentLocation,
			Schedule currentSchedule, int timestamp) {
		Schedule schedule = simInfo.createSchedule(null);

		/*
		 * We copy currentSchedule to prevent of changing it.
		 */
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

		/*
		 * bestIndex - index where we should insert pickup bestIndex2 - insex
		 * where we should insert delivery
		 */
		int bestIndex = -1;
		int bestIndex2 = -1;
		// double bestDist = Double.MAX_VALUE;
		// double tmpDistance;
		double bestCost = Double.MAX_VALUE;
		double tmpCost;

		/*
		 * We get index of first commission, which can be change. It is
		 * necessary, because of dynamic problem, where commission has 3 states:
		 * already realized, during realization and to realize. Only commission
		 * with status 'to realize' can be moved. Other commissions can't be
		 * moved and any commission can be insert between them.
		 */
		int begin = currentSchedule.getNextLocationId(depot, timestamp);

		if (begin != 0 && timestamp == 0) {
			System.out.println("begin " + begin);
			System.exit(0);
		}

		/*
		 * We check every possibility of insert pickup and delivery. The best
		 * combination is determined by distance, which unit will have to travel
		 * (here you can also use time of realization all commissions, by we
		 * chose distance, because after running some tests, it was giving
		 * better results)
		 */
		for (int i = begin; i < schedule.size(); i++) {
			schedule.addCommission(i, commissionToAdd, true);
			for (int j = i + 1; j < schedule.size(); j++) {
				schedule.addCommission(j, commissionToAdd, false);
				// tmpDistance = schedule.getDistance(depot);
				tmpCost = schedule.calculateCost(simInfo);
				load = schedule.isLoadOK(maxLoad);
				/*
				 * Lines like that is for check if new schedule meets time and
				 * load restrictions
				 */
				// if (tmpDistance < bestDist
				// && schedule.calculateTime(depot) <= deadline
				// && schedule.calculateTime(depot) > 0 && load == 0.0) {
				if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
					// bestDist = tmpDistance;
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
			// if (tmpDistance < bestDist
			// && schedule.calculateTime(depot) <= deadline
			// && schedule.calculateTime(depot) > 0 && load == 0.0) {
			// bestDist = tmpDistance;
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
		// if (tmpDistance < bestDist && schedule.calculateTime(depot) <=
		// deadline
		// && schedule.calculateTime(depot) > 0 && load == 0.0) {
		if (tmpCost < bestCost && tmpCost > 0 && load == 0.0) {
			return schedule;
		}

		schedule.removeCommission(schedule.size() - 1);
		schedule.removeCommission(schedule.size() - 1);

		/*
		 * Creation of optimal schedule
		 */
		if (bestIndex == -1 || bestIndex2 == -1) {
			return null;
		}
		schedule.addCommission(bestIndex, commissionToAdd, true);
		if (bestIndex2 == -2)
			schedule.addCommission(commissionToAdd, false);
		else
			schedule.addCommission(bestIndex2, commissionToAdd, false);
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
