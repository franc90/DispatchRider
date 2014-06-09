package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class WaitTime extends MeasureCalculator {

	private static final long serialVersionUID = -856271252570034218L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {
		Map<AID, Schedule> schedules;
		if (newSchedules == null)
			schedules = oldSchedules;
		else
			schedules = newSchedules;
		Measure result = new Measure();
		for (AID aid : schedules.keySet())
			result.put(aid,
					schedules.get(aid).calculateWaitTime(info.getDepot()));
		return result;
	}

	@Override
	public String getName() {
		return "WaitTime";
	}

}
