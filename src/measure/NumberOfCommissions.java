package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class NumberOfCommissions extends MeasureCalculator {

	private static final long serialVersionUID = -5383728706373220007L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {

		Measure result = new Measure();
		Map<AID, Schedule> schedules;
		if (newSchedules != null)
			schedules = newSchedules;
		else
			schedules = oldSchedules;
		for (AID aid : schedules.keySet())
			result.put(aid, new Double(schedules.get(aid).size() / 2));
		return result;
	}

	@Override
	public String getName() {
		return "NumberOfCommissions";
	}

}
