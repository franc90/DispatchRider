package algorithm.AdaptiveLargeNeighbourhoodSearch.removal.method.shawcomparators;


import dtp.commission.Commission;

public class AllComparator implements CommissionComparator {
	
	double timeWeight, distanceWeight, capacityWeight;
	
	private Commission current;

	public AllComparator(double timeWeight, double distanceWeight, double capacityWeight){
		this.timeWeight = timeWeight;
		this.distanceWeight = distanceWeight;
		this.capacityWeight = capacityWeight;			
	}
	
	public void setCurrent(Commission current){
		this.current = current;
	}
	
	@Override
	public int compare(Commission com1, Commission com2) {
		
		//parts of final relatedness of com1
		double distanceTerm1, timeTerm1, capacityTerm1;
		timeTerm1 = timeWeight * (Math.abs(com1.getPickupTime1() - current.getPickupTime1())
										+ Math.abs(com1.getDeliveryTime1() - current.getDeliveryTime1() ));
		distanceTerm1 = distanceWeight * ((getDistance(com1.getPickupX(),com1.getPickupY(), current.getPickupX(), current.getPickupY()))
				+ (getDistance(com1.getDeliveryX(),com1.getDeliveryY(), current.getDeliveryX(), current.getDeliveryY())));
		capacityTerm1 = capacityWeight * (Math.abs(com1.getLoad()-current.getLoad()));
		
		//parts of final relatedness of com2
		double distanceTerm2, timeTerm2, capacityTerm2;
		timeTerm2 = timeWeight * (Math.abs(com2.getPickupTime1() - current.getPickupTime1())
										+ Math.abs(com2.getDeliveryTime1() - current.getDeliveryTime1() ));
		distanceTerm2 = distanceWeight * ((getDistance(com2.getPickupX(),com2.getPickupY(), current.getPickupX(), current.getPickupY()))
				+ (getDistance(com2.getDeliveryX(),com2.getDeliveryY(), current.getDeliveryX(), current.getDeliveryY())));
		capacityTerm2 = capacityWeight * (Math.abs(com2.getLoad()-current.getLoad()));
		
		double result1 = timeTerm1 + distanceTerm1 + capacityTerm1;
		double result2 = timeTerm2 + distanceTerm2 + capacityTerm2;
		
		return result1 < result2 ? -1 : (result1 == result2 ? 0 : 1);
	}
	
	//Method need in comparator
	private double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt((x2 -x1)*(x2-x1) + (y2- y1)*(y2-y1))  ;
	}
}
