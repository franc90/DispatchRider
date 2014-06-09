package algorithm;

import java.awt.geom.Point2D;
import java.util.Comparator;

import dtp.commission.Commission;

public class TimeComparator2 implements Comparator<Commission> {
	private Boolean pickups;
	private Point2D.Double curretLocation;
	public TimeComparator2(Boolean pickups, Point2D.Double currentLocation) {
		this.pickups=pickups;
		this.curretLocation=currentLocation;
	}
	
	public int compare(Commission com1, Commission com2) {
		double dist1;
		double dist2;
		double wait1;
		double wait2;
		if(pickups==null) {
			dist1=Helper.calculateDistance(curretLocation, new Point2D.Double(com1.getPickupX(),com1.getPickupY()));
			dist2=Helper.calculateDistance(curretLocation, new Point2D.Double(com2.getDeliveryX(),com1.getDeliveryY()));
			wait1=com1.getPickupTime1()-dist1;
			wait2=com2.getDeliveryTime1()-dist2;
			if(wait1<=wait2) return -1;
			else return 1;
		}
		if(pickups) {
			dist1=Helper.calculateDistance(curretLocation, new Point2D.Double(com1.getPickupX(),com1.getPickupY()));
			dist2=Helper.calculateDistance(curretLocation, new Point2D.Double(com2.getPickupX(),com1.getPickupY()));
			wait1=com1.getPickupTime1()-dist1;
			wait2=com2.getPickupTime1()-dist2;
			if(wait1<=wait2) return -1;
			else return 1;		
		} else {
			dist1=Helper.calculateDistance(curretLocation, new Point2D.Double(com1.getDeliveryX(),com1.getDeliveryY()));
			dist2=Helper.calculateDistance(curretLocation, new Point2D.Double(com2.getDeliveryX(),com1.getDeliveryY()));
			wait1=com1.getDeliveryTime1()-dist1;
			wait2=com2.getDeliveryTime1()-dist2;
			if(wait1<=wait2) return -1;
			else return 1;
		}
	}
}
