package machineLearning.dummy;

import jade.core.AID;

import java.util.Map;

import machineLearning.MLAlgorithm;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;

import org.apache.poi.ss.formula.eval.NotImplementedException;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

public class Dummy extends MLAlgorithm {

	private static final long serialVersionUID = -4616521400255496438L;

	@Override
	public GlobalConfiguration getGlobalConfiguration(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			SimInfo info, boolean exploration, AlgorithmAgentParent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<AID, HolonConfiguration> getHolonsConfiguration(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			SimInfo info, boolean exploration, AlgorithmAgentParent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAlgorithmParameters(Map<String, String> parameters) {
		// TODO Auto-generated method stub
		System.out.println(parameters);
		throw new NotImplementedException("Implement me c(;");
	}

	@Override
	public void save(String fileName, String saveFileName) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(String fileName) {
		// TODO Auto-generated method stub

	}

}
