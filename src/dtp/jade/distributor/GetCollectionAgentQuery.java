package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetCollectionAgentQuery extends CyclicBehaviour {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2373555211264739523L;
	private DistributorAgent agent;

    public GetCollectionAgentQuery(DistributorAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DATA_COLLECTION_FROM_DISTRIBUTOR);
        ACLMessage message = agent.receive(template);

        

        
        if (message != null) {
        	

        	agent.sendDataToCollector();
        } else {
            block();
        }

 
    }

}
