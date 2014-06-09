package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class StandardDeviationOfDistanceFromCurLocationToBaseFromAllCommissions
		extends MeasureCalculator {

	private static final long serialVersionUID = -1166805336260138608L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {
		Measure measure = new Measure();
		Map<AID, Schedule> schedules;
		if (newSchedules == null)
			schedules = oldSchedules;
		else
			schedules = newSchedules;
		for (AID aid : schedules.keySet()) {
			measure.put(aid, MeasureHelper
					.standardDeviationDistanceFromCurLocationToBase(schedules
							.get(aid).getCommissions(), commissions, info
							.getDepot(), schedules.get(aid)
							.getCurrentLocation()));
		}
		return measure;
	}

	@Override
	public String getName() {
		return "StandardDeviationOfDistanceFromCurLocationToBaseFromAllCommissions";
	}

}
