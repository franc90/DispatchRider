package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class NumberOfCommissionsWeCanAddToOthersBeforeChanges extends
		MeasureCalculator {

	private static final long serialVersionUID = 4189738608398943958L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {
		return MeasureHelper.numberOfCommissionsWeCanAddToOthers(agent,
				oldSchedules, timestamp);
	}

	@Override
	public String getName() {
		return "NumberOfCommissionsWeCanAddToOthersBeforeChanges";
	}

}
