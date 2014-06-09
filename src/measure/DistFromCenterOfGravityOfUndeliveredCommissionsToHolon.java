package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class DistFromCenterOfGravityOfUndeliveredCommissionsToHolon extends
		MeasureCalculator {

	private static final long serialVersionUID = 6048931190953812055L;

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
			measure.put(aid, MeasureHelper.distCenterOfGravityFromHolon(
					schedules.get(aid).getUndeliveredCommissions(
							info.getDepot(), timestamp), commissions, schedules
							.get(aid).getCurrentLocation()));
		}
		return measure;
	}

	@Override
	public String getName() {
		return "DistFromCenterOfGravityOfUndeliveredCommissionsToHolon";
	}

}
