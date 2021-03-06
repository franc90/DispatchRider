package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class StandardDeviationAllComsFromHolon extends MeasureCalculator {

	private static final long serialVersionUID = -601261484283339710L;

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
			measure.put(aid, MeasureHelper.standardDeviationComsFromHolon(
					schedules.get(aid).getCommissions(), commissions, schedules
							.get(aid).getCurrentLocation()));
		}
		return measure;
	}

	@Override
	public String getName() {
		return "StandardDeviationAllComsFromHolon";
	}

}
