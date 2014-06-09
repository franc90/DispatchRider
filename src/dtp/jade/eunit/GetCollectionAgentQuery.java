package dtp.jade.eunit;

import dtp.jade.CommunicationHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class GetCollectionAgentQuery extends CyclicBehaviour  {

	

	    
	    /**
	 * 
	 */
	private static final long serialVersionUID = -280585451087838648L;
		private ExecutionUnitAgent agent;

	    public GetCollectionAgentQuery(ExecutionUnitAgent agent) {
	        this.agent = agent;
	    }

	    @Override
	    public void action() {
	        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DATA_COLLECTION_FROM_EUNIT);
	        ACLMessage message = agent.receive(template);

	        

	        
	        if (message != null) {
	        	

	        	agent.sendDataToCollector();
	        } else {
	            block();
	        }

	 
	    }
	    
	    
	    
}

	
	