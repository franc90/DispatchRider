package algorithm.AdaptiveLargeNeighbourhoodSearch.insert;

import jade.core.AID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.Algorithm;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class RegretInsert implements InsertMethod {

	@Override
	public boolean Insert(Map<AID, Schedule> holons, List<Commission> commissions,
			int searchSize, AlgorithmAgentParent agent, int timestamp) 
	{


		Map<AID, Schedule> tmpSolution = holons;
		
		Map<Integer, Double> regretMap = new HashMap<Integer, Double>(); 
		Map<Integer, AID> regretHelper = new HashMap<Integer, AID>();
		
		int commissionsToPut = commissions.size();
		
		while (commissionsToPut > 0)
		{
			boolean commissionPut = false;
			int commissionPutID = -1;
			
			for (Commission c : commissions)
			{
				
				AID bestHolon = null;
				double leastCost = -1;
				AID secondBestHolon = null;
				double secondLeastCost = -1;
				
				for (AID a : tmpSolution.keySet())
				{
					Schedule sched  = holons.get(a);
					double newCost;
					Schedule tmp =  Schedule.copy(sched);
					Algorithm alg = tmp.getAlgorithm();
					Schedule newSched = alg.makeSchedule(agent, c, null, tmp, timestamp);
					if (newSched != null)
					{
						newCost = newSched.calculateCost(newSched.getAlgorithm().getSimInfo());
						if (leastCost == -1)
						{
							leastCost = newCost; 
							bestHolon = a;
						}
						else if ( secondLeastCost == -1)
						{
							secondLeastCost = newCost;
							secondBestHolon = a;
						}
						else
						{
							if (leastCost > newCost)
							{
								secondBestHolon = bestHolon;
								secondLeastCost = leastCost;
								leastCost = newCost;
								bestHolon = a;
							}
							else if (secondLeastCost > newCost)
							{
								secondBestHolon = a;
								secondLeastCost = newCost;
							}
						}
					}
					
					
				}
				
				if (bestHolon != null && secondBestHolon == null)
				{
					Schedule newSched = tmpSolution.get(bestHolon).getAlgorithm().makeSchedule(agent, c, null, tmpSolution.get(bestHolon), timestamp);
					tmpSolution.put(bestHolon, newSched);
					commissionPut = true;
					commissionPutID = c.getID();
					
				}
				else if (bestHolon != null && secondBestHolon != null)
				{
					regretMap.put(c.getID(), Math.abs(leastCost - secondLeastCost));
					regretHelper.put(c.getID(), bestHolon);
				}
				else return false;
				
				
			}
			
			commissionsToPut--;
			if (commissionPut)
			{
				int tmpIndex = -1;
				for (int i =0 ; i < commissions.size(); i++)
					if (commissions.get(i).getID() == commissionPutID)
					{
						tmpIndex = i;
						
					}
				commissions.remove(tmpIndex);
				continue;
			}
			
			int bestCom = -1;
			Double bestVal = 0.0d;
			
			for (int id : regretMap.keySet())
			{
				if (bestCom == -1)
				{
					bestCom = id;
					bestVal = regretMap.get(id);
				}
				else
				{
					if ( regretMap.get(id) > bestVal )
					{
						bestVal = regretMap.get(id);
						bestCom = id;
					}
				}
			}

			Schedule newSched = null;
			for (Commission c : commissions)
				if (c.getID() == bestCom) 
				{	
					Schedule tmp =  Schedule.copy(tmpSolution.get(regretHelper.get(bestCom)));
					Algorithm alg = tmp.getAlgorithm();
					newSched = alg.makeSchedule(agent, c, null, tmpSolution.get(regretHelper.get(bestCom)), timestamp);
					
				}
			
			if (newSched == null) return false;
			int indexToRemove = 0;
			for (int i =0 ; i < commissions.size(); i++)
				if (commissions.get(i).getID() == bestCom)
				{
					indexToRemove = i;
					
				}
			commissions.remove(indexToRemove);

			tmpSolution.put(regretHelper.get(bestCom),newSched);
		}
		return true;
	}

}
