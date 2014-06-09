package dtp.jade.transport;

import jade.core.AID;

import java.io.Serializable;

public class NewHolonOffer implements Serializable, Comparable<NewHolonOffer> {
	private static final long serialVersionUID = 1L;
	private AID truck;
	private AID trailer;
	private AID driver;
	private boolean isValid;
	private TransportElementInitialDataTrailer trailerData;
	private TransportElementInitialDataTruck truckData;
	private TransportElementInitialData driverData;
	
	public int compareTo(NewHolonOffer offer) {
		if(isValid()==false && offer.isValid()==false) return 0;
		if(offer.isValid()==false) return 1;
		if(isValid()==false) return -1;
		if(truck.equals(offer.getTruck()) && trailer.equals(offer.getTrailer()) && driver.equals(offer.getDriver())) return 0;
		return driver.compareTo(offer.getDriver());
	}
	
	public NewHolonOffer(AID truck, AID trailer, AID driver, TransportElementInitialDataTrailer trailerData, TransportElementInitialDataTruck truckData, TransportElementInitialData driverData) {
		this.truck=truck;
		this.trailer=trailer;
		this.driver=driver;
		isValid=true;
		this.trailerData=trailerData;
		this.truckData=truckData;
		this.driverData=driverData;
	}
	
	public NewHolonOffer() {
		isValid=false;
	}
	
	public TransportElementInitialData getDriverData() {
		return driverData;
	}
	
	public TransportElementInitialDataTrailer getTrailerData() {
		return trailerData;
	}
	
	public TransportElementInitialDataTruck getTruckData() {
		return truckData;
	}
	
	public boolean isValid() {
		if(isValid==false) return false;
		if(truck==null) return false;
		if(trailer==null) return false;
		if(driver==null) return false;
		return true;
	}
	
	public AID getTruck() {
		return truck;
	}
	public void setTruck(AID truck) {
		this.truck = truck;
	}
	public AID getTrailer() {
		return trailer;
	}
	public void setTrailer(AID trailer) {
		this.trailer = trailer;
	}
	public AID getDriver() {
		return driver;
	}
	public void setDriver(AID driver) {
		this.driver = driver;
	}
}
