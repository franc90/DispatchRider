package algorithm.STLike;

import jade.core.AID;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import algorithm.Schedule;
import algorithm.simmulatedTrading.SimmulatedTrading;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

public class SimulatedTrading extends ExchangeAlgorithm {

	private static final long serialVersionUID = -8644579533528842902L;
	private String chooseWorstCommission = "time";
	private int maxFullSTDepth = 1;
	private boolean firstComplexSTResultOnly = true;

	@Override
	public Map<AID, Schedule> doExchangesAfterComAdded(
			AlgorithmAgentParent agent, Set<AID> aids,
			Map<AID, Schedule> holons, AID holon, SimInfo info, int timestamp) {

		if (parameters.containsKey("chooseWorstCommission"))
			chooseWorstCommission = parameters.get("chooseWorstCommission");

		return SimmulatedTrading.fullSimmulatedTrading(agent, aids, holons,
				holon, 1, info, new HashSet<Integer>(), chooseWorstCommission,
				timestamp);
	}

	@Override
	public Map<AID, Schedule> doExchangesWhenCantAddCom(
			AlgorithmAgentParent agent, Set<AID> aids,
			Map<AID, Schedule> holons, Commission com, SimInfo info,
			int timestamp) {

		if (parameters.containsKey("maxFullSTDepth"))
			maxFullSTDepth = Integer.parseInt(parameters.get("maxFullSTDepth"));
		if (parameters.containsKey("firstComplexSTResultOnly"))
			firstComplexSTResultOnly = Boolean.parseBoolean(parameters
					.get("firstComplexSTResultOnly"));

		return SimmulatedTrading.complexSimmulatedTrading(agent, aids, holons,
				com, maxFullSTDepth, new TreeSet<Integer>(), timestamp, info,
				firstComplexSTResultOnly);
	}

}
