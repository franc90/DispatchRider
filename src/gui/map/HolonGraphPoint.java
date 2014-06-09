package gui.map;

import dtp.graph.GraphPoint;

public class HolonGraphPoint extends GraphPoint {

	private static final long serialVersionUID = 1L;
	private Integer holonID;
	private Integer holonCreationTime, truckComfort, trailerCapacity;
	private String driver;

	public HolonGraphPoint(double xVal, double yVal) {
		super(xVal, yVal);
	}

	public Integer getHolonID() {
		return holonID;
	}

	public void setHolonID(Integer holonID) {
		this.holonID=holonID;		
	}

	public void setHolonCreationTime(Integer holonCreationTime) {
		this.holonCreationTime=holonCreationTime;		
	}

	public void setDriver(String driver) {
this.driver=driver;		
	}

	public Integer getTruckComfort() {
		return truckComfort;
	}

	public void setTruckComfort(Integer truckCapacity) {
		this.truckComfort = truckCapacity;
	}

	public Integer getTrailerCapacity() {
		return trailerCapacity;
	}

	public void setTrailerCapacity(Integer trailerCapacity) {
		this.trailerCapacity = trailerCapacity;
	}

	public Integer getHolonCreationTime() {
		return holonCreationTime;
	}

	public String getDriver() {
		return driver;
	}
}
