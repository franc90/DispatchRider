package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class AverageNumberOfComsWithinTimeWinOfUndeliveredCommissions extends
		MeasureCalculator {

	private static final long serialVersionUID = 8602127002723190272L;

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
			measure.put(aid, MeasureHelper.averagenNumberOfComsWithinTimeWin(
					schedules.get(aid).getUndeliveredCommissions(
							info.getDepot(), timestamp), commissions));
		}
		return measure;
	}

	@Override
	public String getName() {
		return "AverageNumberOfComsWithinTimeWinOfUndeliveredCommissions ";
	}

}
