package algorithm;

import jade.core.AID;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import dtp.commission.Commission;
import dtp.simmulation.SimInfo;

public class BasicSchedule extends Schedule {

	private static final long serialVersionUID = -1591838319435718825L;

	private Point2D.Double nextLocation;
	private double waitTime = 0.0;
	private boolean waiting = false;
	private int previousId;

	public BasicSchedule(Algorithm algorithm) {
		super(algorithm);
	}

	public BasicSchedule(Algorithm algorithm, int currentCommission,
			double creationTime) {
		super(algorithm, currentCommission, creationTime);
	}

	@Override
	protected Schedule copySpecificFields(Schedule result) {
		BasicSchedule basic = (BasicSchedule) result;
		basic.nextLocation = this.nextLocation;
		basic.waitTime = this.waitTime;
		basic.waiting = this.waiting;
		basic.previousId = this.previousId;
		return basic;
	}

	@Override
	protected double calculateDistance(Double point1, Double point2) {
		return Helper.calculateDistance(point1, point2);
	}

	@Override
	protected double calculateTime(Double point1, Double point2, Double depot) {
		return calculateDistance(point1, point2);
	}

	@Override
	public Schedule createSchedule(Algorithm algorithm) {
		return new BasicSchedule(algorithm);
	}

	@Override
	public Schedule createSchedule(Algorithm algorithm, int currentCommission,
			double creationTime) {
		return new BasicSchedule(algorithm, currentCommission, creationTime);
	}

	@Override
	public void initSchedule(Schedule schedule) {
		schedule.copySpecificFields(this);
		this.currentCommission = schedule.currentCommission;
		this.currentLocation = schedule.currentLocation;
	}

	@Override
	public void updateCurrentLocation(int timestamp, Point2D.Double depot,
			AID aid) {
		if (currentCommission == null) {
			currentCommission = getCommission(0);
			if (currentCommission.isPickup())
				nextLocation = new Point2D.Double(
						currentCommission.getPickupX(),
						currentCommission.getPickupY());
			else
				nextLocation = new Point2D.Double(
						currentCommission.getDeliveryX(),
						currentCommission.getDeliveryY());
		}
		if (currentCommission.getID() == -1) {
			waiting = true;
			waitTime = 0;
		}

		if (waiting) {
			waitTime--;
			if (waitTime <= 0) {
				waiting = false;
				waitTime = 0;
				int id;
				if (currentCommission.isPickup())
					id = currentCommission.getPickUpId();
				else
					id = currentCommission.getDeliveryId();
				if (currentCommission.getID() == -1)
					id = previousId;
				int i;
				Commission com;
				for (i = 0; i < getAllCommissions().size(); i++) {
					com = getCommission(i);
					if (isPickup(i) && id == com.getPickUpId())
						break;
					else if (isPickup(i) == false && id == com.getDeliveryId())
						break;
				}
				if (i + 1 >= getAllCommissions().size()) {
					com = new Commission();
					com.setID(-1);
					previousId = id;
					currentCommission = com;
				} else {
					currentCommission = getCommission(i + 1);
					if (currentCommission.isPickup())
						nextLocation = new Point2D.Double(
								currentCommission.getPickupX(),
								currentCommission.getPickupY());
					else
						nextLocation = new Point2D.Double(
								currentCommission.getDeliveryX(),
								currentCommission.getDeliveryY());
				}
				if (currentCommission.getID() == -1)
					waiting = true;
			}
		} else {
			double z = Helper.calculateDistance(currentLocation, nextLocation)
					- waitTime;
			waitTime = 0;
			waiting = false;
			if (z <= 1) {
				currentLocation = nextLocation;
				double dif;
				if (currentCommission.isPickup())
					dif = currentCommission.getPickupTime1() - timestamp;
				else
					dif = currentCommission.getDeliveryTime1() - timestamp;
				if (dif < 0)
					dif = 0;
				if (currentCommission.isPickup())
					waitTime = dif - (1 - z)
							+ currentCommission.getPickUpServiceTime();
				else
					waitTime = dif - (1 - z)
							+ currentCommission.getDeliveryServiceTime();
				waiting = true;
			} else {
				double newX = currentLocation.getX()
						+ (nextLocation.getX() - currentLocation.getX()) / z;
				double newY = currentLocation.getY()
						+ (nextLocation.getY() - currentLocation.getY()) / z;
				currentLocation = new Point2D.Double(newX, newY);
			}
		}

		setCurrentLocation(currentLocation);
	}

	@Override
	public double calculateDriveTime(SimInfo info) {
		return getDistance(info.getDepot());
	}
}
