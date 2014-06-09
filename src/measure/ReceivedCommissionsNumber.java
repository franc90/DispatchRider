package measure;

import jade.core.AID;

import java.util.Map;

import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public class ReceivedCommissionsNumber extends MeasureCalculator {

	private static final long serialVersionUID = -3903168968798688699L;

	@Override
	public Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent) {
		Measure result = new Measure();

		if (newSchedules == null)
			return result;

		Schedule oldSchedule;
		Schedule newSchedule;
		boolean found;
		int numberOfReceivedCommissions;
		for (AID aid : newSchedules.keySet()) {
			oldSchedule = oldSchedules.get(aid);
			newSchedule = newSchedules.get(aid);
			numberOfReceivedCommissions = 0;
			for (Commission com : newSchedule.getCommissions()) {
				found = false;
				for (Commission com2 : oldSchedule.getCommissions()) {
					if (com.getID() == com2.getID()) {
						found = true;
						break;
					}
				}
				if (found == false)
					numberOfReceivedCommissions++;
			}
			result.put(aid, 1.0 * numberOfReceivedCommissions);
		}
		return result;
	}

	@Override
	public String getName() {
		return "ReceivedCommissionsNumber";
	}

}
