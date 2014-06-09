package algorithm.AdaptiveLargeNeighbourhoodSearch.insert;

import jade.core.AID;

import java.util.List;
import java.util.Map;

import algorithm.Schedule;
import dtp.commission.Commission;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;

public interface InsertMethod {

	public boolean Insert(Map<AID, Schedule> holons, List<Commission> commissions, int searchSize,AlgorithmAgentParent agent, int timestamp);
}
