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
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.method.shawcomparators.CommissionComparator;
import dtp.commission.Commission;

public class ShawRemoval implements RemovalMethod {

	//commission that is used in computing relatedness
	Commission current;
	
	public ShawRemoval(CommissionComparator comp)
	{
		this.comp = comp;
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
		for (AID aid : holons.keySet()) {
			Schedule schedule = holons.get(aid);
			for (Commission c : schedule.getCommissions()) {
				c.setOldSchedule(schedule);
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
				comm.setOldSchedule(sched);
						
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
			comp.setCurrent(current);
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
	public CommissionComparator comp;

}
