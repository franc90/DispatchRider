package dtp.jade.transport;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class TransportAgentsMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	Map<TransportType,List<TransportAgentData>> agents;
	
	public TransportAgentsMessage(Map<TransportType,List<TransportAgentData>> agents) {
		this.agents=agents;
	}
	
	public Map<TransportType,List<TransportAgentData>> getAgents() {
		return agents;
	}
}
