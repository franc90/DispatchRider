package dtp.jade.dataCollectorAgent.agent.behaviour;


import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;
import dtp.jade.dataCollector.agent.DataCollectionAgent;

public class GetInfoReply extends CyclicBehaviour {

    /**
	 * 
	 */
    private static final long serialVersionUID = -8479077145084706990L;
    
    private DataCollectionAgent agent;

    public GetInfoReply(DataCollectionAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DATA_COLLECTION_FROM_INFO_REPLY);
        ACLMessage message = agent.receive(template);

        
        if (message != null) {
        	
        	agent.dataFromInfoAgentReceived(message);
        } else {
            block();
        }

    }
}
