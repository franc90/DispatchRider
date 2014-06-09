package algorithm.AdaptiveLargeNeighbourhoodSearch;

import jade.core.AID;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import algorithm.Helper;
import algorithm.Schedule;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

public class AdaptiveLargeNeighbourhoodTrading {

	public static Map<AID, Schedule> LNSTrading(
			Set<AID> aids,	Map<AID, Schedule> holons,
			SimInfo info, int iterations, int searchSize ,
			AlgorithmAgentParent agent, int timestamp) {



		Map<AID, Schedule> tmpSolution;
		Map<AID, Schedule> bestSolution = Helper.copyAID(holons);
		double bestResultCost = calculateSummaryCost(bestSolution, info);
		Random r = new Random();
		int newSearchSize = searchSize + (int)((r.nextFloat() - 0.5) * searchSize); 
		for (int i = 0; i < iterations; i++)
		{
			tmpSolution = Helper.copyAID(holons);
			RemovalMethod rm = RouletteWheel.getRemovalMethod();
			List<Commission> commissions = rm.Remove(tmpSolution, newSearchSize);
			if (commissions == null)
				continue;

			InsertMethod im = RouletteWheel.getInsertMethod();
			if (! im.Insert(tmpSolution, commissions, newSearchSize, agent,timestamp))
				continue;
			
			double cost = calculateSummaryCost(tmpSolution,info);
			if (cost < bestResultCost) {
				System.out.println("ALNS found better soultion");
				bestSolution = Helper.copyAID(tmpSolution);
				bestResultCost = cost;

				
			}
			
		}
		
		return bestSolution;
	}
	
	private static double calculateSummaryCost(Map<AID, Schedule> schedules,
			SimInfo info) {
		double result = 0.0;
		for (AID aid : schedules.keySet())
			result += schedules.get(aid).calculateSummaryCost(info);
		return result;
	}
	
}
