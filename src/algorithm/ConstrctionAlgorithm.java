package algorithm;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgent;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

/**
 * Not used
 */
public class ConstrctionAlgorithm extends Algorithm {

	private static final long serialVersionUID = 5495195592887206357L;
	private Schedule schedule;
	private double maxLoad;
	private Point2D.Double depot;
	private int deadline;
	private double deadlineTmp;
	private final ComparatorType comparator;
	private SimInfo simInfo;

	@Override
	public Point2D.Double getDepot() {
		return depot;
	}

	@Override
	public SimInfo getSimInfo() {
		return simInfo;
	}

	@Override
	public void init(double maxLoad, SimInfo simInfo) {
		this.maxLoad = maxLoad;
		this.simInfo = simInfo;
		this.deadline = (int) simInfo.getDeadline();
		this.depot = simInfo.getDepot();
	}

	public ConstrctionAlgorithm(double maxLoad, SimInfo simInfo) {
		this.maxLoad = maxLoad;
		this.simInfo = simInfo;
		this.deadline = (int) simInfo.getDeadline();
		this.depot = simInfo.getDepot();
		comparator = ComparatorType.time;
	}

	public ConstrctionAlgorithm(double maxLoad, Point2D.Double depot,
			int deadline, ComparatorType comparator) {
		this.maxLoad = maxLoad;
		this.depot = depot;
		this.deadline = deadline;
		this.comparator = comparator;
	}

	public ConstrctionAlgorithm(ComparatorType comparator) {
		this.comparator = comparator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algorithm.Algorithm#makeSchedule(dtp.commission.Commission,
	 * java.awt.geom.Point2D.Double, algorithm.Schedule, algorithm.Schedule)
	 */
	@Override
	public Schedule makeSchedule(AlgorithmAgentParent agent,
			Commission commissionToAdd, Point2D.Double currentLocation,
			Schedule currentSchedule, int timestamp) {
		deadlineTmp = deadline;

		this.schedule = simInfo.createSchedule(null);
		List<Commission> commissions = new LinkedList<Commission>();
		if (currentSchedule != null) {
			schedule.setAlgorithm(currentSchedule.getAlgorithm());
			for (Commission com : currentSchedule.getOriginalCommissions()) {
				commissions.add(Commission.copy(com));
				schedule.addOriginalCommission(Commission.copy(com));
			}
		} else
			commissions = new LinkedList<Commission>();
		commissions.add(commissionToAdd);
		List<Commission> deliveries = new LinkedList<Commission>();
		Commission com;
		Integer load = 0;
		if (currentLocation == null)
			currentLocation = depot;
		schedule.addOriginalCommission(Commission.copy(commissionToAdd));
		do {
			/*
			 * System.out.println("Commissions"); for (Commission c :
			 * commissions) System.out.println(c);
			 * System.out.println("Deliveries"); for (Commission c : deliveries)
			 * System.out.println(c); System.out.println("Current location " +
			 * currentLocation);
			 */
			com = getBestCommission(commissions, deliveries, load,
					Schedule.copy(schedule), currentLocation);

			if (com == null) {
				return null;
			}

			// System.out.println("BestCommission " + com.getID() + " "+
			// com.isPickup());

			schedule.addCommission(com, com.isPickup());
			if (com.isPickup()) {
				deliveries.add(com);
				load += com.getLoad();
				currentLocation = new Point2D.Double(com.getPickupX(),
						com.getPickupY());
			} else {
				load -= com.getLoad();
				currentLocation = new Point2D.Double(com.getDeliveryX(),
						com.getDeliveryY());
			}
			// TODO - wymiana zlecen
			if (canFullfieldCommissions(com, commissions, deliveries, schedule)
					.size() > 0) {

				return null;
			}

		} while (commissions.size() > 0 || deliveries.size() > 0);

		Point2D.Double lastLocation = schedule.getLastCommissionLocation();
		double distance = Helper.calculateDistance(lastLocation, depot);
		if (distance > deadlineTmp) {

			return null;
		}

		return schedule;
	}

	private void changeTimes(double distance, Commission commission,
			List<Commission> commissions) {
		double serviceTime;
		if (commission.isPickup()) {
			serviceTime = commission.getPickUpServiceTime();
			if (commission.getPickupTime1() >= distance) {
				distance = commission.getPickupTime1();
			}
		} else {
			serviceTime = commission.getDeliveryServiceTime();
			if (commission.getDeliveryTime1() >= distance) {
				distance = commission.getDeliveryTime1();
			}
		}

		for (Commission com : commissions) {
			com.setDeliveryTime1(com.getDeliveryTime1() - distance
					- serviceTime);
			com.setDeliveryTime2(com.getDeliveryTime2() - distance
					- serviceTime);
			com.setPickupTime1(com.getPickupTime1() - distance - serviceTime);
			com.setPickupTime2(com.getPickupTime2() - distance - serviceTime);
		}

		commission.setDeliveryTime1(commission.getDeliveryTime1() - distance
				- serviceTime);
		commission.setDeliveryTime2(commission.getDeliveryTime2() - distance
				- serviceTime);
		commission.setPickupTime1(commission.getPickupTime1() - distance
				- serviceTime);
		commission.setPickupTime2(commission.getPickupTime2() - distance
				- serviceTime);

		deadlineTmp = deadlineTmp - distance - serviceTime;
	}

	public Commission getBestCommission(List<Commission> commissions,
			List<Commission> deliveries, Integer load, Schedule schedule,
			Point2D.Double currentLocation) {
		Comparator<Commission> commissionsComparator;
		Comparator<Commission> deliveriesComparator;
		Comparator<Commission> comComparator;
		if (comparator == ComparatorType.time) {
			commissionsComparator = new TimeComparator(true, currentLocation);
			deliveriesComparator = new TimeComparator(false, currentLocation);
			comComparator = new TimeComparator(null, currentLocation);
		} else if (comparator == ComparatorType.distance) {
			commissionsComparator = new DistanceComparator(true,
					currentLocation);
			deliveriesComparator = new DistanceComparator(false,
					currentLocation);
			comComparator = new DistanceComparator(null, currentLocation);
		} else if (comparator == ComparatorType.mix) {
			commissionsComparator = new MixComparator(true, currentLocation);
			deliveriesComparator = new MixComparator(false, currentLocation);
			comComparator = new MixComparator(null, currentLocation);
		} else
			throw new IllegalArgumentException("Wrong comparator");

		Collections.sort(commissions, commissionsComparator);
		Collections.sort(deliveries, deliveriesComparator);
		if (commissions.size() == 0
				|| commissions.get(0).getLoad() + load > maxLoad) {

			if (deliveries.size() == 0)
				return null;
			Commission com = deliveries.remove(0);
			double dist;
			if (currentLocation != null) {
				dist = Helper.calculateDistance(
						currentLocation,
						new Point2D.Double(com.getDeliveryX(), com
								.getDeliveryY()));
			} else {
				dist = Helper.calculateDistance(
						schedule.getLastCommissionLocation(),
						new Point2D.Double(com.getDeliveryX(), com
								.getDeliveryY()));
			}

			com.setPickup(false);

			double tmp = deadlineTmp;
			changeTimes(dist, Commission.copy(com), commissions);
			deadlineTmp = tmp;
			changeTimes(dist, com, deliveries);

			return com;
		}

		Commission pickUp = null;
		if (commissions.size() > 0)
			pickUp = commissions.get(0);
		Commission delivery = null;
		if (deliveries.size() > 0)
			delivery = deliveries.get(0);

		Commission commission = null;
		double dist = 0.0;

		if (pickUp != null && delivery != null) {
			if (comComparator.compare(pickUp, delivery) < 0
					&& pickUp.getLoad() + load <= maxLoad) {
				commission = commissions.remove(0);
				commission.setPickup(true);
			} else {
				commission = deliveries.remove(0);
				commission.setPickup(false);
			}
		} else if (pickUp != null) {
			commission = commissions.remove(0);
			commission.setPickup(true);
		} else if (delivery != null) {
			commission = deliveries.remove(0);
			commission.setPickup(false);
		} else {
			return null;
		}

		if (currentLocation != null) {
			if (commission.isPickup())
				dist = Helper.calculateDistance(
						currentLocation,
						new Point2D.Double(commission.getPickupX(), commission
								.getPickupY()));
			else
				dist = Helper.calculateDistance(currentLocation,
						new Point2D.Double(commission.getDeliveryX(),
								commission.getDeliveryY()));
		} else {
			if (commission.isPickup())
				dist = Helper.calculateDistance(schedule
						.getLastCommissionLocation(), new Point2D.Double(
						commission.getPickupX(), commission.getPickupY()));
			else
				dist = Helper.calculateDistance(schedule
						.getLastCommissionLocation(), new Point2D.Double(
						commission.getDeliveryX(), commission.getDeliveryY()));
		}

		double tmp = deadlineTmp;
		changeTimes(dist, Commission.copy(commission), commissions);
		deadlineTmp = tmp;
		changeTimes(dist, commission, deliveries);
		return commission;
	}

	public Schedule canFullfieldCommissions(Commission commission,
			List<Commission> commissions, List<Commission> deliveries,
			Schedule schedule) {
		// Point2D.Double lastCommission=schedule.getLastCommissionLocation();

		if (schedule.calculateTime2(depot) < 0)
			return schedule;
		Schedule result = schedule.createSchedule(schedule.getAlgorithm());
		// double dist;
		for (Commission com : commissions) {
			// dist=Helper.calculateDistance(lastCommission, new
			// Point2D.Double(com.getPickupX(),com.getPickupY()));
			if (com.getPickupTime2() + commission.getPickUpServiceTime()/*-dist*/< 0) {
				result.addCommission(com, true);
			}
		}
		for (Commission com : deliveries) {
			// dist=Helper.calculateDistance(lastCommission, new
			// Point2D.Double(com.getDeliveryX(),com.getDeliveryY()));
			if (com.getDeliveryTime2() + commission.getDeliveryServiceTime()/*-dist*/< 0) {
				result.addCommission(com, false);
			}
		}
		return result;
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
