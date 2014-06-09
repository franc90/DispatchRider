package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class AverageDistPerCommissionBeforeChange extends MeasureCalculator {

	private static final long serialVersionUID = 7605379739031077878L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {

		return MeasureHelper
				.averageDistToCarryOneCommission(oldSchedules, info);
	}

	@Override
	public String getName() {
		return "AverageDistPerCommissionBeforeChange";
	}

}
