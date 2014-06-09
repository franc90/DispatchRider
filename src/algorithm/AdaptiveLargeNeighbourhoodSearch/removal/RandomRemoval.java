package algorithm.AdaptiveLargeNeighbourhoodSearch.removal;

import jade.core.AID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import algorithm.Schedule;
import dtp.commission.Commission;

public class RandomRemoval implements RemovalMethod{

	@Override
	public List<Commission> Remove(Map<AID, Schedule> holons, int searchSize) {
		
		
		//final result
		List<Commission> removedCommissions = new ArrayList<Commission>();
		//List of all commissions and number of commissions wanted
		List<Commission> commissions = new ArrayList<Commission>();

		
		//Get random request from whole solution and add it to final result
		for (AID a : holons.keySet()) 
			for (Commission c : holons.get(a).getCommissions())
				commissions.add(c);
		if (commissions.size() < 10)
		{
			return null;
		}
		int wantedSize = commissions.size() / 100 * searchSize;
		do
		{	
			//Get all commissions other than current and put them into commissions
			
			
			Random r = new Random();
			Commission comm = commissions.get(r.nextInt(commissions.size()));
			removedCommissions.add(comm);
			commissions.remove(comm);
			
			

		} while (removedCommissions.size() < wantedSize);
		
		//remove chosen commissions
		for (Schedule s : holons.values()) 
			for (Commission c : removedCommissions)
				s.removeCommission(c);
		return removedCommissions;
	}

}
