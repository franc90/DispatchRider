package dtp.jade.agentcalendar;

import jade.core.AID;

import java.io.Serializable;
import java.util.List;

import algorithm.Schedule;

public class CalendarStats implements Serializable {

	private static final long serialVersionUID = 3185173025130927613L;

	private AID aid;

	private int capacity;

	private AID driverAID;
	private AID truckAID;
	private AID trailerAID;

	private Schedule schedule2;

	// MODIFY by LP
	private int trailer_mass;
	private int truck_power;
	private int truck_reliability;
	private int truck_comfort;
	private int truck_fuelConsumption;
	// end of modifications

	private double distance;

	private double cost;

	private double waitTime;

	private double driveTime;
	private double punishment;

	private List<CalendarAction> schedule;

	private boolean isDefault;
	private int maxSTDepth;

	public CalendarStats(AID aid) {

		maxSTDepth = 0;
		this.aid = aid;
		isDefault = false;
	}

	public double getDriveTime() {
		return driveTime;
	}

	public void setDriveTime(double driveTime) {
		this.driveTime = driveTime;
	}

	public double getPunishment() {
		return punishment;
	}

	public void setPunishment(double punishment) {
		this.punishment = punishment;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setMaxSTDepth(int STDepth) {
		maxSTDepth = STDepth;
	}

	public int getMaxSTDepth() {
		return maxSTDepth;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public AID getAID() {

		return aid;
	}

	public void setAID(AID aid) {

		this.aid = aid;
	}

	public double getDistance() {

		return distance;
	}

	public void setDistance(double distance) {

		this.distance = distance;
	}

	public double getWaitTime() {

		return waitTime;
	}

	public void setWaitTime(double waitTime) {

		this.waitTime = waitTime;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getCost() {
		return cost;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public AID getDriverAID() {
		return driverAID;
	}

	public void setDriverAID(AID driverAID) {
		this.driverAID = driverAID;
	}

	public AID getTruckAID() {
		return truckAID;
	}

	public void setTruckAID(AID truckAID) {
		this.truckAID = truckAID;
	}

	public AID getTrailerAID() {
		return trailerAID;
	}

	public void setTrailerAID(AID trailerAID) {
		this.trailerAID = trailerAID;
	}

	public void setSchedule(List<CalendarAction> schedule) {
		this.schedule = schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule2 = schedule;
	}

	public Schedule getSchedule2() {
		return schedule2;
	}

	public List<CalendarAction> getSchedule() {
		return schedule;
	}

	private long reorganizationTime;

	public void setReorganizationTime(long time) {
		reorganizationTime = time;
	}

	public long getReorganizationTime() {
		return reorganizationTime;
	}

	private long organizationTime;

	public void setOrganizationTime(long time) {
		organizationTime = time;
	}

	public long getOrganizationTime() {
		return organizationTime;
	}

	// Modification by LP
	/**
	 * @param mass
	 *            the mass to set
	 */
	public void setMass(int mass) {
		this.trailer_mass = mass;
	}

	/**
	 * @return the mass
	 */
	public int getMass() {
		return trailer_mass;
	}

	/**
	 * @param reliability
	 *            the reliability to set
	 */
	public void setReliability(int reliability) {
		this.truck_reliability = reliability;
	}

	/**
	 * @return the reliability
	 */
	public int getReliability() {
		return truck_reliability;
	}

	/**
	 * @param comfort
	 *            the comfort to set
	 */
	public void setComfort(int comfort) {
		this.truck_comfort = comfort;
	}

	/**
	 * @return the comfort
	 */
	public int getComfort() {
		return truck_comfort;
	}

	/**
	 * @param fuelComsuption
	 *            the fuelComsuption to set
	 */
	public void setFuelConsumption(int fuelConsumption) {
		this.truck_fuelConsumption = fuelConsumption;
	}

	/**
	 * @return the fuelComsuption
	 */
	public int getFuelConsumption() {
		return truck_fuelConsumption;
	}

	/**
	 * @param power
	 *            the power to set
	 */
	public void setPower(int power) {
		this.truck_power = power;
	}

	/**
	 * @return the power
	 */
	public int getPower() {
		return truck_power;
	}
}
