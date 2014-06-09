package algorithm;

import java.awt.geom.Point2D;
import java.util.Comparator;

import dtp.commission.Commission;

public class MixComparator implements Comparator<Commission> {
	// private final Boolean pickups;
	// private final Point2D.Double currentLocation;

	public MixComparator(Boolean pickups, Point2D.Double currentLocation) {
		// this.pickups = pickups;
		// this.currentLocation = currentLocation;
		throw new UnsupportedClassVersionError("Brak implementacji");
	}

	public int compare(Commission com1, Commission com2) {
		// double dist1;
		// double dist2;
		// if(pickups==null) {
		// dist1=Helper.calculateDistance(currentLocation, new
		// Point2D.Double(com1.getPickupX(), com1.getPickupY()));
		// dist2=Helper.calculateDistance(currentLocation, new
		// Point2D.Double(com2.getDeliveryX(),com2.getDeliveryY()));
		// if(com1.getPickupTime1() < com2.getDeliveryTime1()) {
		// if(dist2<=dist1) {
		// //spr czy po wykonaniu com2 mozna dalej wykonac com1
		// double time=dist2+com2.getServiceTime()+Helper.calculateDistance(new
		// Point2D.Double(com1.getPickupX(), com1.getPickupY()), new
		// Point2D.Double(com2.getDeliveryX(),com2.getDeliveryY()));
		// if(com1.getPickupTime2()-time>=0) return 1;
		// else return -1;
		// }
		// else return -1;
		// } else {
		// if(dist1<=dist2) {
		// //spr czy po wykonaniu com2 mozna dalej wykonac com1
		// double time=dist1+com1.getServiceTime()+Helper.calculateDistance(new
		// Point2D.Double(com1.getPickupX(), com1.getPickupY()), new
		// Point2D.Double(com2.getDeliveryX(),com2.getDeliveryY()));
		// if(com2.getDeliveryTime2()-time>=0) return -1;
		// else return 1;
		// } else return 1;
		// }
		// }
		// else if(pickups) {
		// dist1=Helper.calculateDistance(currentLocation, new
		// Point2D.Double(com1.getPickupX(), com1.getPickupY()));
		// dist2=Helper.calculateDistance(currentLocation, new
		// Point2D.Double(com2.getPickupX(),com2.getPickupY()));
		// if(com1.getPickupTime1() < com2.getPickupTime1()) {
		// if(dist2<=dist1) {
		// //spr czy po wykonaniu com2 mozna dalej wykonac com1
		// double time=dist2+com2.getServiceTime()+Helper.calculateDistance(new
		// Point2D.Double(com1.getPickupX(), com1.getPickupY()), new
		// Point2D.Double(com2.getPickupX(),com2.getPickupY()));
		// if(com1.getPickupTime2()-time>=0) return 1;
		// else return -1;
		// }
		// else return -1;
		// } else {
		// if(dist1<=dist2) {
		// //spr czy po wykonaniu com2 mozna dalej wykonac com1
		// double time=dist1+com1.getServiceTime()+Helper.calculateDistance(new
		// Point2D.Double(com1.getPickupX(), com1.getPickupY()), new
		// Point2D.Double(com2.getPickupX(),com2.getPickupY()));
		// if(com2.getPickupTime2()-time>=0) return -1;
		// else return 1;
		// }
		// else return 1;
		// }
		// } else {
		// dist1=Helper.calculateDistance(currentLocation, new
		// Point2D.Double(com1.getDeliveryX(), com1.getDeliveryY()));
		// dist2=Helper.calculateDistance(currentLocation, new
		// Point2D.Double(com2.getDeliveryX(),com2.getDeliveryY()));
		// if(com1.getDeliveryTime1() < com2.getDeliveryTime1()) {
		// if(dist2<=dist1) {
		// //spr czy po wykonaniu com2 mozna dalej wykonac com1
		// double time=dist2+com2.getServiceTime()+Helper.calculateDistance(new
		// Point2D.Double(com1.getDeliveryX(), com1.getDeliveryY()), new
		// Point2D.Double(com2.getDeliveryX(),com2.getDeliveryY()));
		// if(com1.getDeliveryTime2()-time>=0) return 1;
		// else return -1;
		// }
		// else return -1;
		// } else {
		// if(dist1<=dist2) {
		// //spr czy po wykonaniu com2 mozna dalej wykonac com1
		// double time=dist1+com1.getServiceTime()+Helper.calculateDistance(new
		// Point2D.Double(com1.getDeliveryX(), com1.getDeliveryY()), new
		// Point2D.Double(com2.getDeliveryX(),com2.getDeliveryY()));
		// if(com2.getDeliveryTime2()-time>=0) return -1;
		// else return 1;
		// }
		// else return 1;
		// }
		// }
		return 0;
	}
}
