package measure;

import jade.core.AID;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

/**
 * Base class for all other calculators which calculate specific measure. To add
 * new MeasureCalculator (and use it), you have to: 1. create your class which
 * extends MeasureCalculator 2. It has to be in measure package 3. Now you can
 * use it in configuration (you using class name)
 */
public abstract class MeasureCalculator implements Serializable {

	private static final long serialVersionUID = 5357884320339593987L;
	protected SimInfo info;
	protected int timestamp;
	protected List<Commission> commissions;

	/**
	 * @param oldSchedules
	 *            - schedules before ST
	 * @param newSchedules
	 *            - schedules after ST
	 * @return
	 */
	public abstract Measure calculateMeasure(Map<AID, Schedule> oldSchedules,
			Map<AID, Schedule> newSchedules, AlgorithmAgentParent agent);

	public abstract String getName();

	public void setSimInfo(SimInfo info) {
		this.info = info;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public void setCommissions(List<Commission> commissions) {
		this.commissions = commissions;
	}

}
