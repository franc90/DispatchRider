package dtp.jade.info.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;
import dtp.jade.info.InfoAgent;

public class GetCollectionAgentQuery extends CyclicBehaviour  {

	

	    
	    private InfoAgent agent;

	    public GetCollectionAgentQuery(InfoAgent agent) {
	        this.agent = agent;
	    }

	    @Override
	    public void action() {
	        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DATA_COLLECTION_FROM_INFO);
	        ACLMessage message = agent.receive(template);

	        if (message != null) {
	        	
	        	
	        	agent.sendDataToCollector();
	        } else {
	            block();
	        }

	 
	    }
	    
	    
	    
}

	
	
	
	
	
	


