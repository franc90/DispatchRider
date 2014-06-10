package algorithm.AdaptiveLargeNeighbourhoodSearch.removal.method;

import jade.core.AID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import algorithm.Schedule;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import dtp.commission.Commission;

public class ShawRemoval implements RemovalMethod {

	//weights of particular terms 
	double timeWeight, distanceWeight, capacityWeight, requestsWeight;
	//commission that is used in computing relatedness
	Commission current;
	
	public ShawRemoval()
	{
		timeWeight = 1;
		distanceWeight = 1;
		capacityWeight = 1;	
	}
	
	@Override
	public List<Commission> Remove(Map<AID, Schedule> holons, int searchSize) {
		
		//don't change solutions that are too small
		//clear current
		current = null;
		//final result
		List<Commission> removedCommissions = new ArrayList<Commission>();
		//List of all commissions and number of commissions wanted
		List<Commission> commissions = new ArrayList<Commission>();
		
		//Get all commissions other than current and put them into commissions
		for (AID a  : holons.keySet()) 
		{
			for (Commission c : holons.get(a).getCommissions())
			{
				commissions.add(c);
			}
		}
				
		int before = commissions.size();
		//don't change solutions that are too small
		
		if (commissions.size() < 10)
		{
			return null;
		}
		
		int wantedSize = commissions.size() / 100 * searchSize;

		do
		{	
			
			
			Random r = new Random();
			Schedule sched = null;
			
			while (sched == null )
			{
				AID[] a = new AID[holons.size()];
				holons.keySet().toArray(a);
				sched = holons.get(a[r.nextInt(a.length)]);
				if (sched.size() < 2) sched = null;
				
			}
			
			boolean canBe = false;
			Commission comm = null;
			
			while (canBe == false)
			{
				comm = sched.getCommission(r.nextInt(sched.getCommissions().size()));
						
				for (Commission c : commissions)
				{	
					if (c.getID() == comm.getID())
					{
						canBe = true;
						break;
					}
				}
			}
			
			removedCommissions.add(comm);
			//setting current commission
			current = comm;
			
			//remove commissions that have already been chosen
			for (Commission c : commissions)
			{	
				if (c.getID() == comm.getID())
				{
					commissions.remove(c);
					break;
				}
			}
			
			//sort the list
			Collections.sort(commissions, comp);
			//parameter responsible for randomness
			double y = r.nextFloat();
			//index of chosen commission
			int index = (int)(Math.pow(y, r.nextInt(5) + 5) * commissions.size());
			
			removedCommissions.add(commissions.remove(index));

		} while (removedCommissions.size() < wantedSize);
		

		//remove chosen commissions
		for (Schedule s : holons.values()) 
			for (Commission c : removedCommissions)
				s.removeCommission(c);
		
		commissions.clear();

		//Get all commissions other than current and put them into commissions
		for (Schedule a : holons.values()) 
			for (Commission c : a.getCommissions())
				commissions.add(c);
		
		int after = commissions.size();
		if (before-after != removedCommissions.size()) System.out.println("WHYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
		return removedCommissions;
	}

	//Creating own comparator as a way to achieve a list sorted by relatedness
	public Comparator<Commission> comp = new Comparator<Commission>() {

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
		
	};
	
	//Method need in comparator
	private double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt((x2 -x1)*(x2-x1) + (y2- y1)*(y2-y1))  ;
	}
	

}
