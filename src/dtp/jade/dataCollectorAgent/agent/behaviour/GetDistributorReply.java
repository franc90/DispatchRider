package dtp.jade.dataCollectorAgent.agent.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.dataCollector.agent.DataCollectionAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetDistributorReply extends CyclicBehaviour{
	
	
    private DataCollectionAgent agent;

    public GetDistributorReply(DataCollectionAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DATA_COLLECTION_FROM_DISTRIBUTOR_REPLY);
        ACLMessage message = agent.receive(template);

        
        if (message != null) {
        	
        	agent.dataFromDistributorAgentReceived(message);
        } else {
            block();
        }

    }

}
