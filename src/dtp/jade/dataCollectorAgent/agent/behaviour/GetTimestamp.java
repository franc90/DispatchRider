package dtp.jade.dataCollectorAgent.agent.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;
import dtp.jade.dataCollector.agent.DataCollectionAgent;

public class GetTimestamp extends CyclicBehaviour{
	
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -7257624610478249253L;
	private DataCollectionAgent agent;

    public GetTimestamp(DataCollectionAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DATA_COLLECTION_FROM_DISTRIBUTOR_TIMESTAMP);
        ACLMessage message = agent.receive(template);

        
        if (message != null) {
        	
        	agent.timestampChangeNotificationReceived(message);
        } else {
            block();
        }

    }


}
