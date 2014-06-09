package algorithm.STLike;

import jade.core.AID;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import algorithm.Schedule;
import algorithm.AdaptiveLargeNeighbourhoodSearch.AdaptiveLargeNeighbourhoodTrading;
import algorithm.simmulatedTrading.SimmulatedTrading;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

public class ALNSTrading extends ExchangeAlgorithm  {


	private int maxFullSTDepth = 1;
	private boolean firstComplexSTResultOnly = true;
	
	private static final long serialVersionUID = 21312321312L;

	@Override
	public Map<AID, Schedule> doExchangesAfterComAdded(
			AlgorithmAgentParent agent, Set<AID> aids,
			Map<AID, Schedule> holons, AID holon, SimInfo info,  int timestamp) {

		//defaults
		int iterations = 100;
		int searchRange = 20;
		if (parameters.containsKey("iterations"))
			iterations = Integer.parseInt(parameters.get("iterations"));
		if (parameters.containsKey("search"))
			searchRange = Integer.parseInt(parameters.get("search"));
		return AdaptiveLargeNeighbourhoodTrading.LNSTrading(aids, holons, info, iterations , searchRange, agent, timestamp);
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
