package algorithm.simmulatedTrading;

import java.util.HashMap;
import java.util.Map;

import algorithm.Algorithm;
import algorithm.Helper;
import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.jade.agentcalendar.AgentCalendarWithoutGraph;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.jade.eunit.LoadNotOkException;
import dtp.simmulation.SimInfo;

/**
 * Implements old (naive) approach to simulated trading. Not used anymore
 */
public class OldSTSimmulator {

	public static Map<Integer, AgentCalendarWithoutGraph> simmulatedTrading(
			Map<Integer, AgentCalendarWithoutGraph> holons, Integer holon,
			int STDepth) {
		Map<Integer, AgentCalendarWithoutGraph> backup = new HashMap<Integer, AgentCalendarWithoutGraph>();
		for (Integer key : holons.keySet()) {
			backup.put(key, holons.get(key).clone());
		}
		AgentCalendarWithoutGraph schedule = holons.get(holon);
		Commission worstCommission = schedule.getWorstCommission(0, STDepth);
		if (worstCommission == null)
			return holons;
		AgentCalendarWithoutGraph calendar;
		double bestCost = Double.MAX_VALUE;
		double extraDistance;
		int bestHolon = -1;
		boolean found = false;
		for (Integer holonId : holons.keySet()) {
			calendar = holons.get(holonId);
			if (calendar.addCommission(worstCommission, 0) == -1) {
				calendar.removeCommission(worstCommission);
				try {
					extraDistance = calendar.getExtraDistance(worstCommission,
							0);
					if (bestCost > Helper.getRatio(extraDistance,
							worstCommission)) {
						bestCost = Helper.getRatio(extraDistance,
								worstCommission);
						bestHolon = holonId;
					}
					found = true;
				} catch (LoadNotOkException e) {

				}
			}
		}
		if (found == false) {
			System.err.println("Can't add commission (ST) "
					+ worstCommission.getID() + " " + holon + " " + bestHolon);
			return backup;
			// System.exit(0);
		}

		if (holon == bestHolon)
			return simmulatedTrading(holons, bestHolon, STDepth + 1);
		calendar = holons.get(bestHolon);
		if (calendar.addCommission(worstCommission, 0) != -1) {
			System.err.println("Fatal error " + bestHolon + " "
					+ calendar.addCommission(worstCommission, 0));
			System.exit(0);
		}

		return simmulatedTrading(holons, bestHolon, 1);
	}

	/*
	 * Not used
	 */
	public static Map<Integer, Schedule> simmulatedTrading(
			AlgorithmAgentParent agent, Map<Integer, Schedule> holons,
			Integer holon, int STDepth, SimInfo info, Algorithm algorithm,
			String chooseWorstCommission, int timestamp) {
		Schedule schedule = holons.get(holon);
		Schedule backup = Schedule.copy(schedule);
		Commission worstCommission = schedule.getWorstCommission(0, STDepth,
				info, chooseWorstCommission);
		if (worstCommission == null)
			return holons;
		double bestCost = Double.MAX_VALUE;
		int bestHolon = 0;
		boolean added = false;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		double extraDistance;
		for (Integer key : holons.keySet()) {
			schedule = holons.get(key);
			tmpSchedule = algorithm
					.makeSchedule(agent, Commission.copy(worstCommission),
							null, schedule, timestamp);
			if (tmpSchedule != null) {
				extraDistance = tmpSchedule.getDistance(info.getDepot())
						- schedule.getDistance(info.getDepot());
				if (bestCost > Helper.getRatio(extraDistance, worstCommission)) {
					bestCost = Helper.getRatio(extraDistance, worstCommission);
					bestHolon = key;
					bestSchedule = tmpSchedule;
				}
				added = true;
			}
		}
		if (added == false) {
			holons.put(holon, backup);
			return holons;
		} else {
			holons.put(bestHolon, bestSchedule);
		}

		if (bestHolon == holon)
			return holons;
		return simmulatedTrading(agent, holons, bestHolon, STDepth, info,
				algorithm, chooseWorstCommission, timestamp);
	}
}
