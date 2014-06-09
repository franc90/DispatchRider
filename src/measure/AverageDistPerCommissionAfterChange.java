package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class AverageDistPerCommissionAfterChange extends MeasureCalculator {

	private static final long serialVersionUID = 3698345909236756449L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {

		return MeasureHelper
				.averageDistToCarryOneCommission(newSchedules, info);
	}

	@Override
	public String getName() {
		return "AverageDistPerCommissionBeforeChange";
	}

}
