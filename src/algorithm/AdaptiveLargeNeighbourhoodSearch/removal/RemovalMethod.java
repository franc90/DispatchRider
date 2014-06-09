package algorithm.AdaptiveLargeNeighbourhoodSearch.removal;

import jade.core.AID;

import java.util.List;
import java.util.Map;

import algorithm.Schedule;
import dtp.commission.Commission;

public interface RemovalMethod {
	
	public List<Commission> Remove(Map<AID, Schedule> holons, int searchSize);
}
