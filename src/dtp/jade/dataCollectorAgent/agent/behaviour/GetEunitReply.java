package dtp.jade.dataCollectorAgent.agent.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.dataCollector.agent.DataCollectionAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetEunitReply extends CyclicBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1524797413313144460L;

    private DataCollectionAgent agent;

    public GetEunitReply(DataCollectionAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DATA_COLLECTION_FROM_EUNIT_REPLY);
        ACLMessage message = agent.receive(template);



        
        if (message != null) {

        	
        	agent.dataFromEunitAgentReceived(message);
        } else {
            block();
        }

    }

}
