package algorithm.AdaptiveLargeNeighbourhoodSearch.insert.method;

import jade.core.AID;

import java.util.List;
import java.util.Map;
import java.util.Random;

import algorithm.Algorithm;
import algorithm.Schedule;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

/**
 * Regular greedy insertion. Only difference is that its cost is perturbed by
 * factor d in [0.8, 1.2] to introduce randomization.
 */
public class GreedyPerturbedInsert implements InsertMethod {

	@Override
	public boolean Insert(Map<AID, Schedule> holons,
			List<Commission> commissions, int searchSize,
			AlgorithmAgentParent agent, int timestamp) {

		Map<AID, Schedule> tmpSolution = holons;

		for (Commission commision : commissions) {
			AID bestHolon = null;
			double leastCost = -1;

			for (AID a : tmpSolution.keySet()) {
				Schedule schedule = holons.get(a);
				double newCost;
				Schedule tmp = Schedule.copy(schedule);
				Algorithm alg = tmp.getAlgorithm();
				Schedule newSched = alg.makeSchedule(agent, commision, null,
						tmp, timestamp);
				if (newSched != null) {
					newCost = perturb(newSched.calculateCost(newSched
							.getAlgorithm().getSimInfo()));
					if (leastCost == -1) {
						leastCost = newCost;
						bestHolon = a;
					} else {
						if (leastCost > newCost) {
							leastCost = newCost;
							bestHolon = a;
						}
					}
				}

			}

			if (bestHolon != null) {
				Schedule newSched = tmpSolution
						.get(bestHolon)
						.getAlgorithm()
						.makeSchedule(agent, commision, null,
								tmpSolution.get(bestHolon), timestamp);
				tmpSolution.put(bestHolon, newSched);
			} else
				return false;

		}
		return true;
	}

	private double perturb(double cost) {
		Random random = new Random();

		double factor = (random.nextDouble() * 0.4) + 0.8;
		return cost * factor;
	}

}
