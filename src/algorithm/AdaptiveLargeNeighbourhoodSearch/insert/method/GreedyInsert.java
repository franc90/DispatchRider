package algorithm.AdaptiveLargeNeighbourhoodSearch.insert.method;

import jade.core.AID;

import java.util.List;
import java.util.Map;

import algorithm.Algorithm;
import algorithm.Schedule;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class GreedyInsert implements InsertMethod{

	@Override
	public boolean Insert(Map<AID, Schedule> holons, List<Commission> commissions,
			int searchSize, AlgorithmAgentParent agent,  int timestamp) {
		
		Map<AID, Schedule> tmpSolution = holons;
		
		for (Commission c : commissions)
		{
			AID bestHolon = null;
			double leastCost = -1;
			
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
					else
					{
						if (leastCost > newCost)
						{
							leastCost = newCost;
							bestHolon = a;
						}
					}
				}
				
			}
			
			if (bestHolon != null)
			{
				Schedule newSched = tmpSolution.get(bestHolon).getAlgorithm().makeSchedule(agent, c, null, tmpSolution.get(bestHolon), timestamp);
				tmpSolution.put(bestHolon, newSched);
			}
			else return false;
			
		}
		return true;
		
	}

}
