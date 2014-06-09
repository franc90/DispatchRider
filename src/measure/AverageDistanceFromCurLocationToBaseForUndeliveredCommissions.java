package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class AverageDistanceFromCurLocationToBaseForUndeliveredCommissions
		extends MeasureCalculator {

	private static final long serialVersionUID = -7229687027435879938L;

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
					.averageDistanceFromCurLocationToBase(
							schedules.get(aid).getUndeliveredCommissions(
									info.getDepot(), timestamp), commissions,
							info.getDepot(), schedules.get(aid)
									.getCurrentLocation()));
		}
		return measure;
	}

	@Override
	public String getName() {
		return "AverageDistanceFromCurLocationToBaseForUndeliveredCommissions";
	}

}
