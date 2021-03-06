package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class StandardDeviationOfMinDistBetweenUndeliveredCommissions extends
		MeasureCalculator {

	private static final long serialVersionUID = -3262692939893345138L;

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
					.standardDeviationMinDistBetweenCommissions(
							schedules.get(aid).getUndeliveredCommissions(
									info.getDepot(), timestamp), commissions));
		}
		return measure;
	}

	@Override
	public String getName() {
		return "StandardDeviationOfMinDistBetweenUndeliveredCommissions";
	}

}
