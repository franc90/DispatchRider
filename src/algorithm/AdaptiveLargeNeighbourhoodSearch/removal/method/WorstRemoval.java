package algorithm.AdaptiveLargeNeighbourhoodSearch.removal.method;

import jade.core.AID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import algorithm.Helper;
import algorithm.Schedule;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import dtp.commission.Commission;

public class WorstRemoval implements RemovalMethod{

	
	private Map<AID, Schedule> tmpholons1;
	private Map<AID, Schedule> tmpholons2;
	
	@Override
	public List<Commission> Remove(Map<AID, Schedule> holons, int searchSize) {
		
		tmpholons1 = Helper.copyAID(holons);
		tmpholons2 = Helper.copyAID(holons);

		//final result
		List<Commission> removedCommissions = new ArrayList<Commission>();
		//List of all commissions and number of commissions wanted
		List<Commission> commissions = new ArrayList<Commission>();
	
		
		for (AID a : holons.keySet()) 
			for (Commission c : holons.get(a).getCommissions())
				commissions.add(c);
		if (commissions.size() < 10)
		{
			return null;
		}
		Collections.sort(commissions, comp);
	
		int wantedSize = commissions.size() / 100 * searchSize;
		
		
		do
		{	
			Random r = new Random();
			
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
		return removedCommissions;
	}
	
	//Creating own comparator
	public Comparator<Commission> comp = new Comparator<Commission>() {

		@Override
		public int compare(Commission com1, Commission com2) {
			
			for (AID a : tmpholons1.keySet()) 
				if (tmpholons1.get(a).getCommissions().contains(com1))
					tmpholons1.get(a).getCommissions().remove(com1);
			for (AID a : tmpholons2.keySet()) 
				if (tmpholons2.get(a).getCommissions().contains(com2))
					tmpholons2.get(a).getCommissions().remove(com2);
			double result1 = calculateSummaryCost(tmpholons1);
			double result2 = calculateSummaryCost(tmpholons2);
			return result1 < result2 ? -1 : (result1 == result2 ? 0 : 1);
		}
		
	};
	
	private static double calculateSummaryCost(Map<AID, Schedule> schedules) {
		double result = 0.0;
		for (AID aid : schedules.keySet())
			result += schedules.get(aid).calculateCost(schedules.get(aid).getAlgorithm().getSimInfo());
		return result;
	}
		
}
