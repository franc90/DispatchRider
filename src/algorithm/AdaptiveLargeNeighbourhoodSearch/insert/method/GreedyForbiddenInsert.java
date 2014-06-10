package algorithm.AdaptiveLargeNeighbourhoodSearch.insert.method;

import jade.core.AID;

import java.util.List;
import java.util.Map;

import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import algorithm.Algorithm;
import algorithm.Schedule;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;

public class GreedyForbiddenInsert implements InsertMethod {

	@Override
	public boolean Insert(Map<AID, Schedule> holons,
			List<Commission> commissions, int searchSize,
			AlgorithmAgentParent agent, int timestamp) {

		for (Commission commision : commissions) {
			AID bestHolon = null;
			double leastCost = -1;

			for (AID aid : holons.keySet()) {
				Schedule schedule = holons.get(aid);
				double newCost;
				Schedule sheduleCopy = Schedule.copy(schedule);
				Algorithm algorithm = sheduleCopy.getAlgorithm();
				Schedule newSchedule = algorithm.makeSchedule(agent, commision, null, sheduleCopy, timestamp);

				// ForbiddenInsert
				if (schedule.equals(commision.getOldSchedule())) {
					continue;
				}

				if (newSchedule != null) {
					newCost = newSchedule.calculateCost(newSchedule.getAlgorithm().getSimInfo());
					if (leastCost == -1) {
						leastCost = newCost;
						bestHolon = aid;
					} else {
						if (leastCost > newCost) {
							leastCost = newCost;
							bestHolon = aid;
						}
					}
				}
			}

			if (bestHolon != null) {
				Schedule newSched = holons
						.get(bestHolon)
						.getAlgorithm()
						.makeSchedule(agent, commision, null, holons.get(bestHolon), timestamp);
				holons.put(bestHolon, newSched);
			} else {
				return false;
			}
		}

		return true;
	}

}
