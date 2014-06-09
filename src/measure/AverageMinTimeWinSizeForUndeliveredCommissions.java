package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class AverageMinTimeWinSizeForUndeliveredCommissions extends
		MeasureCalculator {

	private static final long serialVersionUID = 2374323605994609245L;

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
			measure.put(aid, MeasureHelper.averageMinTimeWindowsSize(
					schedules.get(aid).getUndeliveredCommissions(
							info.getDepot(), timestamp), commissions));
		}
		return measure;
	}

	@Override
	public String getName() {
		return "AverageMinTimeWinSizeForUndeliveredCommissions";
	}

}
